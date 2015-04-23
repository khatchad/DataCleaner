package org.datacleaner.documentation;

import org.apache.metamodel.util.HasName;
import org.datacleaner.api.MappedProperty;
import org.datacleaner.descriptors.ConfiguredPropertyDescriptor;

import com.google.common.base.Strings;
import com.google.common.html.HtmlEscapers;

/**
 * A wrapper around the {@link ConfiguredPropertyDescriptor} object to make it
 * easier for the documentation template to get to certain aspects that should
 * be presented in the documentation.
 */
public class ConfiguredPropertyDocumentationWrapper {

    private final ConfiguredPropertyDescriptor _property;

    public ConfiguredPropertyDocumentationWrapper(ConfiguredPropertyDescriptor property) {
        _property = property;
    }

    public String getName() {
        return _property.getName();
    }

    public String getDescription() {
        return Strings.nullToEmpty(_property.getDescription());
    }

    public boolean isRequired() {
        return _property.isRequired();
    }

    public boolean isArray() {
        return _property.isArray();
    }

    public String[] getEnumChoices() {
        final Class<?> baseType = _property.getBaseType();
        if (!baseType.isEnum()) {
            return new String[0];
        }
        final Enum<?>[] constants = (Enum<?>[]) baseType.getEnumConstants();
        final String[] result = new String[constants.length];
        for (int i = 0; i < constants.length; i++) {
            final Enum<?> constant = constants[i];
            if (constant instanceof HasName) {
                result[i] = ((HasName) constant).getName();
            } else {
                result[i] = constant.toString();
            }
            result[i] = HtmlEscapers.htmlEscaper().escape(result[i]);
        }
        return result;
    }

    public String getType() {
        final StringBuilder sb = new StringBuilder();
        if (_property.isArray()) {
            sb.append("List of ");
        }

        final Class<?> baseType = _property.getBaseType();
        if (baseType.isEnum()) {
            // don't show the actual name of the enum, it is irrelevant to the
            // user - show the choices he has
            sb.append("Choice:");
        } else {
            sb.append(baseType.getSimpleName());

            final int typeArgumentCount = _property.getTypeArgumentCount();
            if (typeArgumentCount > 0) {
                sb.append("<");
                for (int i = 0; i < typeArgumentCount; i++) {
                    final Class<?> typeArgument = _property.getTypeArgument(i);
                    if (i > 0) {
                        sb.append(',');
                    }
                    sb.append(typeArgument.getSimpleName());
                }
                sb.append(">");
            }
        }

        final String result = sb.toString();
        return HtmlEscapers.htmlEscaper().escape(result);
    }

    public boolean isMapped() {
        return _property.getAnnotation(MappedProperty.class) != null;
    }

    public String getMappedDescription() {
        final MappedProperty annotation = _property.getAnnotation(MappedProperty.class);
        return "Mapped with <i>" + annotation.value() + "</i>";
    }
}
