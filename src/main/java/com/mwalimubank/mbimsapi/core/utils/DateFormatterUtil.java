package com.mwalimubank.mbimsapi.core.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

public final class DateFormatterUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private DateFormatterUtil() {}

    public static String format(LocalDate date) {
        return date != null ? date.format(FORMATTER) : null;
    }

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toLocalDate().format(FORMATTER) : null;
    }

    public static String format(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneOffset.UTC).toLocalDate().format(FORMATTER);
    }

    public static String format(OffsetDateTime odt) {
        if (odt == null) return null;
        return odt.toLocalDate().format(FORMATTER);
    }

    /**
     * Safe entry point when the field type varies (Instant / LocalDateTime / etc.)
     */
    public static String format(Object value) {
        if (value == null) return null;
        if (value instanceof Instant i) return format(i);
        if (value instanceof LocalDateTime ldt) return format(ldt);
        if (value instanceof LocalDate ld) return format(ld);
        if (value instanceof OffsetDateTime odt) return format(odt);
        return value.toString();
    }
}