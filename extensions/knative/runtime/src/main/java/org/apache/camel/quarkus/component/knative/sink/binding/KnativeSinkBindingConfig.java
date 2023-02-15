package org.apache.camel.quarkus.component.knative.sink.binding;

import java.util.Map;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigRoot;
import org.apache.camel.component.knative.spi.Knative;
import org.eclipse.microprofile.config.ConfigProvider;

@ConfigRoot(name = "camel.knative.sink.binding")
public class KnativeSinkBindingConfig {
    /**
     * Whether a Knative Sink should be automatically configured
     * according to 'quarkus.camel.knative.sink.binding.*' configurations.
     */
    @ConfigItem(defaultValue = "false")
    public boolean enabled;

    /**
     * Name of the Knative resource
     */
    @ConfigItem
    public String name;

    /**
     * Type of the Knative resource
     */
    @ConfigItem
    public Knative.Type type;

    /**
     * Cloud Events API version
     */
    @ConfigItem
    public Optional<String> apiVersion;

    /**
     * Sink URL
     */
    @ConfigItem
    public Optional<String> sinkUrl;

    /**
     * The list of key/value used override Cloud Events configuration,
     * defaults to empty map.
     */
    @ConfigItem
    public Map<String, String> ceOverride;

    public static final class Enabled implements BooleanSupplier {
        @Override
        public boolean getAsBoolean() {
            return ConfigProvider.getConfig().getOptionalValue("quarkus.camel.knative.sink.binding.enabled", Boolean.class)
                    .orElse(Boolean.FALSE);
        }
    }
}
