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
package org.apache.camel.quarkus.component.atlasmap.deployment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import io.atlasmap.core.DefaultAtlasContextFactory;
import io.atlasmap.core.DefaultAtlasModuleInfo;
import io.atlasmap.java.v2.Modifier;
import io.atlasmap.java.v2.ModifierList;
import io.atlasmap.json.v2.JsonEnumFields;
import io.atlasmap.json.v2.JsonFields;
import io.atlasmap.mxbean.AtlasContextFactoryMXBean;
import io.atlasmap.mxbean.AtlasModuleInfoMXBean;
import io.atlasmap.spi.AtlasConverter;
import io.atlasmap.spi.AtlasFieldAction;
import io.atlasmap.v2.ADMDigest;
import io.atlasmap.v2.Action;
import io.atlasmap.v2.AreaUnitType;
import io.atlasmap.v2.AtlasMapping;
import io.atlasmap.v2.BaseMapping;
import io.atlasmap.v2.CollectionType;
import io.atlasmap.v2.Constant;
import io.atlasmap.v2.Constants;
import io.atlasmap.v2.DataSource;
import io.atlasmap.v2.DataSourceMetadata;
import io.atlasmap.v2.DataSourceType;
import io.atlasmap.v2.DistanceUnitType;
import io.atlasmap.v2.Field;
import io.atlasmap.v2.FieldAction;
import io.atlasmap.v2.FieldStatus;
import io.atlasmap.v2.FieldType;
import io.atlasmap.v2.InspectionType;
import io.atlasmap.v2.LookupEntry;
import io.atlasmap.v2.LookupTable;
import io.atlasmap.v2.LookupTables;
import io.atlasmap.v2.MappingFileType;
import io.atlasmap.v2.MappingType;
import io.atlasmap.v2.Mappings;
import io.atlasmap.v2.MassUnitType;
import io.atlasmap.v2.Multiplicity;
import io.atlasmap.v2.NumberType;
import io.atlasmap.v2.Properties;
import io.atlasmap.v2.Property;
import io.atlasmap.v2.StringList;
import io.atlasmap.v2.ValueContainer;
import io.atlasmap.v2.VolumeUnitType;
import io.atlasmap.xml.v2.NodeType;
import io.atlasmap.xml.v2.Restriction;
import io.atlasmap.xml.v2.RestrictionType;
import io.atlasmap.xml.v2.XmlNamespace;
import io.atlasmap.xml.v2.XmlNamespaces;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.IndexDependencyBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;
import io.quarkus.deployment.util.ServiceUtil;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;

class AtlasmapProcessor {
    private static final String FEATURE = "camel-atlasmap";
    private static final String ATLASMAP_SERVICE_BASE = "META-INF/services/";

