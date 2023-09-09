package com.fullcycle.admin.catalogo.infrastructure.utils;

public final class SqlUtils {

    private SqlUtils() {
    }

    public static String like(final String term) {
        return "%" + term + "%";
    }

    public static String upper(final String term) {
        return term.toUpperCase();
    }
}
