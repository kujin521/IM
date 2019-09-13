package com.example.im.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.im.R;
import com.example.im.dbhelper.SmsOpenHelper;
import com.example.im.provider.SmsProvider;
import com.example.im.service.IMService;
import com.example.im.utils.ThreadUtils;
import com.example.im.utils.ToastUtil;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.tv_tilte)
    TextView tvTilte;
    @BindView(R.id.listview)
    ListView listview;
    @BindView(R.id.et_body)
    EditText etBody;
    @BindView(R.id.btn_send)
    Button btnSend;

    private String nickname;
    private String account;
    private ChatManager chatManager;
    private MyMessageListener myMessageListener;
    private String TAG="chatactivity";
    private CursorAdapter madapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        init();
        initView();
        initData();
        initListener();
    }

    private void init() {
        account = getIntent().getStringExtra("account");
        nickname = getIntent().getStringExtra("nickname");
        registerContentObserver();

    }

    private void initView() {
        //设置标题
        tvTilte.setText("与"+nickname+"聊天中");

    }

    private void initData() {
        setAdapterorNotify();

    }

    private void setAdapterorNotify() {
        //判断adapter是否存在
        if (madapter!=null){
            //刷新
            madapter.getCursor().requery();
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                Cursor c=getContentResolver().query(SmsProvider.URI_SMS,null,null,null,
                        "time asc");
                //如果没有数据直接返回
                if (c.getCount()<1){
                    return;
                }
                ThreadUtils.runInUIThread(new Runnable() {
                    @Override
                    public void run() {
                        //CusorAdapter :getView-->newView-->bindView
                        madapter = new CursorAdapter(ChatActivity.this,c) {
                            @Override
                            public int getViewTypeCount() {
                                //接收--》如果当前账号 不等于 消息创建者
                                
                                return super.getViewTypeCount()+1;//2种情况样式（0接收1发送）
                            }

                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                return super.getView(position, convertView, parent);
                            }

                            @Override
                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                                return null;
                            }

                            @Override
                            public void bindView(View view, Context context, Cursor cursor) {

                            }

                            //                            @Override//如果converView==null的时候会调用--》返回跟布局
//                            public View newView(Context context, Cursor cursor, ViewGroup parent) {
//                                TextView textView = new TextView(context);
//                                return textView;
//                            }
//
//                            @Override
//                            public void bindView(View view, Context context, Cursor cursor) {
//                                //具体设置数据
//                                String body = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
//                                TextView tv= (TextView) view;
//                                tv.setText(body);
//                            }
                        };
                        listview.setAdapter(madapter);
                    }
                });

            }
        });
    }

    private void initListener() {

    }

    @OnClick(R.id.btn_send)
    public void onViewClicked() {
        String body = etBody.getText().toString();
        ToastUtil.showToastSafe(this,body);
        ThreadUtils.runInThread(new Runnable() {

            private Chat chat;

            @Override
            public void run() {
                try {
                    //获取消息管理者
                    if (chatManager==null){
                        chatManager = IMService.conn.getChatManager();
                    }

                    //创建聊天对象
                    //chatManager.createChat("发送的对象",消息的监听者);
                    if (myMessageListener==null){
                        myMessageListener = new MyMessageListener();
                    }
                    chat = chatManager.createChat(account, myMessageListener);
                    //发送消息
                    Message msg=new Message();
                    msg.setFrom(IMService.account);
                    msg.setTo(account);
                    msg.setBody(body);
                    msg.setType(Message.Type.chat);
                    chat.sendMessage(msg);

                    //保存消息
                    saveMessage(account,msg);

                    //更新ui
                    ThreadUtils.runInUIThread(new Runnable() {
                        @Override
                        public void run() {
                            etBody.setText("");
                        }
                    });
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 保存消息--》contentResoler--》contentProvider-->sqlite
     * @param sessionAccount 对方是谁
     * @param msg
     */
    private void saveMessage(String sessionAccount,Message msg) {
        ContentValues values=new ContentValues();
        values.put(SmsOpenHelper.SmsTable.FROM_ACCOUNT,msg.getFrom());
        values.put(SmsOpenHelper.SmsTable.TO_ACCOUNT,msg.getTo());
        values.put(SmsOpenHelper.SmsTable.BODY,msg.getBody());
        values.put(SmsOpenHelper.SmsTable.STATUS,"offline");
        values.put(SmsOpenHelper.SmsTable.TYPE,msg.getType().name());
        values.put(SmsOpenHelper.SmsTable.TIME,System.currentTimeMillis());
        values.put(SmsOpenHelper.SmsTable.SESSION_ACCOUNT,sessionAccount);
        getContentResolver().insert(SmsProvider.URI_SMS,values);
    }

    class MyMessageListener implements MessageListener{
        @Override
        public void processMessage(Chat chat, Message message) {
            String body = message.getBody();
            Log.d(TAG, "processMessage: "+message.getBody());
            //收到消息，保存消息
            saveMessage(chat.getParticipant(),message);
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterContentObserver();
        super.onDestroy();
    }

    /*===============使用ContentObserver时刻监听数据的改变

    /**
     * 注册监听
     */
    public void registerContentObserver(){
        getContentResolver().registerContentObserver(SmsProvider.URI_SMS,true,myContentObserver);
    }

    /**
     * 反注册监听
     */
    public void unRegisterContentObserver(){
        getContentResolver().unregisterContentObserver(myContentObserver);
    }
    MyContentObserver myContentObserver=new MyContentObserver(new Handler());
    class MyContentObserver extends ContentObserver{

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 接收到数据的改变
         * @param selfChange
         */
        @Override
        public void onChange(boolean selfChange) {
            //设置adpter或者notifyadapter
            setAdapterorNotify();
            super.onChange(selfChange);
        }
    }

}
