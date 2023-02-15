package org.apache.camel.quarkus.component.knative.sink.binding;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import org.apache.camel.CamelContext;
import org.apache.camel.component.knative.spi.Knative;
import org.apache.camel.component.knative.spi.KnativeResource;

@Recorder
public class KnativeSinkBindingRecorder {

    public RuntimeValue<KnativeResource> createKnativeSinkBindingResource(CamelContext camelContext,
            KnativeSinkBindingConfig config) {
        // create a synthetic service definition to target the Knative Sink url
        config.sinkUrl.ifPresent(sinkUrl -> {
            KnativeResource resource = new KnativeResource();
            resource.setEndpointKind(Knative.EndpointKind.sink);
            resource.setUrl(sinkUrl);
            resource.setType(config.type);
            resource.setName(config.name);
            config.apiVersion.ifPresent(resource::setObjectApiVersion);
            if (Knative.Type.event == config.type) {
                resource.setObjectName(config.name);
            }

            if (config.ceOverride != null) {
                config.ceOverride.forEach(resource::addCeOverride);
            }
            camelContext.getRegistry().bind(config.name, resource);
        });
        return new RuntimeValue<>(new KnativeResource());
    }
}
