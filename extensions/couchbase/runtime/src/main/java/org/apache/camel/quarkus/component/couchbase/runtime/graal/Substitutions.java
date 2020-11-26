package org.apache.camel.quarkus.component.couchbase.runtime.graal;

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.Module;
import com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.deser.DeserializerModifier;
import com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.ser.SerializerModifier;
import com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.util.ClassName;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

public class Substitutions {
}
/*
@TargetClass(className = "com.couchbase.client.java.query.QueryAccessor")
final class Target_QueryAccessor {

    @Substitute
    public static QueryRequest targetedQueryRequest(String statement,
            byte[] queryBytes,
            String clientContextId,
            @Nullable NodeIdentifier target,
            boolean readonly,
            RetryStrategy retryStrategy,
            Duration timeout,
            RequestSpan parentSpan,
            Core core) {
        notNullOrEmpty(statement, "Statement", () -> new ReducedQueryErrorContext(statement));

        QueryRequest request;
        if (target != null) {
            request = new TargetedQueryRequest(timeout, core.context(), retryStrategy, core.context().authenticator(),
                    statement,
                    queryBytes, readonly, clientContextId, parentSpan, null, target);
        } else {
            request = new QueryRequest(timeout, core.context(), retryStrategy, core.context().authenticator(), statement,
                    queryBytes, readonly, clientContextId, parentSpan, null);
        }
        return request;
    }
}

final class TargetedQueryRequest extends QueryRequest implements TargetedRequest {
    private NodeIdentifier target;

    public TargetedQueryRequest(Duration timeout, CoreContext ctx, RetryStrategy retryStrategy, Authenticator authenticator,
            String statement, byte[] query, boolean idempotent, String contextId, final RequestSpan parentSpan,
            final String queryContext, NodeIdentifier target) {
        super(timeout, ctx, retryStrategy, authenticator, statement, query, idempotent, contextId, parentSpan, queryContext);
        this.target = target;
    }

    @Override
    public NodeIdentifier target() {
        return target;
    }
}*/
/*
@TargetClass(className = "com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.util.MyClassLoader", onlyWith = TargetClass.AlwaysIncluded.class)
final class Target_MyClassLoader {
    @Substitute
    public Class<?> loadAndResolve(ClassName className, byte[] byteCode)
            throws IllegalArgumentException {
        System.out.println("************************** Get out");
        return null;
    }
}*/

/*
@TargetClass(className = "com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.AfterburnerModule")
final class Target_AfterburnerModule {

    @Alias
    protected boolean _cfgUseValueClassLoader = true;

    @Alias
    protected boolean _cfgUseOptimizedBeanDeserializer = false;

    @Substitute
    public Target_AfterburnerModule() {
        _cfgUseValueClassLoader = true;
        _cfgUseOptimizedBeanDeserializer = false;
    }

    @Substitute
    public void setupModule(Module.SetupContext context)
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        context.addBeanDeserializerModifier(new DeserializerModifier(cl,
                false));
        context.addBeanSerializerModifier(new SerializerModifier(cl));
    }

    @Substitute
    public Target_AfterburnerModule setUseValueClassLoader(boolean state) {
        return this;
    }

    @Substitute
    public Target_AfterburnerModule setUseOptimizedBeanDeserializer(boolean state) {
        return this;
    }
}*/

