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
package org.apache.camel.quarkus.component.atlasmap.graalvm;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import io.atlasmap.core.AtlasUtil;
import io.atlasmap.core.CompoundClassLoader;
import io.atlasmap.core.DefaultAtlasContextFactory;
import io.atlasmap.core.DefaultAtlasModuleInfo;
import io.atlasmap.spi.AtlasModule;
import io.atlasmap.spi.AtlasModuleDetail;
import io.atlasmap.spi.AtlasModuleInfo;
import io.atlasmap.spi.AtlasModuleInfoRegistry;

public class Substitutions {
}

@TargetClass(className = "io.atlasmap.core.DefaultAtlasCompoundClassLoader")
final class DefaultAtlasCompoundClassLoader_Substitute {
    @Alias
    private Set<ClassLoader> delegates;

    @Substitute
    public synchronized void addAlternativeLoader(ClassLoader cl) {
        delegates.add(Thread.currentThread().getContextClassLoader());
    }

    /*  @Substitute
    private Set<ClassLoader> classLoaders() {
        delegates = new LinkedHashSet<>();
        delegates.add(Thread.currentThread().getContextClassLoader());
        return delegates;
    }
    
    @Substitute
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        System.out.println("*************** classLoader is " + classLoader + ",  loading class  : " + name);
        try {
            return classLoader.loadClass(name);
        } catch (Exception e) {
            System.out.println("************************* !! Class '{}' was not found with ClassLoader '{}': {}");
            e.printStackTrace();
    
        }
        throw new ClassNotFoundException(name);
    }*/
}
/*
@TargetClass(DefaultAtlasContext.class)
final class DefaultAtlasContext_Substitute {
    @Alias
    private Map<String, AtlasModule> sourceModules;
    @Alias
    private Map<String, AtlasModule> targetModules;

    @Substitute
    public Map<String, AtlasModule> getSourceModules() {
        System.out.println("**************** sourceModules");
        for (Map.Entry<String, AtlasModule> entry : sourceModules.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        return this.sourceModules;
    }

    @Substitute
    public Map<String, AtlasModule> getTargetModules() {
        System.out.println("**************** targetModules");
        for (Map.Entry<String, AtlasModule> entry : targetModules.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        return this.targetModules;
    }
}*/

@TargetClass(DefaultAtlasContextFactory.class)
final class DefaultAtlasContextFactory_Sub {
    @Alias
    private CompoundClassLoader classLoader;

    @Alias
    private AtlasModuleInfoRegistry moduleInfoRegistry;

