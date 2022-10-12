package org.apache.camel.quarkus.component.platform.http.proxy.ssl.it;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "platform.origin.url", defaultValue = "TODO")
    String url;


    @Override
    public void configure() throws Exception {
        from("platform-http:proxy")
                .log("header is :: ${headers.CamelHttpHost}")
                .log("Im here with body :: ${body}")
                .toD("https://${headers." + Exchange.HTTP_HOST + "}?bridgeEndpoint=true&sslContextParameters=#sslContextParameters&x509HostnameVerifier=#x509HostnameVerifier");
    }
}
