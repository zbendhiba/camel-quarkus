package org.apache.camel.quarkus.component.json.path;

import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.filter.ValueNode;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(ValueNode.class)
final class ValueNodeSubstitution {
    @Substitute
    private static boolean isJson(Object o) {
        // temporary
        return false;
    }

}

@TargetClass(className = "com.jayway.jsonpath.internal.filter.ValueNode$JsonNode")
final class JsonNodeSubstitution {

    @Substitute
    public Object parse(Predicate.PredicateContext ctx) {
        // temporary
        return null;
    }
}
