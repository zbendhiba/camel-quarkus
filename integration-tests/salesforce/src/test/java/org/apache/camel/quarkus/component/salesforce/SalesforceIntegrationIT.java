package org.apache.camel.quarkus.component.salesforce;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "SALESFORCE_USERNAME", matches = ".+")
@EnabledIfEnvironmentVariable(named = "SALESFORCE_PASSWORD", matches = ".+")
@EnabledIfEnvironmentVariable(named = "SALESFORCE_CLIENTID", matches = ".+")
@EnabledIfEnvironmentVariable(named = "SALESFORCE_CLIENTSECRET", matches = ".+")
public class SalesforceIntegrationIT extends SalesforceIntegrationTest {
}
