package com.ctrlwe.quaero.common.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility helpers for validating HTTP and HTTPS URLs.
 *
 * <p>Uses {@link URI} and {@link java.net.URL} for strict RFC-compliant
 * parsing rather than regular expressions, which are prone to edge-case
 * failures.</p>
 *
 * @author Quaero Engineering
 * @since 1.0
 */
public final class UrlValidationUtil {

    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private UrlValidationUtil() {
        // Utility class — prevent instantiation
    }

    /**
     * Validates that the given string is a well-formed HTTP or HTTPS URL.
     *
     * @param url the URL string to validate
     * @return {@code true} if the string is a valid HTTP/HTTPS URL,
     *         {@code false} otherwise (including {@code null} and blank input)
     */
    public static boolean isValidHttpUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(url);
            // Force full URL parsing (scheme + authority)
            uri.toURL();
            String scheme = uri.getScheme();
            return SCHEME_HTTP.equalsIgnoreCase(scheme)
                    || SCHEME_HTTPS.equalsIgnoreCase(scheme);
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates that the given string is a well-formed HTTPS-only URL.
     *
     * @param url the URL string to validate
     * @return {@code true} if the string is a valid HTTPS URL,
     *         {@code false} otherwise
     */
    public static boolean isValidHttpsUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(url);
            uri.toURL();
            return SCHEME_HTTPS.equalsIgnoreCase(uri.getScheme());
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return false;
        }
    }
}
