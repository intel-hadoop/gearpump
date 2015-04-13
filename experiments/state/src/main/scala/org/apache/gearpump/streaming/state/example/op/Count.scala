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

package org.apache.gearpump.streaming.state.example.op

import com.twitter.bijection.{Injection, Bijection}
import org.apache.gearpump.Message
import org.apache.gearpump.streaming.state.api.StateOp

class Count extends StateOp[Long] {
  /**
   * count is inited with 0
   */
  override def init: Long = 0

  /**
   * update count on new message
   */
  override def update(msg: Message, count: Long): Long = {
    count + 1
  }

  override def serialize(count: Long): Array[Byte] = {
    Injection[Long, Array[Byte]](count)
  }

  override def deserialize(bytes: Array[Byte]): Long = {
    Injection.invert[Long, Array[Byte]](bytes)
      .getOrElse(throw new RuntimeException("fail to deserialize bytes to long"))
  }
}
