package com.ctrlwe.quaero.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility helpers for formatting and parsing {@link LocalDateTime} values.
 *
 * <p>All formatters are thread-safe {@link DateTimeFormatter} instances
 * cached as constants to avoid repeated allocations.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class DateTimeUtil {

    private DateTimeUtil() {
        // Utility class — prevent instantiation
    }

    /**
     * ISO-8601 date-time formatter ({@code yyyy-MM-dd'T'HH:mm:ss}).
     */
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Human-readable date-time formatter ({@code dd MMM yyyy, hh:mm a}).
     *
     * <p>Example output: {@code 25 Jul 2026, 12:15 PM}</p>
     */
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    /**
     * Date-only formatter ({@code yyyy-MM-dd}).
     */
    private static final DateTimeFormatter DATE_ONLY_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Formats a {@link LocalDateTime} to an ISO-8601 string.
     *
     * @param dateTime the date-time to format; must not be {@code null}
     * @return formatted string, e.g. {@code "2026-07-25T12:15:00"}
     */
    public static String formatIso(LocalDateTime dateTime) {
        return dateTime.format(ISO_FORMATTER);
    }

    /**
     * Formats a {@link LocalDateTime} to a human-readable display string.
     *
     * @param dateTime the date-time to format; must not be {@code null}
     * @return formatted string, e.g. {@code "25 Jul 2026, 12:15 PM"}
     */
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }

    /**
     * Formats a {@link LocalDateTime} to a date-only string.
     *
     * @param dateTime the date-time to format; must not be {@code null}
     * @return formatted string, e.g. {@code "2026-07-25"}
     */
    public static String formatDateOnly(LocalDateTime dateTime) {
        return dateTime.format(DATE_ONLY_FORMATTER);
    }

    /**
     * Parses an ISO-8601 string into a {@link LocalDateTime}.
     *
     * @param text the text to parse, e.g. {@code "2026-07-25T12:15:00"}
     * @return the parsed {@link LocalDateTime}
     * @throws java.time.format.DateTimeParseException if the text cannot be parsed
     */
    public static LocalDateTime parseIso(String text) {
        return LocalDateTime.parse(text, ISO_FORMATTER);
    }

    /**
     * Returns the current date-time formatted as an ISO-8601 string.
     *
     * @return the current timestamp, e.g. {@code "2026-07-25T12:15:00"}
     */
    public static String nowIso() {
        return formatIso(LocalDateTime.now());
    }
}
