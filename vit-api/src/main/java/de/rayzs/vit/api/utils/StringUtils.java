package de.rayzs.vit.api.utils;

import java.util.HashMap;
import java.util.Map;

public class StringUtils {

    /**
     * Looks for the String which is being searched inside the source
     * String. If it finds a match, it will return the index of the
     * last letter of the first found match.
     *
     * @param searching String to be searched.
     * @param source Source String where to search.
     *
     * @return Index of the last letter of the first found match. -1 if nothing is found.
     */
    public static int searchIndex(final String searching, final String source) {
        char[] sourceChars = source.toCharArray();

        int s = 0;
        for (int i = 0; i < sourceChars.length; i++) {
            if (s == searching.length()) {
                return i;
            }

            if (sourceChars[i] != searching.charAt(s)) {
                s = 0;
                continue;
            }

            s++;
        }

        return -1;
    }

    /**
     * Takes a number as input and formats it into something
     * more "nicer". Example:
     *
     * <pre>
     *  {@code
     *      StringUtils.formatNumber(1);
     *      // Out: "+1"
     *
     *      StringUtils.formatNumber(0);
     *      // Out: "0"
     *
     *      StringUtils.formatNumber(0.5);
     *      // Out: "0,5"
     *
     *      StringUtils.formatNumber(-1);
     *      // Out: "-1"
     *  }
     * </pre>
     *
     * @param number Number to convert into a nicer String format.
     *
     * @return Formatted number.
     */
    public static String formatNumber(final double number) {
        final String prefix = number > 0 ? "+" : "";

        if (number % 1 == 0) {
            return prefix + (int) number;
        }

        return (prefix + number).replace(".", ",");
    }

    /**
     * Automatically modifies a String by replacing all of its
     * arguments with the corresponding value.
     *
     * <pre>
     *  {@code
     *      String raw       = "I am %name% and like the number %num%!";
     *      String formatted = replace(raw, "%num%", 2, %name%, "Player");
     *      // Out: I am Player and like the number 2!
     *  }
     * </pre>
     *
     * @param source Source String.
     * @param replacements Replacements.
     *
     * @return Formatted String.
     */
    public static String replace(String source, final String... replacements) {

        // Map of replacements: %num%, 23
        final Map<String, String> map = new HashMap<>();

        String key = null;
        for (final String part : replacements) {

            if (key == null) {
                key = part;
                continue;
            }

            map.put(key, part);
            key = null;
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            source = source.replace(entry.getKey(), entry.getValue());
        }

        return source;
    }
}
