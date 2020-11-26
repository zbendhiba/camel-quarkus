/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.quarkus.component.couchbase.deployment;

import java.util.ArrayList;
import java.util.List;

import com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.couchbase.client.java.json.JacksonTransformers;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import org.jboss.logging.Logger;

class CouchbaseProcessor {
    private static final Logger LOG = Logger.getLogger(CouchbaseProcessor.class);

    private static final String FEATURE = "camel-couchbase";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    // com/couchbase/client/core/msg/query/TargetedQueryRequest


    @BuildStep
    List<ReflectiveClassBuildItem> registerReflectiveClasses() {
        List<ReflectiveClassBuildItem> items = new ArrayList<ReflectiveClassBuildItem>();
      /*  items.add(new ReflectiveClassBuildItem(true, true, JacksonTransformers.class));
        items.add(new ReflectiveClassBuildItem(true, true, "com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.AfterburnerModule"));*/
        //items.add(new ReflectiveClassBuildItem(true, true, "com.couchbase.client.core.deps.com.fasterxml.jackson.module.afterburner.util.MyClassLoader"));
     //   items.add(new ReflectiveClassBuildItem(true, true, "com.couchbase.client.core.msg.query.TargetedQueryRequest"));
       // items.add(new ReflectiveClassBuildItem(true, true, "com/couchbase/client/core/deps/com/fasterxml/jackson/databind/Module"));
        return items;
    }

}
