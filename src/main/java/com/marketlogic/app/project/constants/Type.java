package com.marketlogic.app.project.constants;

import java.util.Arrays;

public enum Type {
    TYPE_1("t1"),
    TYPE_2("t2"),
    TYPE_3("t3");

    private final String name;

    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Type parse(String type) {
        return Arrays.stream(Type.values())
                .filter(typ -> typ.getName().equals(type))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

}
