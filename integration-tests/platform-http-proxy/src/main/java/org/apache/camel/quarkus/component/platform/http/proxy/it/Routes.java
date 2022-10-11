package org.apache.camel.quarkus.component.platform.http.proxy.it;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Routes extends RouteBuilder {



    @Override
    public void configure() throws Exception {
        from("platform-http:proxy")
                /*  .toD("${headers." + Exchange.HTTP_SCHEME + "}://" +
                          "${headers." + Exchange.HTTP_HOST + "}:" +
                          "${headers." + Exchange.HTTP_PORT + "}" +
                          "${headers." + Exchange.HTTP_PATH + "}?bridgeEndpoint=true");*/
                .toD("http://${headers." + Exchange.HTTP_HOST + "}?bridgeEndpoint=true");
    }
}
