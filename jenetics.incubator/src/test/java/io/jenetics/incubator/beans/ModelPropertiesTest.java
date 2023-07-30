package io.jenetics.incubator.beans;

import java.util.Comparator;

import org.testng.annotations.Test;

import io.jenetics.incubator.beans.property.Property;

public class ModelPropertiesTest {

    @Test
    public void foo() {
        final var model = new ModelProperties("adf");

        model.properties(Property.filtering(PathEntry::path, Path.filter("*")))
            .forEach(System.out::println);

        model.properties(Property.filtering(PathEntry::value, value -> value.type() == String.class))
            .forEach(System.out::println);
    }

}
