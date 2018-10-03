package com.github.matheosu.meta;

@FunctionalInterface
public interface EntityAttribute extends Attribute {

    @SuppressWarnings("unchecked")
    default <T extends Attribute> T get(T attribute) {
        Attribute attr = () -> name() + DOT + attribute.name();
        return (T) attr;
    }

}
