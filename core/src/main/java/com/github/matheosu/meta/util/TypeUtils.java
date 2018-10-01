package com.github.matheosu.meta.util;

import com.github.matheosu.meta.Meta;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public final class TypeUtils {

    private TypeUtils() {
    }

    public static Meta createFromVariable(VariableElement element) {
        String filedName = element.getSimpleName().toString();

        DeclaredType typeMirror = (DeclaredType) element.asType();
        TypeElement elementSymbol = (TypeElement) typeMirror.asElement();
        String className = elementSymbol.getQualifiedName().toString();

        // Define Type
        Meta meta;
        try {
            Class clazz = Class.forName(className);
            if (clazz.isPrimitive() || clazz.isEnum() ||
                    Number.class.isAssignableFrom(clazz) || Boolean.class.getName().equals(className) ||
                    CharSequence.class.isAssignableFrom(clazz) || UUID.class.isAssignableFrom(clazz) ||
                    Date.class.getSimpleName().equals(className) || Calendar.class.getSimpleName().equals(className) ||
                    Temporal.class.isAssignableFrom(clazz)) {
                meta = new Meta(Meta.Type.ATTRIBUTE);
            } else if (Collection.class.isAssignableFrom(clazz)) {
                meta = new Meta(Meta.Type.COLLECTION);
                meta.setTypeArguments(typeMirror.getTypeArguments().get(0).toString());
            } else {
                meta = new Meta(Meta.Type.ENTITY);
            }
        } catch (ClassNotFoundException e) {
            meta = new Meta(Meta.Type.ENTITY);
        }

        meta.setField(filedName);
        meta.setValue(filedName);
        meta.setClassName(className);

        return meta;
    }

}
