package org.apache.camel.quarkus.component.couchbase.it;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.view.DesignDocument;
import com.couchbase.client.java.manager.view.View;
import com.couchbase.client.java.view.DesignDocumentNamespace;
import org.apache.camel.quarkus.testcontainers.ContainerResourceLifecycleManager;
import org.apache.camel.util.CollectionHelper;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.utility.DockerImageName;

public class CouchbaseTestResource implements ContainerResourceLifecycleManager {
    private final static DockerImageName COUCHBASE_IMAGE = DockerImageName.parse("couchbase/server:6.5.1");
    public static final int KV_PORT = 11210;
    public static final int MANAGEMENT_PORT = 8091;
    public static final int VIEW_PORT = 8092;
    public static final int QUERY_PORT = 8093;
    public static final int SEARCH_PORT = 8094;
    protected static String bucketName = "testBucket";

    private CustomCouchbaseContainer container;
    protected static Cluster cluster;

    private class CustomCouchbaseContainer extends CouchbaseContainer {
        public CustomCouchbaseContainer() {
            super(COUCHBASE_IMAGE);

            addFixedExposedPort(KV_PORT, KV_PORT);
            addFixedExposedPort(MANAGEMENT_PORT, MANAGEMENT_PORT);
            addFixedExposedPort(VIEW_PORT, VIEW_PORT);
            addFixedExposedPort(QUERY_PORT, QUERY_PORT);
            addFixedExposedPort(SEARCH_PORT, SEARCH_PORT);
        }
    }

    @Override
    public Map<String, String> start() {
        container = new CustomCouchbaseContainer();
        container.start();

        initBucket();

        return CollectionHelper.mapOf("couchbase.connection.uri", getConnectionUri(),
                "couchbase.bucket.name", bucketName);
    }

    @Override
    public void stop() {
        if (cluster != null) {
            cluster.buckets().dropBucket(bucketName);
            cluster.disconnect();
        }
        if (container != null) {
            container.stop();
        }
    }

    public String getConnectionUri() {
        return String.format("couchbase:http://%s:%d?bucket=%s&username=%s&password=%s", getHostname(),
                getPort(), bucketName, getUsername(), getPassword());
    }

    public String getUsername() {
        return container.getUsername();
    }

    public String getPassword() {
        return container.getPassword();
    }

    public String getHostname() {
        return container.getHost();
    }

    public int getPort() {
        return container.getBootstrapHttpDirectPort();
    }

    private void initBucket() {
        cluster = Cluster.connect(container.getConnectionString(), this.getUsername(), this.getPassword());

        cluster.buckets().createBucket(
                BucketSettings.create(bucketName).bucketType(BucketType.COUCHBASE).flushEnabled(true));

        cluster.bucket(bucketName);
        DesignDocument designDoc = new DesignDocument(
                bucketName,
                Collections.singletonMap(bucketName, new View("function (doc, meta) {  emit(meta.id, doc);}")));
        cluster.bucket(bucketName).viewIndexes().upsertDesignDocument(designDoc, DesignDocumentNamespace.PRODUCTION);
        cluster.bucket(bucketName).waitUntilReady(Duration.ofSeconds(30));
    }
}
