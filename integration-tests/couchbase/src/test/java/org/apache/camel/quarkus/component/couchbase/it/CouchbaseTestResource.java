package org.apache.camel.quarkus.component.couchbase.it;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import com.couchbase.client.java.Bucket;
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
    private final static DockerImageName COUCHBASE_IMAGE = DockerImageName.parse("couchbase/server:6.5.1"); //DockerImageName.parse("couchbase:6.6.1");
    public static final String COUCHBASE_USERNAME = "couchbase.username";
    public static final String COUCHBASE_PASSWORD = "couchbase.password";
    public static final String COUCHBASE_HOSTNAME = "couchbase.hostname";
    public static final String COUCHBASE_PORT = "couchbase.port";
    public static final int KV_PORT = 11210;
    public static final int MANAGEMENT_PORT = 8091;
    public static final int VIEW_PORT = 8092;
    public static final int QUERY_PORT = 8093;
    public static final int SEARCH_PORT = 8094;
    private static String bucketName = "testBucket";

    private CustomCouchbaseContainer container;
    private Cluster cluster;

    private class CustomCouchbaseContainer extends CouchbaseContainer {
        public CustomCouchbaseContainer() {
            super("couchbase/server:6.5.1");

            final int kvPort = 11210;
            addFixedExposedPort(kvPort, kvPort);

            final int managementPort = 8091;
            addFixedExposedPort(managementPort, managementPort);

            final int viewPort = 8092;
            addFixedExposedPort(viewPort, viewPort);

            final int queryPort = 8093;
            addFixedExposedPort(queryPort, queryPort);

            final int searchPort = 8094;
            addFixedExposedPort(searchPort, searchPort);
        }
    }

    @Override
    public Map<String, String> start() {
        container = new CustomCouchbaseContainer();
        container.start();

        initBucket();

        return CollectionHelper.mapOf("couchbase.connection.url", getConnectionUri(),
                COUCHBASE_HOSTNAME, getHostname(),
                COUCHBASE_PORT, String.valueOf(getPort()),
                COUCHBASE_USERNAME, getUsername(),
                COUCHBASE_PASSWORD, getPassword());
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

    public String getConnectionString() {
        return String.format("couchbase:http://%s:%d", getHostname(), container.getBootstrapCarrierDirectPort());
    }

    public String getLongConnectionString() {
        return String.format("%s?bucket=%s&username=%s&password=%s", getConnectionString(),
                bucketName, getUsername(), getPassword());
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

        Bucket bucket = cluster.bucket(bucketName);
        DesignDocument designDoc = new DesignDocument(
                bucketName,
                Collections.singletonMap(bucketName, new View("function (doc, meta) {  emit(meta.id, doc);}")));
        cluster.bucket(bucketName).viewIndexes().upsertDesignDocument(designDoc, DesignDocumentNamespace.PRODUCTION);
        cluster.bucket(bucketName).waitUntilReady(Duration.ofSeconds(30));
    }
}
