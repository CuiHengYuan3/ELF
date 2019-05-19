package com.example.lenovo.elf;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class WelcomeActivity extends AppCompatActivity {
    private int time=2;
    final Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    time--;
                    Log.e("TAG",time+"");
                    if (time>0){
                        handler.sendMessageDelayed(handler.obtainMessage(1),1000);
                    }
//                    else {
//                        //启动页面
//                        startMainActivity();
//                    }
                    break;
            }
            super.handleMessage(msg);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        handler.sendMessageDelayed(handler.obtainMessage(1), 1000);

        //延迟3秒后进入主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //执行在主线程
                //启动页面
                startMainActivity();
            }
        },3000);

    }

    private void startMainActivity() {
        startActivity(new Intent(WelcomeActivity.this,HomeActivity.class));
        //关闭当前页面
        finish();
    }


    }


