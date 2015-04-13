/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.gearpump.streaming.state.example

import com.typesafe.config.ConfigFactory
import org.apache.gearpump.cluster.UserConfig
import org.apache.gearpump.cluster.client.ClientContext
import org.apache.gearpump.cluster.main.{ArgumentsParser, CLIOption, ParseResult}
import org.apache.gearpump.partitioner.HashPartitioner
import org.apache.gearpump.streaming.kafka.lib.KafkaConfig
import org.apache.gearpump.streaming.state.api.Window
import org.apache.gearpump.streaming.state.example.processor.{WindowAverageProcessor, GenNumProcessor}
import org.apache.gearpump.streaming.state.internal.impl.HadoopCheckpointStoreFactory
import org.apache.gearpump.streaming.state.util.StateConfig
import org.apache.gearpump.streaming.{Processor, StreamApplication}
import org.apache.gearpump.util.Graph
import org.apache.gearpump.util.Graph.Node

import scala.concurrent.duration._

object WindowAverage extends App with ArgumentsParser {

  override val options: Array[(String, CLIOption[Any])] = Array(
    "gen" -> CLIOption("<how many gen tasks>", required = false, defaultValue = Some(1)),
    "count" -> CLIOption("<how mange count tasks", required = false, defaultValue = Some(1)),
    "window" -> CLIOption("<window length in milliseconds>", required = false , defaultValue = Some(5000))
  )

  def application(config: ParseResult) : StreamApplication = {

    val windowSize = Duration(config.getInt("window"), MILLISECONDS)
    val kafkaConfig = KafkaConfig(ConfigFactory.parseResources("kafka.conf"))
    val userConfig = UserConfig.empty
      .withValue[Window](StateConfig.WINDOW, Window(windowSize))
      .withLong(StateConfig.CHECKPOINT_INTERVAL_DEFAULT, 1000)
      .withString(StateConfig.CHECKPOINT_STORE_FACTORY, classOf[HadoopCheckpointStoreFactory].getName)
      .withString(StateConfig.HDFS_FS, "hdfs://localhost:9000")
      .withValue(KafkaConfig.NAME, kafkaConfig)
    val gen = Processor[GenNumProcessor](config.getInt("gen"))
    val count = Processor[WindowAverageProcessor](config.getInt("count"))
    val partitioner = new HashPartitioner()
    val app = StreamApplication("WindowAverage", Graph(gen ~ partitioner ~> count), userConfig)
    app
  }

  val config = parse(args)
  val context = ClientContext()

  implicit val system = context.system
  val appId = context.submit(application(config))
  context.close()
}
