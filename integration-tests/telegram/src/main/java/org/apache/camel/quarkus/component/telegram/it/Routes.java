package org.apache.camel.quarkus.component.telegram.it;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class Routes extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("webhook:telegram:bots?webhookAutoRegister=false")
                .convertBodyTo(String.class)
                .to("mock:webhook");
    }
}
