package org.apache.camel.quarkus.component.platform.http.proxy.ssl.it;

import javax.enterprise.context.ApplicationScoped;

import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @ConfigProperty(name = "platform.origin.url", defaultValue = "TODO")
    String url;


    @Override
    public void configure() throws Exception {


        from("platform-http:proxy")
                /*  .toD("${headers." + Exchange.HTTP_SCHEME + "}://" +
                          "${headers." + Exchange.HTTP_HOST + "}:" +
                          "${headers." + Exchange.HTTP_PORT + "}" +
                          "${headers." + Exchange.HTTP_PATH + "}?bridgeEndpoint=true");*/
                .log("header is :: ${headers.CamelHttpHost}")
                .log("Im here with body :: ${body}")
                //.transform().constant("hello");
                .to(url
                        + "?sslContextParameters=#sslContextParameters&x509HostnameVerifier=#x509HostnameVerifier&bridgeEndpoint=true");
        
        
               /* from("platform-http:/hello")
                        //.log("url is "+)
                        .toD(url
                                + "?bridgeEndpoint=true&sslContextParameters=#ssw");*/

    }
}
