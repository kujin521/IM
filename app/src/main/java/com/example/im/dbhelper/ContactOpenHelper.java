package com.example.im.dbhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ContactOpenHelper extends SQLiteOpenHelper {

    public static final String T_contact="t_contact";

    public class ContactTable implements BaseColumns{//就是会默认添加一列 _id
        /**
         * _id:主键
         * account:账号
         * nickname:别名
         * avatar:头像
         * pinyin:账号拼音
         */
        public static final String ACCOUNT="account";
        public static final String NICKNAME="nickname";
        public static final String AVATAR="avatar";
        public static final String PINYIN="pinyin";
    }


    public ContactOpenHelper(Context context) {
        super(context, "contact.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql="CREATE TABLE "+T_contact+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +ContactTable.ACCOUNT+" text, "
                +ContactTable.NICKNAME+" text, "
                +ContactTable.AVATAR+" text, "
                +ContactTable.PINYIN+" text)";
        sqLiteDatabase.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
