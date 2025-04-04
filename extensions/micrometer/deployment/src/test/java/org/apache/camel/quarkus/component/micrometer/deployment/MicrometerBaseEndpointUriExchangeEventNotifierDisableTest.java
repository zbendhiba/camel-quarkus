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
package org.apache.camel.quarkus.component.micrometer.deployment;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Optional;
import java.util.Properties;

import io.quarkus.test.QuarkusUnitTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import org.apache.camel.component.micrometer.eventnotifier.MicrometerExchangeEventNotifier;
import org.apache.camel.spi.EventNotifier;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MicrometerBaseEndpointUriExchangeEventNotifierDisableTest {
    @RegisterExtension
    static final QuarkusUnitTest CONFIG = new QuarkusUnitTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource(applicationProperties(), "application.properties"));

    @Inject
    CamelContext context;

    @Test
    public void testBaseEndpointURIDisabled() {
        Optional<EventNotifier> optionalExchangeEventNotifier = context.getManagementStrategy()
                .getEventNotifiers()
                .stream()
                .filter(eventNotifier -> eventNotifier.getClass().equals(MicrometerExchangeEventNotifier.class))
                .findFirst();
        assertTrue(optionalExchangeEventNotifier.isPresent());

        MicrometerExchangeEventNotifier eventNotifier = (MicrometerExchangeEventNotifier) optionalExchangeEventNotifier.get();
        assertFalse(eventNotifier.isBaseEndpointURI());
    }

    public static Asset applicationProperties() {
        Writer writer = new StringWriter();

        Properties props = new Properties();
        props.setProperty("quarkus.camel.metrics.base-endpoint-uri-exchange-event-notifier", "false");

        try {
            props.store(writer, "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new StringAsset(writer.toString());
    }
}
