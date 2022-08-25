package org.apache.camel.quarkus.component.knative.it;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @Inject
    CamelContext context;

    @Override
    public void configure() throws Exception {

        from("knative:channel/feedback?environment=#knativeenv")
                .log("********* Receiving message ${body}");

    }
}
