package com.github.matheosu.meta;

public class Meta {

    private Type type;
    private String value;
    private String field;
    private String className;

    public Meta(Type type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public enum Type {
        ATTRIBUTE(Attribute.class),
        ENTITY(EntityAttribute.class),
        COLLECTION(CollectionAttribute.class),
        ;

        private final Class<?> clazz;

        Type(Class<?> clazz) {
            this.clazz = clazz;
        }


        public String getName() {
            return clazz.getSimpleName();
        }

        public String getFqdn() {
            return clazz.getName();
        }
    }
}
