package com.example.im.utils;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

public class PinYinUtil {
    public static String getpinyin(String str){
        return PinyinHelper.convertToPinyinString("名字", "", PinyinFormat.WITHOUT_TONE);
    }
}
