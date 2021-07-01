package org.apache.camel.quarkus.component.salesforce;

import java.util.Map;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.apache.camel.quarkus.test.wiremock.WireMockTestResourceLifecycleManager;

public class SalesforceTestResource extends WireMockTestResourceLifecycleManager {
    private static final String SALESFORCE_BASE_URL = "login.salesforce.com";
    private static final String SALESFORCE_CLIENT_ID = "SALESFORCE_CLIENTID";
    private static final String SALESFORCE_CLIENT_SECRET = "SALESFORCE_CLIENTSECRET";
    private static final String SALESFORCE_USERNAME = "SALESFORCE_USERNAME";
    private static final String SALESFORCE_PASSWORD = "SALESFORCE_PASSWORD";

    @Override
    protected String getRecordTargetBaseUrl() {
        return SALESFORCE_BASE_URL;
    }

    @Override
    protected boolean isMockingEnabled() {
        return !envVarsPresent(SALESFORCE_CLIENT_ID, SALESFORCE_CLIENT_SECRET, SALESFORCE_USERNAME, SALESFORCE_PASSWORD);
    }

    @Override
    public Map<String, String> start() {
        Map<String, String> options = super.start();
        if (options.containsKey("wiremock.url")) {
            options.put("camel.component.salesforce.loginUrl", options.get("wiremock.url"));
            options.put("camel.component.salesforce.userName", "username");
            options.put("camel.component.salesforce.password", "password");
            options.put("camel.component.salesforce.clientId", "clientId");
        }
        return options;
    }

    @Override
    protected void customizeWiremockConfiguration(WireMockConfiguration config) {
        config.extensions(new ResponseTemplateTransformer(false));
    }

}
