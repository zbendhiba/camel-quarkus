/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.weather;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.spi.EndpointUriFactory;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
public class WeatherEndpointUriFactory extends org.apache.camel.support.component.EndpointUriFactorySupport implements EndpointUriFactory {

    private static final String BASE = ":name";

    private static final Set<String> PROPERTY_NAMES;
    private static final Set<String> SECRET_PROPERTY_NAMES;
    static {
        Set<String> props = new HashSet<>(41);
        props.add("backoffMultiplier");
        props.add("weatherApi");
        props.add("geolocationAccessKey");
        props.add("topLat");
        props.add("language");
        props.add("lon");
        props.add("units");
        props.add("initialDelay");
        props.add("mode");
        props.add("scheduler");
        props.add("bridgeErrorHandler");
        props.add("useFixedDelay");
        props.add("runLoggingLevel");
        props.add("backoffErrorThreshold");
        props.add("greedy");
        props.add("geolocationRequestHostIP");
        props.add("scheduledExecutorService");
        props.add("lat");
        props.add("geoLocationProvider");
        props.add("repeatCount");
        props.add("timeUnit");
        props.add("zip");
        props.add("httpClient");
        props.add("period");
        props.add("headerName");
        props.add("sendEmptyMessageWhenIdle");
        props.add("schedulerProperties");
        props.add("exchangePattern");
        props.add("cnt");
        props.add("rightLon");
        props.add("zoom");
        props.add("backoffIdleThreshold");
        props.add("lazyStartProducer");
        props.add("delay");
        props.add("pollStrategy");
        props.add("startScheduler");
        props.add("appid");
        props.add("name");
        props.add("ids");
        props.add("location");
        props.add("exceptionHandler");
        PROPERTY_NAMES = Collections.unmodifiableSet(props);
        SECRET_PROPERTY_NAMES = Collections.emptySet();
    }

    @Override
    public boolean isEnabled(String scheme) {
        return "weather".equals(scheme);
    }

    @Override
    public String buildUri(String scheme, Map<String, Object> properties, boolean encode) throws URISyntaxException {
        String syntax = scheme + BASE;
        String uri = syntax;

        Map<String, Object> copy = new HashMap<>(properties);

        uri = buildPathParameter(syntax, uri, "name", null, true, copy);
        uri = buildQueryParameters(uri, copy, encode);
        return uri;
    }

    @Override
    public Set<String> propertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public Set<String> secretPropertyNames() {
        return SECRET_PROPERTY_NAMES;
    }

    @Override
    public boolean isLenientProperties() {
        return false;
    }
}