    private static final DotName ATLASMAP_FIELD = DotName.createSimple("io.atlasmap.v2.Field");
    private static final DotName ATLASMAP_DATASOURCE = DotName.createSimple("io.atlasmap.v2.DataSource");
    private static final DotName ATLASMAP_BASEMAPPING = DotName.createSimple("io.atlasmap.v2.BaseMapping");
    private static final DotName ATLASMAP_MODULE = DotName.createSimple("io.atlasmap.core.BaseAtlasModule");

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<ReflectiveClassBuildItem> registerReflectiveNamedClasses() {
        List<ReflectiveClassBuildItem> items = new ArrayList<>();
        // classes that do not implement an interface or extend a method
        items.add(new ReflectiveClassBuildItem(false, true, false, AtlasContextFactoryMXBean.class));
        items.add(new ReflectiveClassBuildItem(false, true, false, AtlasModuleInfoMXBean.class));
        items.add(new ReflectiveClassBuildItem(true, false, Action.class));
        items.add(new ReflectiveClassBuildItem(true, false, ADMDigest.class));
        items.add(new ReflectiveClassBuildItem(true, false, AtlasMapping.class));
        items.add(new ReflectiveClassBuildItem(true, false, BaseMapping.class));
        items.add(new ReflectiveClassBuildItem(true, false, Constant.class));
        items.add(new ReflectiveClassBuildItem(true, false, Constants.class));
        items.add(new ReflectiveClassBuildItem(true, false, DataSource.class));
        // the field isSource don't have a proper getter, the @JsonProperty is needed, so both methods and fields need to be registred
        items.add(new ReflectiveClassBuildItem(true, true, DataSourceMetadata.class));
        items.add(new ReflectiveClassBuildItem(true, false, DefaultAtlasContextFactory.class));
        items.add(new ReflectiveClassBuildItem(true, false, DefaultAtlasModuleInfo.class));
        items.add(new ReflectiveClassBuildItem(true, false, Field.class));
        items.add(new ReflectiveClassBuildItem(true, false, FieldAction.class));
        items.add(new ReflectiveClassBuildItem(true, false, JsonEnumFields.class));
        items.add(new ReflectiveClassBuildItem(true, false, JsonFields.class));
        items.add(new ReflectiveClassBuildItem(true, false, Mappings.class));
        items.add(new ReflectiveClassBuildItem(true, false, LookupEntry.class));
        items.add(new ReflectiveClassBuildItem(true, false, LookupTable.class));
        items.add(new ReflectiveClassBuildItem(true, false, LookupTables.class));
        items.add(new ReflectiveClassBuildItem(true, false, ModifierList.class));
        items.add(new ReflectiveClassBuildItem(true, false, Properties.class));
        items.add(new ReflectiveClassBuildItem(true, false, Property.class));
        items.add(new ReflectiveClassBuildItem(true, false, ValueContainer.class));
        items.add(new ReflectiveClassBuildItem(true, false, Restriction.class));
        items.add(new ReflectiveClassBuildItem(true, false, StringList.class));
        items.add(new ReflectiveClassBuildItem(true, false, XmlNamespace.class));
        items.add(new ReflectiveClassBuildItem(true, false, XmlNamespaces.class));

        // enums
        items.add(new ReflectiveClassBuildItem(false, true, InspectionType.class));
        items.add(new ReflectiveClassBuildItem(false, true, FieldType.class));
        items.add(new ReflectiveClassBuildItem(false, true, CollectionType.class));
        items.add(new ReflectiveClassBuildItem(false, true, AreaUnitType.class));
        items.add(new ReflectiveClassBuildItem(false, true, NumberType.class));
        items.add(new ReflectiveClassBuildItem(false, true, DistanceUnitType.class));
        items.add(new ReflectiveClassBuildItem(false, true, VolumeUnitType.class));
        items.add(new ReflectiveClassBuildItem(false, true, DataSourceType.class));
        items.add(new ReflectiveClassBuildItem(false, true, Multiplicity.class));
        items.add(new ReflectiveClassBuildItem(false, true, FieldStatus.class));
        items.add(new ReflectiveClassBuildItem(false, true, MappingType.class));
        items.add(new ReflectiveClassBuildItem(false, true, MappingFileType.class));
        items.add(new ReflectiveClassBuildItem(false, true, MassUnitType.class));
        items.add(new ReflectiveClassBuildItem(false, true, Modifier.class));
        items.add(new ReflectiveClassBuildItem(false, true, RestrictionType.class));
        items.add(new ReflectiveClassBuildItem(false, true, NodeType.class));
        return items;
    }

    @BuildStep
    NativeImageResourceBuildItem resource() {
        return new NativeImageResourceBuildItem("META-INF/services/atlas/module/atlas.module");
    }

    @BuildStep
    void addExternalDependencies(BuildProducer<IndexDependencyBuildItem> indexDependency) {
        // common model dependency
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-model"));
        // CSV model and module dependencies
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-csv-model"));
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-csv-module"));
        // DFDL model and module dependencies
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-dfdl-model"));
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-dfdl-module"));
        // Java model and module dependencies
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-java-model"));
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-java-module"));
        // Json model and module dependencies
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-json-model"));
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-json-module"));
        // XML model and module dependencies
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-xml-model"));
        indexDependency.produce(new IndexDependencyBuildItem("io.atlasmap", "atlas-xml-module"));
    }

    @BuildStep
    ReflectiveClassBuildItem registerReflectiveSubClasses(CombinedIndexBuildItem combinedIndex) {
        IndexView index = combinedIndex.getIndex();
        Set<ClassInfo> allKnownImplementors = new HashSet<>();
        // search classes thanks to external dependencies
        allKnownImplementors.addAll(index.getAllKnownSubclasses(ATLASMAP_FIELD));
        allKnownImplementors.addAll(index.getAllKnownSubclasses(ATLASMAP_BASEMAPPING));
        allKnownImplementors.addAll(index.getAllKnownSubclasses(ATLASMAP_DATASOURCE));
        allKnownImplementors.addAll(index.getAllKnownSubclasses(ATLASMAP_MODULE));
        String[] dtos = allKnownImplementors.stream()
                .map(classInfo -> classInfo.name().toString())
                .toArray(String[]::new);
        return new ReflectiveClassBuildItem(true, false, dtos);
    }

    @BuildStep
    void registerNativeImageResources(BuildProducer<ServiceProviderBuildItem> services,
            BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
        Stream.of(
                AtlasConverter.class.getName(),
                AtlasFieldAction.class.getName(),
                Action.class.getName())
                .forEach(service -> {
                    try {
                        Set<String> implementations = ServiceUtil.classNamesNamedIn(
                                Thread.currentThread().getContextClassLoader(),
                                ATLASMAP_SERVICE_BASE + service);
                        services.produce(
                                new ServiceProviderBuildItem(service,
                                        implementations.toArray(new String[0])));

                        // register those classes for reflection too
                        // we don't need to add external dependencies for the services
                        String[] dtos = implementations.stream()
                                .toArray(String[]::new);
                        reflectiveClass.produce(new ReflectiveClassBuildItem(true, false, dtos));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
