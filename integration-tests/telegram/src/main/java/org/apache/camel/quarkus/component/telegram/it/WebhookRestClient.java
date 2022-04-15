package org.apache.camel.quarkus.component.telegram.it;

import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/webhook")
@RegisterRestClient
@ApplicationScoped
public interface WebhookRestClient {

    @POST
    InputStream send(InputStream message);
}
