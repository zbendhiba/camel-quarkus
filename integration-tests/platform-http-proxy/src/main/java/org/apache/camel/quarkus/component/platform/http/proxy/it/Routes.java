package org.apache.camel.quarkus.component.platform.http.proxy.it;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("platform-http:proxy")
                .log("header is :: ${headers.CamelHttpHost}")
                .toD("http://${headers." + Exchange.HTTP_HOST + "}?bridgeEndpoint=true");
    }
}
