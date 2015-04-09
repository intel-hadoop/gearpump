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
import org.apache.gearpump.streaming.task.{StartTime, Task, TaskContext}

import scala.collection.mutable

class Sink(taskContext: TaskContext, conf: UserConfig) extends Task(taskContext, conf) {

  var list = mutable.MutableList[String]()

  override def onStart(startTime: StartTime): Unit = {
    list += getClass.getCanonicalName
  }

  override def onNext(msg: Message): Unit = {
    val l = msg.msg.asInstanceOf[Array[String]]
    list.size match {
      case 1 =>
        l.foreach(f => {
          list += f
        })
      case _ =>
    }
  }

}
