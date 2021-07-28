package org.apache.camel.quarkus.component.kafka.oauth;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(KafkaOauth2TestResource.class)
public class KafkaOauth2Test {

    @Test
    void testBidon(){
        assertTrue(true);
    }
}
