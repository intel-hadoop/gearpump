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

package io.gearpump.streaming

import io.gearpump.esotericsoftware.kryo.io.{Input, Output}
import io.gearpump.esotericsoftware.kryo.{Kryo, Serializer}
import io.gearpump.Message
import io.gearpump.serializer.SerializationDelegate
import io.gearpump.serializer.IMessageSerializer
import io.gearpump.streaming.task._

class MessageSerializer(override val delegate: Option[SerializationDelegate]) extends IMessageSerializer[Message]{
  override def write(kryo: Kryo, output: Output, obj: Message) = {
    output.writeLong(obj.timestamp)
    if(delegate.nonEmpty){
      val bytes = delegate.get.serialize(obj.msg)
      output.writeInt(bytes.length)
      output.write(bytes)
    } else {
      kryo.writeClassAndObject(output, obj.msg)
    }
  }

  override def read(kryo: Kryo, input: Input, typ: Class[Message]): Message = {
    val timeStamp = input.readLong()
    val msg = if(delegate.nonEmpty) {
      val length = input.readInt()
      delegate.get.deserialize(input.readBytes(length))
    } else {
      kryo.readClassAndObject(input)
    }
    new Message(msg, timeStamp)
  }
}

class TaskIdSerializer extends Serializer[TaskId] {
  override def write(kryo: Kryo, output: Output, obj: TaskId) = {
    output.writeInt(obj.processorId)
    output.writeInt(obj.index)
  }

  override def read(kryo: Kryo, input: Input, typ: Class[TaskId]): TaskId = {
    val processorId = input.readInt()
    val index = input.readInt()
    new TaskId(processorId, index)
  }
}

class AckRequestSerializer extends Serializer[AckRequest] {
  val taskIdSerialzer = new TaskIdSerializer()

  override def write(kryo: Kryo, output: Output, obj: AckRequest) = {
    taskIdSerialzer.write(kryo, output, obj.taskId)
    output.writeShort(obj.seq)
    output.writeInt(obj.sessionId)
  }

  override def read(kryo: Kryo, input: Input, typ: Class[AckRequest]): AckRequest = {
    val taskId = taskIdSerialzer.read(kryo, input, classOf[TaskId])
    val seq = input.readShort()
    val sessionId = input.readInt()
    new AckRequest(taskId, seq, sessionId)
  }
}

class InitialAckRequestSerializer extends Serializer[InitialAckRequest] {
  val taskIdSerialzer = new TaskIdSerializer()

  override def write(kryo: Kryo, output: Output, obj: InitialAckRequest) = {
    taskIdSerialzer.write(kryo, output, obj.taskId)
    output.writeInt(obj.sessionId)
  }

  override def read(kryo: Kryo, input: Input, typ: Class[InitialAckRequest]): InitialAckRequest = {
    val taskId = taskIdSerialzer.read(kryo, input, classOf[TaskId])
    val sessionId = input.readInt()
    new InitialAckRequest(taskId, sessionId)
  }
}

class AckSerializer extends Serializer[Ack] {
  val taskIdSerialzer = new TaskIdSerializer()

  override def write(kryo: Kryo, output: Output, obj: Ack) = {
    taskIdSerialzer.write(kryo, output, obj.taskId)
    output.writeShort(obj.seq)
    output.writeShort(obj.actualReceivedNum)
    output.writeInt(obj.sessionId)
  }

  override def read(kryo: Kryo, input: Input, typ: Class[Ack]): Ack = {
    val taskId = taskIdSerialzer.read(kryo, input, classOf[TaskId])
    val seq = input.readShort()
    val actualReceivedNum = input.readShort()
    val sessionId = input.readInt()
    new Ack(taskId, seq, actualReceivedNum, sessionId)
  }
}

class LatencyProbeSerializer extends Serializer[LatencyProbe] {

  override def write(kryo: Kryo, output: Output, obj: LatencyProbe) = {
    output.writeLong(obj.timestamp)
  }

  override def read(kryo: Kryo, input: Input, typ: Class[LatencyProbe]): LatencyProbe = {
    val timestamp = input.readLong()
    new LatencyProbe(timestamp)
  }
}