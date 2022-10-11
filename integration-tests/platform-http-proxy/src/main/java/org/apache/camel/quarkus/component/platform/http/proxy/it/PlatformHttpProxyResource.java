package org.apache.camel.quarkus.component.platform.http.proxy.it;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
@Path("/platform-http-proxy")
public class PlatformHttpProxyResource {

    @ConfigProperty(name = "platform.origin.url", defaultValue = "TODO")
    String url;

    @GET
    public String getUrl() {
        return url;
    }

}
