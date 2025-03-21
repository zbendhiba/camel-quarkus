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
package org.apache.camel.quarkus.component.kamelet.it;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.FluentProducerTemplate;

@Path("/kamelet")
public class KameletResource {

    @Inject
    CamelContext camelContext;

    @Inject
    FluentProducerTemplate fluentProducerTemplate;

    @Inject
    ConsumerTemplate consumerTemplate;

    @Path("/produce")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String produceToKamelet(String message) throws Exception {
        return fluentProducerTemplate.toF("kamelet:setBody/test?bodyValue=%s", message).request(String.class);
    }

    @Path("/consume")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Integer consumeFromKamelet() throws Exception {
        return consumerTemplate.receiveBody("kamelet:tick", 10000, Integer.class);
    }

    @Path("/property")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String bodyFromProperty() throws Exception {
        return fluentProducerTemplate.to("kamelet:setBodyFromProperties").request(String.class);
    }

    @Path("/chain")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String kameletChain(String message) throws Exception {
        return fluentProducerTemplate.to("direct:chain").withBody(message).request(String.class);
    }

    @Path("/invoke/{name}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String invoke(@PathParam("name") String name, String message) throws Exception {
        return fluentProducerTemplate.toF("kamelet:%s", name).withBody(message).request(String.class);
    }

    @Path("/locationAtRuntime/{name}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String kameletLocationAtRuntime(@PathParam("name") String name) {
        return fluentProducerTemplate.to("direct:kamelet-location-at-runtime").withBody(name).request(String.class);
    }

    @Path("/greeting")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String greeting() {
        return consumerTemplate.receiveBody("seda:greeting", 10000, String.class);
    }

    @Path("/pipe")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String pipe() {
        return consumerTemplate.receiveBody("seda:greetingFromProperty", 10000, String.class);
    }

    @Path("/injector")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String injector() {
        return consumerTemplate.receiveBody("seda:injector", 10000, String.class);
    }

    @Path("/custom")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String custom() {
        return fluentProducerTemplate.to("direct:custom").request(String.class);
    }
}
