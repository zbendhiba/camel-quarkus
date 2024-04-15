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
package org.apache.camel.quarkus.component.langchain.chat.it;

import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.eclipse.microprofile.config.ConfigProvider;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class ollamaTestResource<T extends GenericContainer> implements QuarkusTestResourceLifecycleManager {

    private static final int OLLAMA_PORT = 11434;
    private static final String OLLAMA_IMAGE = "docker.io/ollama/ollama:0.1.27";
    private static final String OLLAMA_MODEL= orca-mini;

    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        try {
            container = new GenericContainer<>(OLLAMA_IMAGE)
                    .withExposedPorts(OLLAMA_PORT)
                    .waitingFor(Wait.forListeningPort());
            container.start();

            String ollamaHost = container.getHost();
            int ollamaPort = container.getMappedPort(OLLAMA_PORT);

            return Map.of(
                    "ollamaHost", ollamaHost,
                    "ollamaPort", Integer.toString(OLLAMA_PORT),
                    "ollama.model", ollamaModel
                    );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            if (container != null) {
                container.stop();
            }
        } catch (Exception ex) {
            LOG.error("An issue occured while stopping the Container", ex);
        }
    }

}