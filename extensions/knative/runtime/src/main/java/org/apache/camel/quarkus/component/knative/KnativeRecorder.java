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
package org.apache.camel.quarkus.component.knative;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import org.apache.camel.CamelContext;
import org.apache.camel.component.knative.KnativeComponent;

@Recorder
public class KnativeRecorder {
    /*public RuntimeValue<KnativeComponent> createKnativeComponent(RuntimeValue<Router> router,
            Supplier<Vertx> vertx) {
    
        KnativeComponent knativeComponent = new KnativeComponent();
    
        // consumer factory
        KnativeHttpConsumerFactory consumerFactory = new KnativeHttpConsumerFactory();
        consumerFactory.setRouter(router.getValue());
        knativeComponent.setConsumerFactory(consumerFactory);
    
        // producer factory
        KnativeHttpProducerFactory producerFactory = new KnativeHttpProducerFactory();
        producerFactory.setVertx(vertx.get());
        knativeComponent.setProducerFactory(producerFactory);
    
        // return the component
        return new RuntimeValue<>(knativeComponent);
    }*/

    public RuntimeValue<KnativeComponent> createKnativeComponent(RuntimeValue<CamelContext> camelContextRuntimeValue) {
        CamelContext camelContext = camelContextRuntimeValue.getValue();
        System.out.println("********************************* Creating the Knative component ************");
        return new RuntimeValue<>(new KnativeComponent(camelContext));
    }
}
