package org.apache.camel.quarkus.component.couchbase.it;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.couchbase.CouchbaseConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "couchbase.connection.url", defaultValue = "blabla")
    String connectionUrl;

    @Override
    public void configure() throws Exception {
        from("direct:start").setHeader(CouchbaseConstants.HEADER_ID, constant("SimpleDocument_1"))
                .to(connectionUrl)
                .to("mock:result");
    }
}
