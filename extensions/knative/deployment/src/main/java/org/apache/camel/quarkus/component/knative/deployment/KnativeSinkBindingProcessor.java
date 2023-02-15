package org.apache.camel.quarkus.component.knative.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.Consume;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import org.apache.camel.CamelContext;
import org.apache.camel.component.knative.KnativeComponent;
import org.apache.camel.component.knative.spi.KnativeResource;
import org.apache.camel.quarkus.component.knative.sink.binding.KnativeSinkBindingConfig;
import org.apache.camel.quarkus.component.knative.sink.binding.KnativeSinkBindingRecorder;
import org.apache.camel.quarkus.core.deployment.spi.CamelBeanBuildItem;
import org.apache.camel.quarkus.core.deployment.spi.CamelContextBuildItem;
import org.apache.camel.quarkus.core.deployment.spi.CamelRuntimeBeanBuildItem;

public class KnativeSinkBindingProcessor {

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep(onlyIf = KnativeSinkBindingConfig.Enabled.class)
    @Consume(CamelContextBuildItem.class)
    CamelRuntimeBeanBuildItem setupKnativeSinkBinding(
            KnativeSinkBindingConfig config,
            KnativeComponent knativeComponent,
            KnativeSinkBindingRecorder recorder,
            CamelContext context) {
        return new CamelRuntimeBeanBuildItem(
                "knative-sink-binding",
                KnativeResource.class.getName(),
                recorder.createKnativeSinkBindingResource(context, config));
    }
}
