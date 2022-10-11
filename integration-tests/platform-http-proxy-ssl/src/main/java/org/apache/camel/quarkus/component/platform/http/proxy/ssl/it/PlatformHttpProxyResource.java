package org.apache.camel.quarkus.component.platform.http.proxy.ssl.it;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.jsse.KeyManagersParameters;
import org.apache.camel.support.jsse.KeyStoreParameters;
import org.apache.camel.support.jsse.SSLContextParameters;
import org.apache.camel.support.jsse.TrustManagersParameters;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Path("/platform-http-proxy")
public class PlatformHttpProxyResource {

    @ConfigProperty(name = "platform.origin.url", defaultValue = "TODO")
    String url;

    @Inject
    ProducerTemplate producerTemplate;

    @Inject
    CamelContext context;

    @GET
    public String getUrl() {
        return url;
    }

    @Path("/hey")
    @GET
    public String hello() {
        String uri = String.format("%s?sslContextParameters=#sslContextParameters&x509HostnameVerifier=#x509HostnameVerifier",
                url);
        System.out.println("uri ::: " + uri);
        return producerTemplate.requestBody(uri, null, String.class);
    }

    @Named
    @Produces
    @Singleton
    public SSLContextParameters sslContextParameters() {
        SSLContextParameters sslContextParameters = new SSLContextParameters();

        KeyManagersParameters keyManagersParameters = new KeyManagersParameters();
        KeyStoreParameters keyStore = new KeyStoreParameters();
        keyStore.setPassword("changeit");
        keyStore.setResource("ssl/keystore.p12");
        keyManagersParameters.setKeyPassword("changeit");
        keyManagersParameters.setKeyStore(keyStore);
        sslContextParameters.setKeyManagers(keyManagersParameters);

        KeyStoreParameters truststoreParameters = new KeyStoreParameters();
        truststoreParameters.setResource("ssl/keystore.p12");
        truststoreParameters.setPassword("changeit");

        TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
        trustManagersParameters.setKeyStore(truststoreParameters);
        sslContextParameters.setTrustManagers(trustManagersParameters);

        return sslContextParameters;
    }

    @Named
    public NoopHostnameVerifier x509HostnameVerifier() {
        return NoopHostnameVerifier.INSTANCE;
    }

}
