package com.example.mqttdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity{

    private MQTTManager mqttManager;
    //订阅的话题
    private EditText et_subscriber;
    private Button btn_subscriber;
    //发布的话题
    private EditText et_publish_topic;
    //发布的消息
    private EditText et_publish_message;
    private Button btn_publish;
    //发布或订阅的话题和消息
    private TextView tv_topic;
    private TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        mqttManager = new MQTTManager();
        mqttManager.connect();

        btn_subscriber = findViewById(R.id.btn_subscriber);
        btn_publish = findViewById(R.id.btn_publish);
        et_subscriber = findViewById(R.id.et_subscriber);
        et_publish_topic = findViewById(R.id.et_publish_topic);
        et_publish_message = findViewById(R.id.et_publish_message);
        tv_topic = findViewById(R.id.tv_topic);
        tv_message = findViewById(R.id.tv_message);

        btn_subscriber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_subscriber.getText().toString().trim())){
                    Toast.makeText(MainActivity.this,"订阅的话题不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    mqttManager.subscribe(et_subscriber.getText().toString().trim(),0);
                }

            }
        });

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(et_publish_topic.getText().toString().trim()) && TextUtils.isEmpty(et_publish_message.getText().toString().trim())){
                    Toast.makeText(MainActivity.this,"发布的话题或消息不能为空！",Toast.LENGTH_SHORT).show();
                }else {
                    mqttManager.publish(et_publish_topic.getText().toString().trim(),et_publish_message.getText().toString().trim(),false,0);
                }
            }
        });

    }

    /**
     * 更新UI
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateEvent(PublishEvent event){
        tv_topic.setText(event.getTopic());
        tv_message.setText(event.getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
