package com.zkketech.project.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Root on 2018/4/22.
 */

public class StringUtils {


    public  StringUtils(){
        throw  new UnsupportedOperationException("no ");
    }



    public List<String> stringToList(String strs){
        String str[] = strs.split(",");
        return Arrays.asList(str);
    }


    public static String listToString(List<String> list){
        if(list==null){
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean first = true;
        //第一个前面不拼接","
        for(String string :list) {
            if(first) {
                first=false;
            }else{
                result.append(",");
            }
            result.append(string);
        }
        return result.toString();
    }
}
