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
package org.apache.camel.quarkus.component.groovy.it;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import org.apache.camel.ProducerTemplate;

@Path("/groovy")
@ApplicationScoped
public class GroovyResource {

    @Inject
    ProducerTemplate producerTemplate;

    @POST
    @Path("/predicate")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String predicate(String message) {
        return producerTemplate.requestBody("direct:predicate", Integer.valueOf(message), String.class);
    }

    @Path("/route/{route}")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String route(String statement, @PathParam("route") String route, @Context UriInfo uriInfo) {
        final Map<String, Object> headers = uriInfo.getQueryParameters().entrySet().stream()
                .map(e -> new AbstractMap.SimpleImmutableEntry<String, Object>(e.getKey(), e.getValue().get(0)))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        return producerTemplate.requestBodyAndHeaders("direct:" + route, statement, headers, String.class);
    }

    @POST
    @Path("/direct/{id}")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String direct(@PathParam("id") String id, String message) {
        return producerTemplate.requestBody("direct:" + id, message, String.class);
    }

    @Path("/contextValidation")
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String route() {
        final Map<String, Object> headers = Map.of("header1", (Object) "value_of_header1");
        return producerTemplate.requestBodyAndHeaders("direct:contextValidation", "body", headers, String.class);
    }

    @Path("results/validateExchangeProperties")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Object getMessagesOfWithoutSsl() throws InterruptedException {
        return producerTemplate.requestBody("direct:validateExchangeProperty", "body", String.class);
    }
}
