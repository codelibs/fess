/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.util;

import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * Utility class for handling IP addresses, particularly IPv6 addresses in URLs.
 * This class provides methods to properly format IPv6 addresses for use in URLs
 * by adding brackets where necessary.
 */
public final class IpAddressUtil {

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private IpAddressUtil() {
        // Utility class - no instances allowed
    }

    /**
     * Determines if the given address string represents an IPv6 address.
     * This method validates the address using InetAddress to ensure it's a valid IPv6 address.
     *
     * @param address the IP address string to check
     * @return true if the address is a valid IPv6 address, false otherwise
     */
    public static boolean isIPv6Address(final String address) {
        if (address == null) {
            return false;
        }
        try {
            final InetAddress inetAddress = InetAddress.getByName(address);
            return inetAddress instanceof Inet6Address;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Formats an IP address string for use in a URL.
     * IPv6 addresses are wrapped in brackets, IPv4 addresses are returned as-is.
     *
     * @param address the IP address string to format
     * @return the formatted address (IPv6 with brackets, IPv4 unchanged)
     */
    public static String formatForUrl(final String address) {
        if (address == null) {
            return null;
        }
        if (isIPv6Address(address)) {
            // If already has brackets, return as-is
            if (address.startsWith("[") && address.endsWith("]")) {
                return address;
            }
            // Add brackets for IPv6
            return "[" + address + "]";
        }
        return address;
    }

    /**
     * Compresses an IPv6 address string to its canonical compressed form.
     * For example, "0:0:0:0:0:0:0:1" becomes "::1"
     *
     * @param ipv6Address the IPv6 address string to compress
     * @return the compressed IPv6 address string
     */
    protected static String compressIPv6(final String ipv6Address) {
        if (ipv6Address == null || ipv6Address.isEmpty()) {
            return ipv6Address;
        }

        // Expand :: if present to get full address for normalization
        final String expandedAddress;
        if (ipv6Address.contains("::")) {
            final String[] parts = ipv6Address.split("::");
            final String leftPart = parts.length > 0 && !parts[0].isEmpty() ? parts[0] : "";
            final String rightPart = parts.length > 1 && !parts[1].isEmpty() ? parts[1] : "";
            final int leftCount = leftPart.isEmpty() ? 0 : leftPart.split(":").length;
            final int rightCount = rightPart.isEmpty() ? 0 : rightPart.split(":").length;
            final int zerosCount = 8 - leftCount - rightCount;
            final StringBuilder expanded = new StringBuilder(leftPart);
            for (int i = 0; i < zerosCount; i++) {
                if (expanded.length() > 0) {
                    expanded.append(":");
                }
                expanded.append("0");
            }
            if (!rightPart.isEmpty()) {
                if (expanded.length() > 0) {
                    expanded.append(":");
                }
                expanded.append(rightPart);
            }
            expandedAddress = expanded.toString();
        } else {
            expandedAddress = ipv6Address;
        }

        // Split address into parts
        final String[] parts = expandedAddress.split(":");
        if (parts.length != 8) {
            // Not a standard IPv6 address, return as-is
            return ipv6Address;
        }

        // Normalize each part (remove leading zeros)
        final String[] normalized = new String[8];
        for (int i = 0; i < 8; i++) {
            normalized[i] = parts[i].replaceFirst("^0+(?!$)", "");
            if (normalized[i].isEmpty()) {
                normalized[i] = "0";
            }
        }

        // Find longest sequence of consecutive zeros
        int longestStart = -1;
        int longestLength = 0;
        int currentStart = -1;
        int currentLength = 0;

        for (int i = 0; i < 8; i++) {
            if ("0".equals(normalized[i])) {
                if (currentStart == -1) {
                    currentStart = i;
                }
                currentLength++;

                if (currentLength > longestLength) {
                    longestStart = currentStart;
                    longestLength = currentLength;
                }
            } else {
                currentStart = -1;
                currentLength = 0;
            }
        }

        // If longest sequence is only 1 zero, don't compress
        if (longestLength <= 1) {
            return String.join(":", normalized);
        }

        // Build compressed address
        final StringBuilder result = new StringBuilder();
        boolean inCompression = false;

        for (int i = 0; i < 8; i++) {
            if (i >= longestStart && i < longestStart + longestLength) {
                // We're in the compression zone
                if (!inCompression) {
                    // Start of compression - add ::
                    result.append("::");
                    inCompression = true;
                }
                // Skip this zero
            } else {
                // Normal part
                inCompression = false;
                if (result.length() > 0 && result.charAt(result.length() - 1) != ':') {
                    result.append(":");
                }
                result.append(normalized[i]);
            }
        }

        return result.toString();
    }

    /**
     * Generates a URL-safe host string from an InetAddress.
     * For IPv6 addresses, this wraps the address in brackets and compresses it.
     * For IPv4 addresses, returns the address as-is.
     *
     * @param address the InetAddress to format
     * @return the URL-safe host string
     */
    public static String getUrlHost(final InetAddress address) {
        if (address == null) {
            return null;
        }
        if (address instanceof Inet6Address) {
            final String hostAddress = address.getHostAddress();
            // Remove zone ID if present (e.g., %eth0)
            final int percentIndex = hostAddress.indexOf('%');
            final String cleanAddress = percentIndex >= 0 ? hostAddress.substring(0, percentIndex) : hostAddress;
            // Compress the IPv6 address
            final String compressed = compressIPv6(cleanAddress);
            return "[" + compressed + "]";
        }
        return address.getHostAddress();
    }

    /**
     * Builds a URL from protocol, InetAddress, port, and path.
     * Properly handles IPv6 addresses by wrapping them in brackets.
     *
     * @param protocol the protocol (e.g., "http", "https")
     * @param address the InetAddress for the host
     * @param port the port number
     * @param path the path (should start with "/" or be empty)
     * @return the complete URL string
     */
    public static String buildUrl(final String protocol, final InetAddress address, final int port, final String path) {
        if (protocol == null || address == null) {
            return null;
        }
        final String host = getUrlHost(address);
        final StringBuilder url = new StringBuilder();
        url.append(protocol).append("://").append(host);
        if (port > 0) {
            url.append(":").append(port);
        }
        if (path != null && !path.isEmpty()) {
            if (!path.startsWith("/")) {
                url.append("/");
            }
            url.append(path);
        }
        return url.toString();
    }

    /**
     * Builds a URL from protocol, hostname string, port, and path.
     * Properly handles IPv6 addresses by wrapping them in brackets if needed.
     *
     * @param protocol the protocol (e.g., "http", "https")
     * @param host the hostname or IP address string
     * @param port the port number
     * @param path the path (should start with "/" or be empty)
     * @return the complete URL string
     */
    public static String buildUrl(final String protocol, final String host, final int port, final String path) {
        if (protocol == null || host == null) {
            return null;
        }
        final String formattedHost = formatForUrl(host);
        final StringBuilder url = new StringBuilder();
        url.append(protocol).append("://").append(formattedHost);
        if (port > 0) {
            url.append(":").append(port);
        }
        if (path != null && !path.isEmpty()) {
            if (!path.startsWith("/")) {
                url.append("/");
            }
            url.append(path);
        }
        return url.toString();
    }
}
