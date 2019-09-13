package com.example.im.service;


import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.im.dbhelper.ContactOpenHelper;
import com.example.im.provider.ContactsProvider;
import com.example.im.utils.PinYinUtil;
import com.example.im.utils.ThreadUtils;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;

public class IMService extends Service {
    public static String account;
    private static String TAG = "myservice";

    private Roster roster;
    private MRosterListener rosterListener;
    public static XMPPConnection conn;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        //同步联系人
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: 同步花名册");
                /**
                 * 获取联系人对象
                 * 需要连接对象
                 */
                roster = conn.getRoster();
                Collection<RosterEntry> entries = roster.getEntries();

                //监听联系人数据变化
                rosterListener = new MRosterListener();
                roster.addRosterListener(rosterListener);

                //遍历所有的联系人进行数据更新
                for (RosterEntry entry : entries) {
                    savaOrUpdataEntry(entry);
                }
            }
        });
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (roster!=null&&rosterListener!=null){
            roster.removeRosterListener(rosterListener);
        }
        super.onDestroy();
    }

    private class MRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> address) {//联系人添加了
            Log.d("MyRosterListener", "entriesAdded: --------------");
            for (String s : address) {
                RosterEntry entry = roster.getEntry(s);
                savaOrUpdataEntry(entry);
            }


        }

        @Override
        public void entriesUpdated(Collection<String> address) {//联系人修改了
            Log.d("MyRosterListener", "entriesUpdated: --------------");
            for (String s : address) {
                RosterEntry entry = roster.getEntry(s);
                savaOrUpdataEntry(entry);
            }
        }

        @Override
        public void entriesDeleted(Collection<String> address) {//联系人删除了
            Log.d("MyRosterListener", "entriesDeleted: --------------");
            for (String s : address) {
                getContentResolver().delete(ContactsProvider.URI_CONTACT,
                        ContactOpenHelper.ContactTable.ACCOUNT+"=?",new String[]{s});
            }
        }

        @Override
        public void presenceChanged(Presence presence) {//联系人状态改变了
            Log.d("MyRosterListener", "presenceChanged: --------------");

        }
    }



    //保存或更新联系人信息
    public void savaOrUpdataEntry(RosterEntry entry) {
        ContentValues values = new ContentValues();
        String user = entry.getUser();
        String name = entry.getName();
        //处理昵称为空的情况
        if (name == null || "".equals(name)) {
            user.substring(0, user.indexOf("@"));
        }
        values.put(ContactOpenHelper.ContactTable.ACCOUNT, user);
        values.put(ContactOpenHelper.ContactTable.NICKNAME, name);
        values.put(ContactOpenHelper.ContactTable.AVATAR, "0");
        values.put(ContactOpenHelper.ContactTable.PINYIN, PinYinUtil.getpinyin(user));

        //先update--》插入
        int update = getContentResolver().update(ContactsProvider.URI_CONTACT, values,
                ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{user});
        if (update <= 0) {//没有更新数据
            getContentResolver().insert(ContactsProvider.URI_CONTACT, values);
        }
    }
}

