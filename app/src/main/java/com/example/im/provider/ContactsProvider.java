package com.example.im.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.im.dbhelper.ContactOpenHelper;

public class ContactsProvider extends ContentProvider {
    //获取类的完整路径(主机地址的常量)
    public static final String authorities=ContactsProvider.class.getCanonicalName();
    //地址匹配对象
    static UriMatcher muriMacher;
    private static final int CONTACT=1;
    //对应联系人表的uri常量
    public static Uri URI_CONTACT=Uri.parse("content://"+authorities+"/contact");

    static {
        muriMacher=new UriMatcher(UriMatcher.NO_MATCH);
        //添加一个匹配规则
        muriMacher.addURI(authorities,"/contact", CONTACT);
    }

    private ContactOpenHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper=new ContactOpenHelper(getContext());
        if (mHelper!=null)
            return true;
        return false;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
    /*-----增删改查------*/
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //数据存到sqllite-->创建db文件，建立表--》sqlopenhelper
        int code = muriMacher.match(uri);
        switch (code){
            case CONTACT:
                SQLiteDatabase writableDatabase = mHelper.getWritableDatabase();
                long id = writableDatabase.insert(ContactOpenHelper.T_contact, "", contentValues);
                if (id!=-1){
                    System.out.println("---------------insertSuccess--------------");
                    //拼接最新的uri
                    uri = ContentUris.withAppendedId(uri, id);
                    //通知observer数据改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,null);
                }
                break;
                default:
                    break;
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int code = muriMacher.match(uri);
        int deleteCount=0;
        switch (code){
            case CONTACT:
                SQLiteDatabase writableDatabase = mHelper.getWritableDatabase();
                deleteCount = writableDatabase.delete(ContactOpenHelper.T_contact, s, strings);
                if (deleteCount>0){
                    System.out.println("--------------- delete-------------------");
                    //通知observer数据改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,null);
                }
                break;
            default:
                break;
        }
        return deleteCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int code = muriMacher.match(uri);
        int updateCount=0;
        switch (code){
            case CONTACT:
                SQLiteDatabase writableDatabase = mHelper.getWritableDatabase();
                updateCount = writableDatabase.update(ContactOpenHelper.T_contact, contentValues, s, strings);
                if (updateCount>0){
                    System.out.println("--------------- updateCount-------------------");
                    //通知observer数据改变了
                    getContext().getContentResolver().notifyChange(ContactsProvider.URI_CONTACT,null);
                }
                break;
            default:
                break;
        }
        return updateCount;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        int code = muriMacher.match(uri);
        Cursor query=null;
        int updateCount=0;
        switch (code){
            case CONTACT:
                SQLiteDatabase db = mHelper.getReadableDatabase();
                query = db.query(ContactOpenHelper.T_contact, strings, s, strings1, null, null, s1);
                if (query!=null){
                    System.out.println("--------------- query-------------------");
                }
                break;
            default:
                break;
        }
        return query;
    }
}
