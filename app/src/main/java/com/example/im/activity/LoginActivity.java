package com.example.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.im.R;
import com.example.im.service.IMService;
import com.example.im.utils.ThreadUtils;
import com.example.im.utils.ToastUtil;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends Activity {

    @BindView(R.id.et_username)
    EditText etUsername;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    private static final String host="10.178.0.195";
    private static final int port=5222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        String username=etUsername.getText().toString().trim();
        String password=etPassword.getText().toString().trim();
        if (username.isEmpty()){
            etUsername.setError("用户名不能为空！");
            return;
        }
        if (password.isEmpty()){
            etPassword.setError("密码不能为空！");
            return;
        }
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建连接配置对象
                    ConnectionConfiguration config=new ConnectionConfiguration(host,port);
                    //额外配置
                    config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//禁止安全模式（明文传输，方便调试）
                    config.setDebuggerEnabled(true);//开启调试模式，方便我们查看具体调试的内容
                    //开始创建连接对象
                    XMPPConnection connection=new XMPPConnection(config);
                    connection.connect();
                    //开始登录
                    connection.login(username,password);

                    //如果没出错，就启动服务，监听联系人

                    IMService.conn=connection;
                    String account=username+"@im.kj.com";
                    IMService.account=account;//admin-->admin@im.kj.com
                    startService(new Intent(LoginActivity.this,IMService.class));

                    ToastUtil.showToastSafe(LoginActivity.this,"登陆成功");
                    finish();
                    //跳到主界面
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));

                } catch (XMPPException e) {
                    e.printStackTrace();
                    ToastUtil.showToastSafe(LoginActivity.this,"登陆失败");
                }
            }
        });


    }
}
