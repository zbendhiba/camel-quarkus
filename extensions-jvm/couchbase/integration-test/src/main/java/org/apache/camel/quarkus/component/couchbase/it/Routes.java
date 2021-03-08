package org.apache.camel.quarkus.component.couchbase.it;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "couchbase.connection.uri")
    String connectionUri;

    @ConfigProperty(name = "couchbase.bucket.name")
    String bucketName;

    @Override
    public void configure() throws Exception {
        // insert with auto-insert ids
        /*   from("direct:auto-insert")
                .to(String.format("%s&autoStartIdForInserts=true&startingIdForInsertsFrom=1000", connectionUri));*/

        // consumer limited to 10 messages only
      /*  from(String.format("%s&designDocumentName=%s&viewName=%s&limit=10&collection=collection3", connectionUri, bucketName,
                bucketName))
                        .log("message received")
                        .to("mock:result");*/
    }
}
