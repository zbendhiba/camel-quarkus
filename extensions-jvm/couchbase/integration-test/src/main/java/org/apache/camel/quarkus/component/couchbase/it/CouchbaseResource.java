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
package org.apache.camel.quarkus.component.couchbase.it;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.couchbase.client.java.kv.GetResult;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.couchbase.CouchbaseConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import static org.apache.camel.component.couchbase.CouchbaseConstants.COUCHBASE_DELETE;
import static org.apache.camel.component.couchbase.CouchbaseConstants.COUCHBASE_GET;

@Path("/couchbase")
@ApplicationScoped
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.TEXT_PLAIN)
public class CouchbaseResource {

    private static final Logger LOG = Logger.getLogger(CouchbaseResource.class);

    @ConfigProperty(name = "couchbase.connection.uri")
    String connectionUri;

    @ConfigProperty(name = "couchbase.bucket.name")
    String bucketName;

    @Inject
    ProducerTemplate producerTemplate;

    @Inject
    ConsumerTemplate consumerTemplate;

    @PUT
    @Path("id/{id}")
    public boolean insert(@PathParam("id") String id, String msg) {
        LOG.infof("inserting message %msg with id %id");
        return producerTemplate.requestBodyAndHeader(connectionUri, msg, CouchbaseConstants.HEADER_ID, id, Boolean.class);
    }

    @GET
    @Path("{id}")
    public String getById(@PathParam("id") String id) {
        LOG.infof("Getting object with id : %s");
        GetResult result = producerTemplate.requestBodyAndHeader(String.format("%s&operation=%s", connectionUri, COUCHBASE_GET),
                null, CouchbaseConstants.HEADER_ID, id, GetResult.class);
        return result != null ? result.contentAs(String.class) : null;
    }

    @DELETE
    @Path("{id}")
    public boolean delete(@PathParam("id") String id) {
        LOG.infof("Deleting object with id : %s");
        producerTemplate.sendBodyAndHeader(String.format("%s&operation=%s&queryTimeout=%s", connectionUri, COUCHBASE_DELETE, 5000), null,
                CouchbaseConstants.HEADER_ID, id);
        return true;
    }

    @Path("/consumer")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String consumeDocuments() {
        return consumerTemplate.receiveBody(String.format("%s&designDocumentName=%s&viewName=%s&limit=10", connectionUri, bucketName,
                bucketName), 5000, String.class);
    }
}
