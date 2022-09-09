package org.apache.camel.quarkus.component.knative.producer.it;

import java.util.Map;

import org.apache.camel.quarkus.test.wiremock.WireMockTestResourceLifecycleManager;

public class KnativeTestResource extends WireMockTestResourceLifecycleManager {

    private static final String KNATIVE_CHANNEL_URL = "KNATIVE_CHANNEL_URL";
    private static final String KNATIVE_BROKER_URL = "KNATIVE_BROKER_URL";

    @Override
    protected String getRecordTargetBaseUrl() {
        return "/";
    }

    @Override
    protected boolean isMockingEnabled() {
        return !envVarsPresent(KNATIVE_CHANNEL_URL, KNATIVE_BROKER_URL);
    }

    @Override
    public Map<String, String> start() {
        Map<String, String> options = super.start();
        if (options.containsKey("wiremock.url")) {
            String wiremockUrl = options.get("wiremock.url");
            options.put("channel.test.url", String.format("%s/channel-test", wiremockUrl));
            options.put("broker.test.url", String.format("%s/broker-test", wiremockUrl));
        }
        return options;
    }
}
