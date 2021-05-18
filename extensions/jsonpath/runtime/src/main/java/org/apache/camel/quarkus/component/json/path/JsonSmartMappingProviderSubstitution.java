package org.apache.camel.quarkus.component.json.path;

import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(JsonSmartMappingProvider.class)
@Delete
public final class JsonSmartMappingProviderSubstitution {
}
