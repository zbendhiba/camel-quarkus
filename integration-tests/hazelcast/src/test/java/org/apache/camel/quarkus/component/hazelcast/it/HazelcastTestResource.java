/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.hazelcast.it;

import java.util.Map;

import org.apache.camel.quarkus.testcontainers.ContainerResourceLifecycleManager;
import org.apache.camel.util.CollectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class HazelcastTestResource implements ContainerResourceLifecycleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HazelcastTestResource.class);
    private static final int HAZELCAST_PORT = 5701;
    private static final int HAZELCAST_PORT2 = 5702;
    private static final String HAZELCAST_IMAGE = "hazelcast/hazelcast:4.1.1";
    private GenericContainer container;
    private static GenericContainer container2;

    @Override
    public Map<String, String> start() {
        container = createContainer(HAZELCAST_PORT);
        container.start();
        return CollectionHelper.mapOf(
                "quarkus.hazelcast-client.cluster-members",
                String.format("localhost:%s", container.getMappedPort(HAZELCAST_PORT)));
    }

    @Override
    public void stop() {
        if (container != null) {

            container.stop();
        }
        if (container2 != null) {

            container2.stop();
        }
    }

    /**
     * this is used to test new instance in the same cluster
     */
    public static void addMemberToCluster() {
        container2 = createContainer(HAZELCAST_PORT2);
        container2.start();
    }

    private static GenericContainer createContainer(int port) {
        GenericContainer container = new GenericContainer(HAZELCAST_IMAGE)
                .withExposedPorts(port)
                .withLogConsumer(new Slf4jLogConsumer(LOGGER))
                .waitingFor(Wait.forLogMessage(".*is STARTED.*", 1));
        return container;
    }

}
