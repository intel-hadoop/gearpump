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

package org.apache.gearpump.streaming.transaction.storage.api

trait KeyValueStore[K, V] {
  def get(key: K): Option[V]

  def put(key: K, value: V): Option[V]

  def putAll(kvs: List[(K, V)]): Unit

  def delete(key: K): Option[V]

  def flush(): Unit

  def close(): Unit
}

trait KeyValueSerDe[K, V] {
  def toBytes(kv: (K, V)): Array[Byte]
  def fromBytes(bytes: Array[Byte]): (K, V)
}
