package com.alexeykovzel.example.utils;

public class StringUtils {
    public static String toCamelCase(String value) {
        char[] chars = value.toLowerCase().toCharArray();
        char[] result = new char[chars.length];
        char prev = ' ';
        for (int i = 0; i < chars.length; i++) {
            result[i] = prev == ' ' || prev == '.'
                    ? Character.toUpperCase(chars[i])
                    : chars[i];
            prev = chars[i];
        }
        return String.valueOf(result);
    }

    public static String formatNumber(double num) {
        if (num > 1.0e+9) return String.format("%.2fB", num / 1.0e+9);
        if (num > 1.0e+6) return String.format("%.2fM", num / 1.0e+9);
        if (num > 1.0e+3) return String.format("%.2fK", num / 1.0e+9);
        return String.format("%.2f", num);
    }
}
