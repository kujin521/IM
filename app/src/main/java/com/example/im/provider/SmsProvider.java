package com.example.im.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.im.dbhelper.SmsOpenHelper;

/**
 * 消息提供者
 */
public class SmsProvider extends ContentProvider {
    private static final String AUTHORITIES=SmsProvider.class.getCanonicalName();//得到完整的类路径（主机名称）

    static UriMatcher mUriMatcher;//匹配uri（用于分辨查询那个数据库表）
    public static Uri URI_SMS= Uri.parse("content://" + AUTHORITIES + "/sms");
    public static Uri URI_SESSION= Uri.parse("content://"+AUTHORITIES+"/session");

    private static final int SMS=1;
    private static int SESSION=2;

    static {
        mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        //添加匹配规则
        mUriMatcher.addURI(AUTHORITIES,"/sms", SMS);
    }

    private SmsOpenHelper helper;
    private int delete;
    private int update;
    private Cursor query;

    @Override
    public boolean onCreate() {
        //创建表，创建数据库
        helper = new SmsOpenHelper(getContext());
        if (helper!=null)
            return true;
        else
            return false;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch (mUriMatcher.match(uri)){//uri用于指定哪一个数据源
            case SMS:
                long id = helper.getWritableDatabase().insert(SmsOpenHelper.T_SMS, "", values);
                if (id>0){
                    System.out.println("==========SmsProvider Insert Success");
                    uri= ContentUris.withAppendedId(uri,id);
                    //发送数据改变的信号
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }else {
                    System.out.println("==========SmsProvider Insert F");
                }
                break;

                default:
                    break;
        }
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (mUriMatcher.match(uri)){//uri用于指定哪一个数据源
            case SMS:
                delete = helper.getWritableDatabase().delete(SmsOpenHelper.T_SMS, selection, selectionArgs);
                if (delete >0){
                    System.out.println("-----------delete success-----");
                    //发送数据改变的信号
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }else {
                    System.out.println("-------delete f----------");
                }
                break;

            default:
                break;
        }
        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (mUriMatcher.match(uri)){//uri用于指定哪一个数据源
            case SMS:
                update = helper.getWritableDatabase().update(SmsOpenHelper.T_SMS, values, selection, selectionArgs);
                if (update >0){
                    System.out.println("--------------update success-----");
                    //发送数据改变的信号
                    getContext().getContentResolver().notifyChange(SmsProvider.URI_SMS,null);
                }
                break;

            default:
                break;
        }
        return update;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (mUriMatcher.match(uri)){//uri用于指定哪一个数据源
            case SMS:
                query = helper.getReadableDatabase().query(SmsOpenHelper.T_SMS, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                break;
        }
        return query;
    }
}
