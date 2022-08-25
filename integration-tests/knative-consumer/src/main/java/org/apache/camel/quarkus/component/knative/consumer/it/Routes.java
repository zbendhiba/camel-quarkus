package org.apache.camel.quarkus.component.knative.consumer.it;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.knative.spi.KnativeEnvironment;

import static org.apache.camel.component.knative.spi.KnativeEnvironment.mandatoryLoadFromResource;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @Inject
    CamelContext context;

    @Named("knativeenv")
    KnativeEnvironment environment() throws IOException {
        String path = "classpath:/environment_classic.json";
        return mandatoryLoadFromResource(context, path);
    }

    @Override
    public void configure() throws Exception {

        from("knative:channel/feedback?environment=#knativeenv")
                .log("********* Receiving message ${body}")
                .to("seda:queue");

    }
}
