package com.hnu.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern mobile_Pattern = Pattern.compile("1\\d{10}");
    //用正则表达式校验电话号码
    public static boolean isMobile(String str){
        if(StringUtils.isEmpty(str)){
            return false;
        }
        Matcher matcher = mobile_Pattern.matcher(str);
        return matcher.matches();
    }

//    public static void main(String[] args) {
//        System.out.println(isMobile("13974770895"));
//    }
}
