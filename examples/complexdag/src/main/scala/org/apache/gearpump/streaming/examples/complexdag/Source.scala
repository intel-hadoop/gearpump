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

package org.apache.gearpump.streaming.examples.complexdag

import org.apache.gearpump.Message
import org.apache.gearpump.cluster.UserConfig
import org.apache.gearpump.streaming.task.{StartTime, TaskActor, TaskContext}

class Source(taskContext: TaskContext, conf: UserConfig) extends TaskActor(taskContext, conf) {

  override def onStart(startTime: StartTime): Unit = {
    self ! Message("start")
  }

  override def onNext(msg: Message): Unit = {
    val list = List(getClass.getCanonicalName)
    output(new Message(list, System.currentTimeMillis))
    self ! Message("continue", System.currentTimeMillis())
  }
}

