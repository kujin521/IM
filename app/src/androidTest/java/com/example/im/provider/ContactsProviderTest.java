package com.example.im.provider;

import org.junit.Test;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

import static org.junit.Assert.*;

public class ContactsProviderTest {

    @Test
    public void insert() {

    }

    @Test
    public void delete() {
    }

    @Test
    public void update() {
    }

    @Test
    public void query() {
    }

    @Test
    public void pinyin(){
        //String name = PinyinHelper.convertToPinyinString("名字", "分隔符", 拼音格式);
        String name = PinyinHelper.convertToPinyinString("名字", "", PinyinFormat.WITHOUT_TONE);
        System.out.println(name);
    }
}