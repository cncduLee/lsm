/**
 * Copyright (c) 2014, shouli1990@gmail.com|shouli1990@gmail.com. All rights reserved.
 *
 */
package com.bit.lsm.client.utils;

/**
 * <b>项目名</b>： lsm <br>
 * <b>包名称</b>： com.bit.lsm.client.utils <br>
 * <b>类名称</b>： StringUtils <br>
 * <b>类描述</b>： <br>
 * <b>创建人</b>： <a href="mailto:shouli1990@gmail.com">李朋明</a> <br>
 * <b>修改人</b>： <br>
 * <b>创建时间</b>：2015/2/20 14:16<br>
 * <b>修改时间</b>： <br>
 * <b>修改备注</b>： <br>
 *
 * @version 1.0.0 <br>
 */
public class StringUtils {
    public static final String SEPARATOR = ";";
    public static final String EX_PATH_SEP = ",";
    public static final String EX_PATH_RES_PREFIX = "@";
    public static final String EX_PATH_CON_SEP = "/";

    public static String nonNull(String t) {
        return t == null || "".equals(t) ? "null" : "\"" + t + "\"";
    }

    public static String stringNull(String t) {
        return t == null ? "" : t;
    }

    public static StringBuffer sbNull(StringBuffer sb) {
        return sb == null ? new StringBuffer("") : sb;
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isEquals(CharSequence src, CharSequence cs) {
        return src == null && cs == null || src != null && src.equals(cs);

    }

    public static boolean isNumber(char c) {
        return Character.isDigit(c);
    }

    public static boolean match(String uString, String configString) {
        if (uString == null)
            uString = "";
        if (configString != null) {
            if (configString.contains(uString)) {
                return true;
            }
            String[] cs = configString.split(",");
            for (String s : cs) {
                boolean isPattern = false;
                if (s.contains("*")) {
                    s = s.replaceAll("\\*", ".*");
                    isPattern = true;
                }
                if (s.contains("?")) {
                    s = s.replaceAll(
                            "\\?", ".{1}");
                    isPattern = true;
                }
                if ((isPattern && uString.matches(s)) || (!isPattern && uString.equals(s))) {
                    return true;
                }

            }
        }
        return false;
    }
}
