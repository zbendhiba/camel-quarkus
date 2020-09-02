package org.apache.camel.quarkus.component.couchbase.runtime;

import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.concurrent.ThreadFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;

import com.couchbase.client.core.deps.io.netty.bootstrap.AbstractBootstrapConfig;
import com.couchbase.client.core.deps.io.netty.bootstrap.ChannelFactory;
import com.couchbase.client.core.deps.io.netty.buffer.ByteBufAllocator;
import com.couchbase.client.core.deps.io.netty.channel.Channel;
import com.couchbase.client.core.deps.io.netty.channel.ChannelFuture;
import com.couchbase.client.core.deps.io.netty.channel.DefaultChannelPromise;
import com.couchbase.client.core.deps.io.netty.channel.EventLoopGroup;
import com.couchbase.client.core.deps.io.netty.channel.nio.NioEventLoopGroup;
import com.couchbase.client.core.deps.io.netty.handler.ssl.ApplicationProtocolConfig;
import com.couchbase.client.core.deps.io.netty.handler.ssl.CipherSuiteFilter;
import com.couchbase.client.core.deps.io.netty.handler.ssl.ClientAuth;
import com.couchbase.client.core.deps.io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator;
import com.couchbase.client.core.deps.io.netty.handler.ssl.JdkApplicationProtocolNegotiator;
import com.couchbase.client.core.deps.io.netty.util.concurrent.DefaultThreadFactory;
import com.couchbase.client.core.deps.io.netty.util.concurrent.GlobalEventExecutor;
import com.couchbase.client.core.deps.io.netty.util.internal.logging.InternalLoggerFactory;
import com.couchbase.client.core.deps.io.netty.util.internal.logging.JdkLoggerFactory;
import com.couchbase.client.core.encryption.CryptoManager;
import com.couchbase.client.core.env.OwnedSupplier;
import com.couchbase.client.java.codec.DefaultJsonSerializer;
import com.couchbase.client.java.codec.JsonSerializer;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import com.oracle.svm.core.jdk.JDK8OrEarlier;
import com.oracle.svm.graal.hosted.GraalFeature;

final class NettySubstitutions {
}

@TargetClass(className = "com.couchbase.client.core.env.IoEnvironment.Builder", onlyWith = GraalFeature.IsEnabled.class)
final class SubstituteIoEnvironmentBuilder {
    @Substitute
    public static final boolean nativeIoEnabled = false;
}