    @Substitute
    protected void loadModules(String moduleClassProperty, Class<?> moduleInterface) {
        Class<?> moduleClass = null;
        String moduleClassName = null;
        Set<String> serviceClasses = new HashSet<>();
        System.out.println("*************************** Loading modules ***********");

        try {
            Enumeration<URL> urls = classLoader.getResources("META-INF/services/atlas/module/atlas.module");
            while (urls.hasMoreElements()) {
                URL tmp = urls.nextElement();
                System.out.println("*************************** Loading URL : " + tmp);
                System.out.println("*************************** Loading file : " + tmp.getFile());
                Properties prop = AtlasUtil.loadPropertiesFromURL(tmp);
                String serviceClassPropertyValue = (String) prop.get(moduleClassProperty);
                System.out.println("*************************** serviceClassPropertyValue : " + serviceClassPropertyValue);
                String[] splitted = serviceClassPropertyValue != null ? serviceClassPropertyValue.split(",") : new String[0];
                for (String entry : splitted) {
                    if (!AtlasUtil.isEmpty(entry)) {
                        serviceClasses.add((entry));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("************ Error loading module resources " + e);
        }

        System.out.println("*************************** Loading services ***********");
        for (String clazz : serviceClasses) {
            System.out.println("*************************** Loading service : " + clazz);
            try {
                moduleClass = classLoader.loadClass(clazz);
                moduleClassName = moduleClass.getName();

                if (isClassAtlasModule(moduleClass, moduleInterface)) {
                    @SuppressWarnings("unchecked")
                    Class<AtlasModule> atlasModuleClass = (Class<AtlasModule>) moduleClass;
                    Constructor<AtlasModule> constructor = atlasModuleClass.getDeclaredConstructor();
                    if (constructor != null) {
                        AtlasModuleInfo module = new DefaultAtlasModuleInfo(getModuleName(moduleClass),
                                getModuleUri(moduleClass), atlasModuleClass, constructor,
                                getSupportedDataFormats(moduleClass), getConfigPackages(moduleClass));
                        moduleInfoRegistry.register(module);
                    } else {
                        System.out.println("Invalid module class {}: constructor is not present; " + moduleClassName);
                    }
                } else {
                    System.out.println("Invalid module class {}: unsupported AtlasModule : " + moduleClassName);
                }
            } catch (NoSuchMethodException e) {
                System.out.println(String.format("Invalid module class %s: constructor is not present. " + moduleClassName));
            } catch (ClassNotFoundException e) {
                System.out.println(String.format("Invalid module class %s: not found in classLoader. " + moduleClassName));
            } catch (Exception e) {
                System.out.println(String.format("Invalid module class %s: unknown error. " + moduleClassName));
            }
        }

        System.out.println("Loaded: {} of {} detected modules  " + moduleInfoRegistry.size() + " : " + serviceClasses.size());
    }

    @Substitute
    protected boolean isClassAtlasModule(Class<?> clazz, Class<?> moduleInterface) {
        if (clazz == null) {
            return false;
        } else if (this.isAtlasModuleInterface(clazz, moduleInterface) && clazz.isAnnotationPresent(AtlasModuleDetail.class)) {
            System.out.println(
                    "{} is a '{}' implementation:: " + clazz.getCanonicalName() + " : " + moduleInterface.getSimpleName());

            return true;
        } else {
            System.out.println(
                    "{} is NOT a '{}' implementation:: " + clazz.getCanonicalName() + " : " + moduleInterface.getSimpleName());
            return false;
        }
    }

    @Substitute
    protected String getModuleName(Class<?> clazz) {
        AtlasModuleDetail detail = (AtlasModuleDetail) clazz.getAnnotation(AtlasModuleDetail.class);
        return detail != null ? detail.name() : "UNDEFINED-" + UUID.randomUUID().toString();
    }

    @Substitute
    protected String getModuleUri(Class<?> clazz) {
        AtlasModuleDetail detail = (AtlasModuleDetail) clazz.getAnnotation(AtlasModuleDetail.class);
        return detail != null ? detail.uri() : "UNDEFINED";
    }

    @Substitute
    protected List<String> getSupportedDataFormats(Class<?> clazz) {
        List<String> dataFormats = null;
        AtlasModuleDetail detail = (AtlasModuleDetail) clazz.getAnnotation(AtlasModuleDetail.class);
        if (detail != null) {
            dataFormats = new ArrayList();
            String[] formats = detail.dataFormats();
            String[] var5 = formats;
            int var6 = formats.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String format = var5[var7];
                dataFormats.add(format.trim());
            }
        }

        System.out.println("Module: {} supports data formats: {} : " + clazz.getCanonicalName() + " : " + dataFormats);

        return dataFormats;
    }

    @Substitute
    protected List<String> getConfigPackages(Class<?> clazz) {
        List<String> configPackages = null;
        AtlasModuleDetail detail = (AtlasModuleDetail) clazz.getAnnotation(AtlasModuleDetail.class);
        if (detail != null) {
            configPackages = new ArrayList();
            String[] packages = detail.configPackages();
            String[] var5 = packages;
            int var6 = packages.length;

            for (int var7 = 0; var7 < var6; ++var7) {
                String pkg = var5[var7];
                configPackages.add(pkg.trim());
            }
        }

        System.out.println("Module: {} config packages: {} " + clazz.getCanonicalName() + " : " + configPackages);

        return configPackages;
    }

    @Substitute
    protected boolean isAtlasModuleInterface(Class<?> clazz, Class<?> moduleInterface) {
        if (clazz == null) {
            return false;
        } else {
            boolean isIface = false;
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                isIface = this.isAtlasModuleInterface(superClazz, moduleInterface);
            }

            Class<?>[] interfaces = clazz.getInterfaces();
            Class[] var6 = interfaces;
            int var7 = interfaces.length;

            for (int var8 = 0; var8 < var7; ++var8) {
                Class<?> iface = var6[var8];
                if (iface.equals(moduleInterface)) {
                    isIface = true;
                }
            }

            return isIface;
        }
    }
}
