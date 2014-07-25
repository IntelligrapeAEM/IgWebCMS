package com.ig.igwebcms.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * This Class is used to convert multiple String arguments or a String List into
 * simple String Object with a defined separator value.
 */
public class Strings {
    /**
     * @param separator String value which will work as a separator between
     *                  all the values of String arguments.
     * @param elements  elements is a var argument element that can
     *                  have multiple string parameter passed to this method.
     * @return It returns a String value after concatenating all the string arguments.
     */
    public static String join(final String separator, final String... elements) {
        return StringUtils.join(elements, separator);
    }

    /**
     * @param separator String value which will work as a separator between
     *                  all the values of given String list.
     * @param elements  It is a List Object that can have multiple string values
     *                  to be converted into one string.
     * @return It returns a String value after concatenating all the string
     *         values of the elements list.
     */
    public static String join(final String separator, final List<String> elements) {
        return join(separator, elements.toArray(new String[0]));
    }
/*
    public static String convertStreamToString(InputStream is)
            throws IOException {

        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
*/
}
