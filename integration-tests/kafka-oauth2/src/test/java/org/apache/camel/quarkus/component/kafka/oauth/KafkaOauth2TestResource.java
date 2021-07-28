package org.apache.camel.quarkus.component.kafka.oauth;

import java.io.File;
import java.time.Duration;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class KafkaOauth2TestResource  implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaOauth2TestResource.class);

    private DockerComposeContainer kafkaOauthContainer;

    private static final int KAFKA_PORT = 9092;
    private static final int KEYCLOACK_PORT = 8080;

    @Override
    public Map<String, String> start() {
        kafkaOauthContainer = new DockerComposeContainer(new File("src/test/resources/docker/kafka-oauth-docker-compose.yml"))
        /*cloudContainer = new DockerComposeContainer(new File("src/test/resources/cloud-docker-compose.yml"))
                .withExposedService("solr1", SOLR_PORT)
                .withExposedService("zoo1", ZOOKEEPER_PORT)
                .waitingFor("create-collection", Wait.forLogMessage(".*Created collection 'collection1'.*", 1));*/
                //.withExposedService("kafka", 9092)
            //   .withExposedService("kafka", 1, 9092, Wait.forListeningPort().withStartupTimeout(Duration.ofMinutes(5)));/*
        .withServices("kafka", "keycloak", "zookeeper")
                .withLogConsumer("kafka",new Slf4jLogConsumer(LOGGER))
                .waitingFor("kafka", Wait.forLogMessage(".*Recorded new controller, from now on will use broker kafka.*", 1).withStartupTimeout(Duration.ofMinutes(2)))
        ;
        // starting Keycloack and Strimzi
        kafkaOauthContainer.start();
        return null;
    }

    @Override
    public void stop() {
        if(kafkaOauthContainer!=null){
            kafkaOauthContainer.stop();
        }
    }
}
