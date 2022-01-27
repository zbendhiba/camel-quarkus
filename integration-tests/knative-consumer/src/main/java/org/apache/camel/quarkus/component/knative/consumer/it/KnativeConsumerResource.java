package org.apache.camel.quarkus.component.knative.consumer.it;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.CamelContext;
import org.apache.camel.component.knative.KnativeComponent;

@Path("/test")
@ApplicationScoped
public class KnativeConsumerResource {
    @Inject
    CamelContext context;

    @GET
    @Path("/inspect")
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject inspect() {
        var component = context.getComponent("knative", KnativeComponent.class);
        var builder = Json.createObjectBuilder();

        if (component.getProducerFactory() != null) {
            builder.add("producer-factory", component.getProducerFactory().getClass().getName());
        }
        if (component.getConsumerFactory() != null) {
            builder.add("consumer-factory", component.getConsumerFactory().getClass().getName());
        }

        return builder.build();
    }
}
