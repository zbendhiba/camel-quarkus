package org.apache.camel.quarkus.component.kafka.oauth;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@ApplicationScoped
@Path("example")
public class KafkaOauth2Resource {
    @GET
    public String hello(){
        return "hello";
    }
}
