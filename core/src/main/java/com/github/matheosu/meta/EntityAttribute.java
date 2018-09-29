package com.github.matheosu.meta;

@FunctionalInterface
public interface EntityAttribute extends Attribute {

    default String get(Attribute attribute) {
        return get() + DOT + attribute.get();
    }

}
