package org.apache.camel.quarkus.component.couchbase.it;

import javax.enterprise.context.ApplicationScoped;

import com.couchbase.client.java.kv.GetResult;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.couchbase.CouchbaseConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import static org.apache.camel.component.couchbase.CouchbaseConstants.COUCHBASE_DELETE;
import static org.apache.camel.component.couchbase.CouchbaseConstants.COUCHBASE_GET;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "couchbase.connection.uri")
    String connectionUri;

    @ConfigProperty(name = "couchbase.bucket.name")
    String bucketName;


    @Override
    public void configure() throws Exception {

        // producer to insert a document
        from("direct:insert")
                .to(connectionUri);

        // producer to get a document
        from("direct:get")
                .to(String.format("%s&operation=%s&queryTimeout=%s", connectionUri, COUCHBASE_GET, 10000));

        // producer to delete a document
        from("direct:delete")
                .to(String.format("%s&operation=%s&queryTimeout=%s", connectionUri, COUCHBASE_DELETE, 10000));

        // consumer limited to 10 messages only
        from(String.format("%s&designDocumentName=%s&viewName=%s&limit=10", connectionUri, bucketName, bucketName))
                .log("****************************Message is received")
                .to("mock:result");
    }
}
