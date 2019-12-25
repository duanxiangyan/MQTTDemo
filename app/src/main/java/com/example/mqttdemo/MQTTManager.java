package com.example.mqttdemo;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.greenrobot.eventbus.EventBus;

public class MQTTManager {
    public static final String TAG = MQTTManager.class.getSimpleName();

    //服务器地址
    private String host = "tcp://192.168.100.31:61613";
    //用户名
    private String userName = "admin";
    //密码
    private String passWord = "password";
    //客户端唯一标识
    private String clientId = MqttClient.generateClientId();
    //客户端
    private MqttClient client;
    //连接参数
    private MqttConnectOptions connectOptions;

    /**
     * 建立连接
     */
    public void connect(){
        try{
            client = new MqttClient(host,clientId,new MemoryPersistence());
            connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(userName);
            connectOptions.setPassword(passWord.toCharArray());
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setCleanSession(true);
            connectOptions.setKeepAliveInterval(10);
            connectOptions.setConnectionTimeout(10);
            client.setCallback(mqttCallbackExtended);
            client.connect(connectOptions);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    /**
     * 订阅话题
     * @param topic 话题
     * @param qos   服务质量
     */
    public void subscribe(String topic,int qos){
        if(client != null){
            int[] Qos = {qos};
            String[] topic1 = {topic};
            try {
                client.subscribe(topic1, Qos);
                Log.e(TAG,"订阅话题 : "+topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发布话题
     * @param topic       话题
     * @param msg         消息
     * @param isRetained  是否保留发布的消息
     * @param qos         服务质量
     */
    public void publish(String topic,String msg,boolean isRetained,int qos) {
        try {
            if (client!=null) {
                MqttMessage message = new MqttMessage();
                message.setQos(qos);
                message.setRetained(isRetained);
                message.setPayload(msg.getBytes());
                client.publish(topic, message);
                Log.e(TAG,topic+":"+msg);
                EventBus.getDefault().post(new PublishEvent("发布的话题：" + topic,"发布的消息：" + msg));
            }
        } catch (MqttPersistenceException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.e(TAG,"连接成功");
        }

        @Override
        public void connectionLost(Throwable cause) {
            Log.e(TAG,"连接丢失:"+ cause.toString());
        }

        @Override
        public void messageArrived(String topic, MqttMessage message){
            Log.e(TAG,"接收到的话题 : " + topic);
            String payload = new String(message.getPayload());
            Log.e(TAG,"接收到的消息 : " + payload);
            EventBus.getDefault().post(new PublishEvent("接收到的话题：" + topic,"接收到的消息：" + payload));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            Log.e(TAG,"发布消息成功");
        }
    };

}
