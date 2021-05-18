package org.apache.camel.quarkus.component.json.path;

import com.jayway.jsonpath.internal.DefaultsImpl;
import com.oracle.svm.core.annotate.Delete;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(DefaultsImpl.class)
@Delete
public final class DefaultsImplSubstitution {
}
