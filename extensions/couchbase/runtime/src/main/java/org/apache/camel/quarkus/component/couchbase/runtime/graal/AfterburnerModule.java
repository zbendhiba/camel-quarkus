package org.apache.camel.quarkus.component.couchbase.runtime.graal;

import java.io.Serializable;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class AfterburnerModule  extends Module
        implements Serializable {

    @Override
    public String getModuleName() {
        return null;
    }

    @Override
    public Version version() {
        return null;
    }

    @Override
    public void setupModule(Module.SetupContext context) {

    }
}
