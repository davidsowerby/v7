/*
 * Copyright (c) 2015. David Sowerby
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.q3c.krail.i18n;

import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ClassUtils;
import uk.q3c.krail.core.user.opt.Option;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by David Sowerby on 25/11/14.
 */
public class ClassBundleWriter<E extends Enum<E>> extends BundleWriterBase<E> {

    public String classJavaDoc;
    private Class<?> clazz;
    private EnumMap<E, String> entryMap;
    private List<Class<?>> imports = new ArrayList<>();
    private Class<?> keyClass;
    private String pkg;
    private Class<?> superClass;

    @Inject
    protected ClassBundleWriter(Option option) {
        super(option);
    }

    @Override
    public void setBundle(EnumResourceBundle<E> bundle) {
        super.setBundle(bundle);
        this.clazz = bundle.getClass();
        this.keyClass = bundle.getKeyClass();
        this.superClass = clazz.getSuperclass();
        this.entryMap = bundle.getMap();
        this.pkg = ClassUtils.getPackageCanonicalName(clazz);
        classJavaDoc = "Generated by Krail " + LocalDateTime.now()
                                                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    /**
     * Writes out the class as a sub-class of EnumResourceBundle.  If bundleName is present, uses that as the class
     * name.  If it is not present, uses the bundleName from the bundle's I18NKey.  If there are no keys defined, uses
     * 'Unknown' as the class name
     *
     * @param locale
     * @param bundleName
     *         if present, use as the class name
     *
     * @throws IOException
     */
    @Override
    public void write(Locale locale, Optional<String> bundleName) throws IOException {
        String bundleNameWithLocale = bundleNameWithLocale(locale, bundleName);

        StringBuilder buf = new StringBuilder();
        buf.append("package ")
           .append(pkg)
           .append(";\n\n");

        String indent = "    ";
        String indent2 = indent + indent;

        List<Class<?>> imps = imports;
        for (Class<?> clazz : imps) {
            buf.append("import ")
               .append(clazz.getName())
               .append(";\n");
        }
        buf.append("\n");

        buf.append("/**\n* ")
           .append(classJavaDoc)
           .append("\n*\n*/\n");

        buf.append("public class ")
           .append(bundleNameWithLocale)
           .append(" extends ")
           .append(genericSuperClass())
           .append(" {\n\n");

        buf.append(indent)
           .append("@Override\n");
        buf.append(indent)
           .append("protected void loadMap() {\n");

        //transfer to a TreeMap to sort by key
        SortedMap<String, String> sortedMap = new TreeMap<>();
        entryMap.forEach((k,v) ->{sortedMap.put(k.name(),v);});


        for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
            buf.append(indent2)
               .append("put(")
               .append(keyClass.getSimpleName())
               .append(".")
               .append(entry.getKey())
               .append(", \"")
               .append(entry.getValue())
               .append("\");\n");
        }
        buf.append(indent)
           .append("}\n");
        buf.append("}\n");


        File file = new File(getOptionWritePath(), bundleNameWithLocale + ".java");
        FileUtils.writeStringToFile(file, buf.toString());
    }

    private String genericSuperClass() {
        if (superClass.equals(EnumResourceBundle.class)) {
            return superClass.getSimpleName() + "<" + keyClass.getSimpleName() + ">";
        }
        return superClass.getSimpleName();

    }


}