@TargetClass(className = "com.couchbase.client.core.env.IoEnvironment")
final class SubstituteIoEnvironment {
    @Substitute
    private static OwnedSupplier<EventLoopGroup> createEventLoopGroup(final boolean nativeIoEnabled, final int numThreads,
            final String poolName) {
        final ThreadFactory threadFactory = new DefaultThreadFactory(poolName, true);
        return new OwnedSupplier<>(new NioEventLoopGroup(numThreads, threadFactory));
    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.channel.kqueue.KQueueEventArray")
@Delete
final class SubstituteKQueueEventArray {
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.channel.kqueue.KQueue")
@Delete
final class SubstituteKQueue {
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.channel.kqueue.KQueueEventLoop")
@Delete
final class SubstituteKQueueEventLoop {
}

@TargetClass(className = "com.couchbase.client.java.env.ClusterEnvironment")
final class SubstituteClusterEnvironment {
    @Substitute
    private JsonSerializer newDefaultSerializer(CryptoManager cryptoManager) {
        return DefaultJsonSerializer.create(cryptoManager);
    }
}

/**
 * This substitution avoid having loggers added to the build.
 * Adapted from
 * https://github.com/quarkusio/quarkus/blob/master/extensions/netty/runtime/src/main/java/io/quarkus/netty/runtime/graal/NettySubstitutions.java
 */
@TargetClass(className = "com.couchbase.client.core.deps.io.netty.util.internal.logging.InternalLoggerFactory")
final class Target_io_netty_util_internal_logging_InternalLoggerFactory {

    @Substitute
    private static InternalLoggerFactory newDefaultFactory(String name) {
        return JdkLoggerFactory.INSTANCE;
    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.handler.ssl.JdkSslServerContext")
final class Target_io_netty_handler_ssl_JdkSslServerContext {

    @Alias
    Target_io_netty_handler_ssl_JdkSslServerContext(Provider provider,
            X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory,
            X509Certificate[] keyCertChain, PrivateKey key, String keyPassword,
            KeyManagerFactory keyManagerFactory, Iterable<String> ciphers, CipherSuiteFilter cipherFilter,
            ApplicationProtocolConfig apn, long sessionCacheSize, long sessionTimeout,
            ClientAuth clientAuth, String[] protocols, boolean startTls,
            String keyStore)
            throws SSLException {
    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.handler.ssl.JdkSslClientContext")
final class Target_io_netty_handler_ssl_JdkSslClientContext {

    @Alias
    Target_io_netty_handler_ssl_JdkSslClientContext(Provider sslContextProvider, X509Certificate[] trustCertCollection,
            TrustManagerFactory trustManagerFactory, X509Certificate[] keyCertChain, PrivateKey key,
            String keyPassword, KeyManagerFactory keyManagerFactory, Iterable<String> ciphers,
            CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols,
            long sessionCacheSize, long sessionTimeout, String keyStoreType)
            throws SSLException {

    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.handler.ssl.SslHandler$SslEngineType")
final class Target_io_netty_handler_ssl_SslHandler$SslEngineType {

    @Alias
    public static Target_io_netty_handler_ssl_SslHandler$SslEngineType JDK;

    @Substitute
    static Target_io_netty_handler_ssl_SslHandler$SslEngineType forEngine(SSLEngine engine) {
        return JDK;
    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.handler.ssl.JettyAlpnSslEngine", onlyWith = JDK8OrEarlier.class)
final class Target_io_netty_handler_ssl_JettyAlpnSslEngine {
    @Substitute
    static boolean isAvailable() {
        return false;
    }

    @Substitute
    static Target_io_netty_handler_ssl_JettyAlpnSslEngine newClientEngine(SSLEngine engine,
            JdkApplicationProtocolNegotiator applicationNegotiator) {
        return null;
    }

    @Substitute
    static Target_io_netty_handler_ssl_JettyAlpnSslEngine newServerEngine(SSLEngine engine,
            JdkApplicationProtocolNegotiator applicationNegotiator) {
        return null;
    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.handler.ssl.JdkAlpnApplicationProtocolNegotiator$AlpnWrapper", onlyWith = JDK8OrEarlier.class)
final class Target_io_netty_handler_ssl_JdkAlpnApplicationProtocolNegotiator_AlpnWrapperJava8 {
    @Substitute
    public SSLEngine wrapSslEngine(SSLEngine engine, ByteBufAllocator alloc,
            JdkApplicationProtocolNegotiator applicationNegotiator, boolean isServer) {
        if (Target_io_netty_handler_ssl_JettyAlpnSslEngine.isAvailable()) {
            return isServer
                    ? (SSLEngine) (Object) Target_io_netty_handler_ssl_JettyAlpnSslEngine.newServerEngine(engine,
                            applicationNegotiator)
                    : (SSLEngine) (Object) Target_io_netty_handler_ssl_JettyAlpnSslEngine.newClientEngine(engine,
                            applicationNegotiator);
        }
        throw new RuntimeException("Unable to wrap SSLEngine of type " + engine.getClass().getName());
    }
}

/*
@TargetClass(className = " com.couchbase.client.core.deps.io.netty.handler.ssl.SslContext")
final class Target_io_netty_handler_ssl_SslContext {

    @Substitute
    static SslContext newServerContextInternal(SslProvider provider,
            Provider sslContextProvider,
            X509Certificate[] trustCertCollection, TrustManagerFactory trustManagerFactory,
            X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory,
            Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn,
            long sessionCacheSize, long sessionTimeout, ClientAuth clientAuth, String[] protocols, boolean startTls,
            boolean enableOcsp, String keyStoreType)
            throws SSLException {

        if (enableOcsp) {
            throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + provider);
        }
        return (SslContext) (Object) new Target_io_netty_handler_ssl_JdkSslServerContext(sslContextProvider,
                trustCertCollection, trustManagerFactory, keyCertChain, key, keyPassword,
                keyManagerFactory, ciphers, cipherFilter, apn, sessionCacheSize, sessionTimeout,
                clientAuth, protocols, startTls, keyStoreType);
    }

    @Substitute
    static SslContext newClientContextInternal(
            SslProvider provider,
            Provider sslContextProvider,
            X509Certificate[] trustCert, TrustManagerFactory trustManagerFactory,
            X509Certificate[] keyCertChain, PrivateKey key, String keyPassword, KeyManagerFactory keyManagerFactory,
            Iterable<String> ciphers, CipherSuiteFilter cipherFilter, ApplicationProtocolConfig apn, String[] protocols,
            long sessionCacheSize, long sessionTimeout, boolean enableOcsp, String keyStoreType) throws SSLException {
        if (enableOcsp) {
            throw new IllegalArgumentException("OCSP is not supported with this SslProvider: " + provider);
        }
        return (SslContext) (Object) new Target_io_netty_handler_ssl_JdkSslClientContext(sslContextProvider,
                trustCert, trustManagerFactory, keyCertChain, key, keyPassword,
                keyManagerFactory, ciphers, cipherFilter, apn, protocols, sessionCacheSize,
                sessionTimeout, keyStoreType);
    }

}
*/
@TargetClass(className = "com.couchbase.client.core.deps.io.netty.handler.ssl.JdkDefaultApplicationProtocolNegotiator")
final class Target_io_netty_handler_ssl_JdkDefaultApplicationProtocolNegotiator {

    @Alias
    public static Target_io_netty_handler_ssl_JdkDefaultApplicationProtocolNegotiator INSTANCE;
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.handler.ssl.JdkSslContext")
final class Target_io_netty_handler_ssl_JdkSslContext {

    @Substitute
    static JdkApplicationProtocolNegotiator toNegotiator(ApplicationProtocolConfig config, boolean isServer) {
        if (config == null) {
            return (JdkApplicationProtocolNegotiator) (Object) Target_io_netty_handler_ssl_JdkDefaultApplicationProtocolNegotiator.INSTANCE;
        }

        switch (config.protocol()) {
        case NONE:
            return (JdkApplicationProtocolNegotiator) (Object) Target_io_netty_handler_ssl_JdkDefaultApplicationProtocolNegotiator.INSTANCE;
        case ALPN:
            if (isServer) {
                // GRAAL RC9 bug: https://github.com/oracle/graal/issues/813
                //                switch(config.selectorFailureBehavior()) {
                //                case FATAL_ALERT:
                //                    return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                //                case NO_ADVERTISE:
                //                    return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                //                default:
                //                    throw new UnsupportedOperationException(new StringBuilder("JDK provider does not support ")
                //                    .append(config.selectorFailureBehavior()).append(" failure behavior").toString());
                //                }
                ApplicationProtocolConfig.SelectorFailureBehavior behavior = config.selectorFailureBehavior();
                if (behavior == ApplicationProtocolConfig.SelectorFailureBehavior.FATAL_ALERT)
                    return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                else if (behavior == ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE)
                    return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                else {
                    throw new UnsupportedOperationException(new StringBuilder("JDK provider does not support ")
                            .append(config.selectorFailureBehavior()).append(" failure behavior").toString());
                }
            } else {
                switch (config.selectedListenerFailureBehavior()) {
                case ACCEPT:
                    return new JdkAlpnApplicationProtocolNegotiator(false, config.supportedProtocols());
                case FATAL_ALERT:
                    return new JdkAlpnApplicationProtocolNegotiator(true, config.supportedProtocols());
                default:
                    throw new UnsupportedOperationException(new StringBuilder("JDK provider does not support ")
                            .append(config.selectedListenerFailureBehavior()).append(" failure behavior").toString());
                }
            }
        default:
            throw new UnsupportedOperationException(
                    new StringBuilder("JDK provider does not support ").append(config.protocol()).append(" protocol")
                            .toString());
        }
    }

}

/*
 * This one only prints exceptions otherwise we get a useless bogus
 * exception message: https://github.com/eclipse-vertx/vert.x/issues/1657
 */
@TargetClass(className = "com.couchbase.client.core.deps.io.netty.bootstrap.AbstractBootstrap")
final class Target_io_netty_bootstrap_AbstractBootstrap {

    @Alias
    private ChannelFactory channelFactory;

    @Alias
    void init(Channel channel) throws Exception {
    }

    @Alias
    public AbstractBootstrapConfig config() {
        return null;
    }

    @Substitute
    final ChannelFuture initAndRegister() {
        Channel channel = null;
        try {
            channel = channelFactory.newChannel();
            init(channel);
        } catch (Throwable t) {
            // THE FIX IS HERE:
            t.printStackTrace();
            if (channel != null) {
                // channel can be null if newChannel crashed (eg SocketException("too many open files"))
                channel.unsafe().closeForcibly();
            }
            // as the Channel is not registered yet we need to force the usage of the GlobalEventExecutor
            return new DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE).setFailure(t);
        }

        ChannelFuture regFuture = config().group().register(channel);
        if (regFuture.cause() != null) {
            if (channel.isRegistered()) {
                channel.close();
            } else {
                channel.unsafe().closeForcibly();
            }
        }

        // If we are here and the promise is not failed, it's one of the following cases:
        // 1) If we attempted registration from the event loop, the registration has been completed at this point.
        //    i.e. It's safe to attempt bind() or connect() now because the channel has been registered.
        // 2) If we attempted registration from the other thread, the registration request has been successfully
        //    added to the event loop's task queue for later execution.
        //    i.e. It's safe to attempt bind() or connect() now:
        //         because bind() or connect() will be executed *after* the scheduled registration task is executed
        //         because register(), bind(), and connect() are all bound to the same thread.

        return regFuture;

    }
}
/*
@TargetClass(className = " com.couchbase.client.core.deps.io.netty.channel.nio.NioEventLoop")
final class Target_io_netty_channel_nio_NioEventLoop {

    @Substitute
    private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
        return new LinkedBlockingDeque<>();
    }
}*/

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.buffer.AbstractReferenceCountedByteBuf")
final class Target_io_netty_buffer_AbstractReferenceCountedByteBuf {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FieldOffset, name = "refCnt")
    private static long REFCNT_FIELD_OFFSET;
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.util.AbstractReferenceCounted")
final class Target_io_netty_util_AbstractReferenceCounted {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.FieldOffset, name = "refCnt")
    private static long REFCNT_FIELD_OFFSET;
}

// This class is runtime-initialized by NettyProcessor
final class Holder_io_netty_util_concurrent_ScheduledFutureTask {
    static final long START_TIME = System.nanoTime();
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.util.concurrent.ScheduledFutureTask")
final class Target_io_netty_util_concurrent_ScheduledFutureTask {

    // The START_TIME field is kept but not used.
    // All the accesses to it have been replaced with Holder_io_netty_util_concurrent_ScheduledFutureTask

    @Substitute
    static long initialNanoTime() {
        return Holder_io_netty_util_concurrent_ScheduledFutureTask.START_TIME;
    }

    @Substitute
    static long nanoTime() {
        return System.nanoTime() - Holder_io_netty_util_concurrent_ScheduledFutureTask.START_TIME;
    }

    @Alias
    public long deadlineNanos() {
        return 0;
    }

    @Substitute
    public long delayNanos(long currentTimeNanos) {
        return Math.max(0,
                deadlineNanos() - (currentTimeNanos - Holder_io_netty_util_concurrent_ScheduledFutureTask.START_TIME));
    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.channel.ChannelHandlerMask")
final class Target_io_netty_channel_ChannelHandlerMask {

    // Netty tries to self-optimized itself, but it requires lots of reflection. We disable this behavior and avoid
    // misleading DEBUG messages in the log.
    @Substitute
    private static boolean isSkippable(final Class<?> handlerType, final String methodName, final Class... paramTypes) {
        return false;
    }
}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.util.internal.NativeLibraryLoader")
final class Target_io_netty_util_internal_NativeLibraryLoader {

    // This method can trick GraalVM into thinking that Classloader#defineClass is getting called
    @Substitute
    static Class<?> tryToLoadClass(final ClassLoader loader, final Class<?> helper)
            throws ClassNotFoundException {
        return Class.forName(helper.getName(), false, loader);
    }

}

@TargetClass(className = "com.couchbase.client.core.deps.io.netty.buffer.EmptyByteBuf")
final class Target_io_netty_buffer_EmptyByteBuf {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private static ByteBuffer EMPTY_BYTE_BUFFER;

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private static long EMPTY_BYTE_BUFFER_ADDRESS;

    @Substitute
    public ByteBuffer nioBuffer() {
        return EmptyByteBufStub.emptyByteBuffer();
    }

    @Substitute
    public ByteBuffer[] nioBuffers() {
        return new ByteBuffer[] { EmptyByteBufStub.emptyByteBuffer() };
    }

    @Substitute
    public ByteBuffer internalNioBuffer(int index, int length) {
        return EmptyByteBufStub.emptyByteBuffer();
    }

    @Substitute
    public boolean hasMemoryAddress() {
        return EmptyByteBufStub.emptyByteBufferAddress() != 0;
    }

    @Substitute
    public long memoryAddress() {
        if (hasMemoryAddress()) {
            return EmptyByteBufStub.emptyByteBufferAddress();
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
