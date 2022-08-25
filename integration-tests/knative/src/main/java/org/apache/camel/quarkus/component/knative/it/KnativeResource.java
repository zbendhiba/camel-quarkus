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
package org.apache.camel.quarkus.component.knative.it;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.knative.KnativeComponent;
import org.apache.camel.component.knative.spi.KnativeEnvironment;

import static org.apache.camel.component.knative.spi.KnativeEnvironment.mandatoryLoadFromResource;

@Path("/knative")
@ApplicationScoped
public class KnativeResource {

    @Inject
    CamelContext context;

    @Inject
    ProducerTemplate producerTemplate;

    @Inject
    ConsumerTemplate consumerTemplate;

    @Named("knativeenv")
    KnativeEnvironment environment() throws IOException {
        String path = "classpath:/environment_classic.json";
        return mandatoryLoadFromResource(context, path);
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello World source and sink!";
    }

    @GET
    @Path("/inspect")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject inspect() {
        return Json.createObjectBuilder()
                .add("producer-factory",
                        context.getComponent("knative", KnativeComponent.class).getProducerFactory().getClass().getName())
                .add("consumer-factory",
                        context.getComponent("knative", KnativeComponent.class).getConsumerFactory().getClass().getName())
                .build();
    }

    @GET
    @Path("/send/{msg}")
    public Response sendMessageToChannel(@PathParam("msg") String message) throws IOException {
        producerTemplate.sendBody("knative:channel/feedback?environment=#knativeenv", message);
        System.out.println(String.format("Sending %s is okay", message));
        return Response.ok().build();
    }

    @Path("/read")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String receiveFromChannel() throws Exception {
        return consumerTemplate.receiveBody("knative:channel/feedback?environment=#knativeenv", 10000, String.class);
    }
}
