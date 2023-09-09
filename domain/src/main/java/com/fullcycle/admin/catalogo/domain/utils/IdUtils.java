package com.fullcycle.admin.catalogo.domain.utils;

import java.util.UUID;

public final class IdUtils {

    private IdUtils() {
    }

    public static String uuid() {
        return UUID.randomUUID().toString().toLowerCase().replace("-", "");
    }

    public static String videoIdOf(final String filePath) {
        final var beginIndex = filePath.indexOf('-');
        final var endIndex = filePath.indexOf('/');
        return filePath.substring(beginIndex + 1, endIndex);
    }
}
