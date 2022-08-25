package org.apache.camel.quarkus.component.knative.consumer.it;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.apache.camel.ConsumerTemplate;

@ApplicationScoped
@Path("seda")
public class Resource {

    @Inject
    ConsumerTemplate consumerTemplate;

    @GET
    public String hello() {
        Object obj = consumerTemplate.receiveBody("seda:queue", 1000);
        boolean isNull = obj == null;
        System.out.println("obj is " + isNull);
        return "hello";
    }
}
