package org.apache.camel.quarkus.component.json.path;

import com.jayway.jsonpath.Configuration;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(Configuration.class)
public final class ConfigurationSubstitution {

    @Alias
    private static Configuration.Defaults DEFAULTS;

    @Substitute
    private static Configuration.Defaults getEffectiveDefaults() {
        if (DEFAULTS == null) {
            return DefaultsImpl.INSTANCE;
        } else {
            return DEFAULTS;
        }
    }
}
