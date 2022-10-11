package org.apache.camel.quarkus.component.platform.http.proxy.ssl.it;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @Named("sslContextParameters")
    public SSLContextParameters sslContextParameters() {
        KeyStoreParameters ksp = new KeyStoreParameters();
        ksp.setResource("ssl/keystore.jks");
        ksp.setPassword("changeit");

        KeyManagersParameters kmp = new KeyManagersParameters();
        kmp.setKeyStore(ksp);
        kmp.setKeyPassword("changeit");

        SSLContextParameters sslContextParameters = new SSLContextParameters();
        sslContextParameters.setKeyManagers(kmp);

        return sslContextParameters;
    }

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
                .toD("http://${headers." + Exchange.HTTP_HOST
                        + "}?bridgeEndpoint=true&sslContextParameters=#sslContextParameters\"");
    }
}
