package com.github.matheosu.meta;

@FunctionalInterface
public interface CollectionAttribute extends Attribute {

    default String get(Attribute attribute) {
        return get() + DOT + attribute.get();
    }

}
