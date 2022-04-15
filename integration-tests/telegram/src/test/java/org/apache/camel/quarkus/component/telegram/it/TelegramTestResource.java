/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.telegram.it;

import java.util.Map;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.camel.quarkus.test.wiremock.WireMockTestResourceLifecycleManager;
import org.apache.camel.util.CollectionHelper;

public class TelegramTestResource extends WireMockTestResourceLifecycleManager {

    private static final String TELEGRAM_API_BASE_URL = "https://api.telegram.org";
    private static final String TELEGRAM_ENV_AUTHORIZATION_TOKEN = "TELEGRAM_AUTHORIZATION_TOKEN";
    private static final String TELEGRAM_ENV_WEBHOOK_URL = "TELEGRAM_ENV_WEBHOOK_URL";

    @Override
    public Map<String, String> start() {
        Map<String, String> properties = super.start();
        String wireMockUrl = properties.get("wiremock.url.ssl");
        String baseUri = wireMockUrl != null ? wireMockUrl : TELEGRAM_API_BASE_URL;
        String webhookUrl =  wireMockUrl != null ? getWebhookUrl(wireMockUrl) : TELEGRAM_ENV_WEBHOOK_URL;
        return CollectionHelper.mergeMaps(properties,
                CollectionHelper.mapOf("camel.component.telegram.base-uri", baseUri,
                        "quarkus.rest-client.\"org.apache.camel.quarkus.component.telegram.it.WebhookRestClient\".url", webhookUrl,
                        "camel.component.webhook.configuration.webhook-external-url", webhookUrl+"/webhook" ));
    }

    @Override
    protected String getRecordTargetBaseUrl() {
        return TELEGRAM_API_BASE_URL;
    }

    @Override
    protected boolean isMockingEnabled() {
        return !envVarsPresent(TELEGRAM_ENV_AUTHORIZATION_TOKEN, TELEGRAM_ENV_WEBHOOK_URL);
    }


    private String getWebhookUrl(String wireMockUrl){
        return wireMockUrl + "/fake-token";
    }

    @Override
    protected void customizeWiremockConfiguration(WireMockConfiguration config) {
        config.dynamicHttpsPort();
    }
}
