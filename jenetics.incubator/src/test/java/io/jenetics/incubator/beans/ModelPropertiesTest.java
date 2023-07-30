package io.jenetics.incubator.beans;

import org.testng.annotations.Test;

import io.jenetics.incubator.beans.property.Property;

public class ModelPropertiesTest {

    @Test
    public void foo() {
        final var model = new ModelProperties("adf");

        model.stream(
                Matcher.matching(
                    Property::value,
                    Property.Value::value,
                    String.class::isInstance
                )
            )
            .forEach(System.out::println);

        //model.properties(Property.filtering(PathValue::value, value -> value.type() == String.class))
        //    .forEach(System.out::println);
    }

}
