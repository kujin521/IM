package com.example.im.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.im.R;
import com.example.im.activity.ChatActivity;
import com.example.im.dbhelper.ContactOpenHelper;
import com.example.im.provider.ContactsProvider;
import com.example.im.utils.ThreadUtils;

/**
 * 联系人
 */
public class ContactsFragment extends Fragment {
    private ListView listView;
    private CursorAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        iniData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    private void init() {
        registerContentObserver();
    }

    private void initView(View view) {
        listView=view.findViewById(R.id.listview);
    }

    private void iniData() {


        //开启线程同步花名册
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                //设置adapter，显示数据
                setAdapter();
            }
        });
    }

    //设置adapter，显示数据
    private void setAdapter() {

        //如果adapter存在，刷新即可
        if (adapter!=null){
            adapter.getCursor().requery();
            return;
        }

        //对应的查询记录
        Cursor query = getActivity().getContentResolver().query(ContactsProvider.URI_CONTACT,
                null, null, null, null);

        //假如没有数据库
        if (query.getCount()<=0){
            return;
        }

        ThreadUtils.runInUIThread(new Runnable() {
            @Override
            public void run() {
                adapter = new CursorAdapter(getActivity(), query) {
                    @Override//如果consorView==null，返回一个具体的根视图
                    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                        View view = View.inflate(context, R.layout.item_contact, null);
                        return view;
                    }

                    @Override//设置数据，显示数据
                    public void bindView(View view, Context context, Cursor cursor) {
                        ImageView imgeHead = view.findViewById(R.id.head);
                        TextView textAccount = view.findViewById(R.id.account);
                        TextView textnickname = view.findViewById(R.id.nickname);
                        String account = query.getString(query.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                        String nickname = query.getString(query.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
                        imgeHead.setBackgroundResource(R.drawable.meinv);
                        textAccount.setText(account);
                        textnickname.setText(nickname);
                    }
                };

                listView.setAdapter(adapter);
            }
        });
    }



    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //拿到jid(账号)--昵称
                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(position);
                String account = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                String nickname = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("account",account);
                intent.putExtra("nickname",nickname);
                startActivity(intent);
            }
        });
    }



    MyContentObserver myContentObserver=new MyContentObserver(new Handler());

    //注册监听
    public void registerContentObserver(){
        getActivity().getContentResolver().registerContentObserver(ContactsProvider.URI_CONTACT,
                true,myContentObserver);

    }

    //反注册监听
    public void unRegisterContentObserver(){
        getActivity().getContentResolver().unregisterContentObserver(myContentObserver);
    }


    /*========监听数据库的改变========*/
    class MyContentObserver extends ContentObserver{

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 如果数据库发生改变会在这个方法收到通知
         * @param selfChange
         * @param uri
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //更新adapter
            setAdapter();
        }
    }

    @Override
    public void onDestroy() {
        /**
         * 按照常理，我们fragment销毁了，那么我们不应继续监听
         * 但是我们需要一直监听roster
         * 所引，我们把联系人监听和同步放到Service里
         */
        unRegisterContentObserver();

        super.onDestroy();
    }
}
