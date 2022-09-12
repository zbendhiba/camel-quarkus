package org.apache.camel.quarkus.component.knative.consumer.it;

import org.apache.camel.builder.RouteBuilder;

public class Route extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("knative:channel/channel-test")
                .to("seda:queue-channel");

        from("knative:event/broker-test")
                .to("seda:queue-broker");

    }
}
