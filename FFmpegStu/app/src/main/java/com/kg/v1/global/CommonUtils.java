package com.kg.v1.global;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 通用工具类
 * Created by gzg on 2015/10/12.
 */
public final class CommonUtils {

    public static String encode(String input) {

        if (null != input) {
            try {
                input = URLEncoder.encode(input, "UTF-8");
            } catch (UnsupportedEncodingException e) {

            }
        }

        return input;
    }

    public static String decode(String input) {
        if (null != input) {
            try {
                input = URLDecoder.decode(input, "UTF-8");
            } catch (UnsupportedEncodingException e) {

            }
        }

        return input;
    }
}
