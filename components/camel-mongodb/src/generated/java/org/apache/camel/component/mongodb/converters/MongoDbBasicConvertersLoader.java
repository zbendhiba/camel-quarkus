/* Generated by camel build tools - do NOT edit this file! */
package org.apache.camel.component.mongodb.converters;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.TypeConverterLoaderException;
import org.apache.camel.spi.TypeConverterLoader;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.support.SimpleTypeConverter;
import org.apache.camel.support.TypeConverterSupport;
import org.apache.camel.util.DoubleMap;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
@SuppressWarnings("unchecked")
public final class MongoDbBasicConvertersLoader implements TypeConverterLoader {

    public MongoDbBasicConvertersLoader() {
    }

    @Override
    public void load(TypeConverterRegistry registry) throws TypeConverterLoaderException {
        registerConverters(registry);
    }

    private void registerConverters(TypeConverterRegistry registry) {
        addTypeConverter(registry, java.util.List.class, java.lang.String.class, true,
            (type, exchange, value) -> org.apache.camel.component.mongodb.converters.MongoDbBasicConverters.fromStringToList((java.lang.String) value));
        addTypeConverter(registry, java.util.Map.class, org.bson.Document.class, false,
            (type, exchange, value) -> org.apache.camel.component.mongodb.converters.MongoDbBasicConverters.fromDocumentToMap((org.bson.Document) value));
        addTypeConverter(registry, org.bson.Document.class, java.io.File.class, false,
            (type, exchange, value) -> org.apache.camel.component.mongodb.converters.MongoDbBasicConverters.fromFileToDocument((java.io.File) value, exchange));
        addTypeConverter(registry, org.bson.Document.class, java.io.InputStream.class, false,
            (type, exchange, value) -> org.apache.camel.component.mongodb.converters.MongoDbBasicConverters.fromInputStreamToDocument((java.io.InputStream) value, exchange));
        addTypeConverter(registry, org.bson.Document.class, java.lang.String.class, false,
            (type, exchange, value) -> org.apache.camel.component.mongodb.converters.MongoDbBasicConverters.fromStringToDocument((java.lang.String) value));
        addTypeConverter(registry, org.bson.Document.class, java.util.Map.class, false,
            (type, exchange, value) -> org.apache.camel.component.mongodb.converters.MongoDbBasicConverters.fromMapToDocument((java.util.Map) value));
        addTypeConverter(registry, org.bson.types.ObjectId.class, java.lang.String.class, false,
            (type, exchange, value) -> org.apache.camel.component.mongodb.converters.MongoDbBasicConverters.fromStringToObjectId((java.lang.String) value));
    }

    private static void addTypeConverter(TypeConverterRegistry registry, Class<?> toType, Class<?> fromType, boolean allowNull, SimpleTypeConverter.ConversionMethod method) { 
        registry.addTypeConverter(toType, fromType, new SimpleTypeConverter(allowNull, method));
    }

}