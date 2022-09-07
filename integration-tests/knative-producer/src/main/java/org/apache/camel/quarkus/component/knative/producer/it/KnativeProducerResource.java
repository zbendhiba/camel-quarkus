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
package org.apache.camel.quarkus.component.knative.producer.it;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.cloudevents.CloudEvent;
import org.apache.camel.component.knative.KnativeComponent;

@Path("/knative-producer")
@ApplicationScoped
public class KnativeProducerResource {
    @Inject
    CamelContext context;

    @Inject
    ProducerTemplate producerTemplate;

    @GET
    @Path("/inspect")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject inspect() {
        return Json.createObjectBuilder().add("producer-factory", context.getComponent("knative", KnativeComponent.class).getProducerFactory().getClass().getName()).add("consumer-factory", context.getComponent("knative", KnativeComponent.class).getConsumerFactory().getClass().getName()).build();
    }

    @GET
    @Path("/channel/send/{msg}")
    public Response sendMessageToChannel(@PathParam("msg") String message) throws IOException {
        producerTemplate.sendBodyAndHeader("knative:channel/channel-test", message, CloudEvent.CAMEL_CLOUD_EVENT_SOURCE, "channelTest");
        return Response.ok().build();
    }

    @GET
    @Path("/event/send/{msg}")
    public Response sendMessageToBroker(@PathParam("msg") String message) throws IOException {
        producerTemplate.sendBodyAndHeader("knative:event/broker-test", message, CloudEvent.CAMEL_CLOUD_EVENT_SOURCE, "eventTest");
        return Response.ok().build();
    }
}
