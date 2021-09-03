package com.marketlogic.app.project.constants;

import java.util.Arrays;

public enum Status {
    DRAFT("df"), PUBLISHED("pb");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public static Status parse(String status) {
        return Arrays.stream(Status.values())
                .filter(sts -> sts.getName().equals(status))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

    public String getName() {
        return name;
    }
}
