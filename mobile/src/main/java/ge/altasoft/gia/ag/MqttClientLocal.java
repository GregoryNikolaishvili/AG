package ge.altasoft.gia.ag;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.Charset;
import java.util.ArrayList;

import ge.altasoft.gia.ag.classes.AquaControllerData;


public class MqttClientLocal {

    private static final String TOPIC_CHA_NOTIFICATION = "$SYS/broker/connection/cha_wrt_remote/state";

    private static final String TOPIC_CHA_SYS = "aquagod/sys/";
    private static final String TOPIC_CHA_ALERT = "aquagod/alert";
    private static final String TOPIC_CHA_LOG = "aquagod/log/"; // last "/" is important

    private static final String TOPIC_AQUAGOD_ALIVE = "aquagod/alive";
    private static final String TOPIC_AQUAGOD_CONTROLLER_STATE = "aquagod/state";

    private static final String TOPIC_AQUAGOD_SENSOR_STATE = "aquagod/sensor/"; // last "/" is important
    private static final String TOPIC_AQUAGOD_DEVICE_STATE = "aquagod/device/"; // last "/" is important

    private static final String TOPIC_AQUAGOD_SETTINGS = "aquagod/settings";

    static final String MQTT_DATA_TYPE = "ge.altasoft.gia.ag.DATA_TYPE";

    public enum MQTTReceivedDataType {
        WrtState,
        Alert,
        Log,
        ClientConnected,

        AquagodControllerAlive,
        AquagodControllerState,

        AquagodSensorState,
        AquagodDeviceState,

        AquagodSettings
    }

    enum MQTTConnectionStatus {
        ERROR,
        INITIAL,                            // initial status
        CONNECTING,                         // attempting to connect
        CONNECTED,                          // connected
        NOTCONNECTED_USERDISCONNECT,        // user has explicitly requested
        NOTCONNECTED_UNKNOWNREASON          // failed to connect for some reason
    }

    // constants used to tell the Activity UI the connection status
    static final String MQTT_STATUS_INTENT = "ge.altasoft.gia.ag.STATUS";
    static final String MQTT_DATA_INTENT = "ge.altasoft.gia.ag.DATA";
    static final String MQTT_CONN_STATUS = "ge.altasoft.gia.ag.MSG.STATUS";
    static final String MQTT_MSG = "ge.altasoft.gia.ag.MSG";
    static final String MQTT_MSG_IS_ERROR = "ge.altasoft.gia.ag.MSG.IS_ERROR";

    final private Context context;

    private MqttAndroidClient mqttClient = null;

    private final String clientId;
    private String brokerUrl;

    private final ArrayList<String> connectedClients = new ArrayList<>();

    private MQTTConnectionStatus connectionStatus = MQTTConnectionStatus.INITIAL;

    MqttClientLocal(Context context) {
        this.context = context;
        //clientId = Utils.getDeviceName().concat("-").concat(Utils.getDeviceUniqueId(context));
        clientId = Utils.getDeviceName(); //
    }

    ArrayList<String> getConnectedClientList() {
        return connectedClients;
    }

    void start() {
        if (mqttClient != null)
            stop();

        Utils.readUrlSettings(context);
        brokerUrl = "tcp://" + Utils.getMtqqBrokerUrl(context);

        mqttClient = new MqttAndroidClient(context, brokerUrl, "android." + System.currentTimeMillis());
        //mqttClient.registerResources(context);
        mqttClient.setCallback(new MqttCallbackHandler());

        new Thread(new Runnable() {
            @Override
            public void run() {
                connectToBroker();
            }
        }, "MQTTservice_start").start();
    }

    void stop() {
        if (mqttClient == null)
            return;

        broadcastServiceStatus("Disconnecting", false);

        publish(TOPIC_CHA_SYS.concat(clientId), "", true);
        try {
            //mqttClient.unsubscribe("aquagod/#");
            mqttClient.unregisterResources();
            mqttClient.close();
//                IMqttToken disconToken = getMqttClient.disconnect();
//                disconToken.setActionCallback(new IMqttActionListener() {
//                    @Override
//                    public void onSuccess(IMqttToken asyncActionToken) {
//                        Log.d("mqtt", "disconnect.onSuccess");
//                        connectionStatus = MQTTConnectionStatus.NOTCONNECTED_USERDISCONNECT;
//                        broadcastServiceStatus("Disconnected", false);
//                    }
//
//                    @Override
//                    public void onFailure(IMqttToken asyncActionToken,
//                                          Throwable exception) {
//                        // something went wrong, but probably we are disconnected anyway
//                        connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
//                        broadcastServiceStatus("Disconnected", false);
//                    }
//                });
//        } catch (MqttPersistenceException e) {
//            Log.e("mqtt", "disconnect failed - persistence exception", e);
//            broadcastServiceStatus("disconnect failed - persistence exception: " + e.getMessage(), true);
//        } catch (MqttException e) {
//            Log.e("mqtt", "disconnect failed - MQTT exception", e);
//            broadcastServiceStatus("disconnect failed - MQTT exception: " + e.getMessage(), true);
        } finally {
            mqttClient = null;
        }
    }

    public void publish(String topic, String message, boolean retained) {
        if (mqttClient == null)
            return;

        Log.d("mqtt", String.format("publish. topic='%s', payload='%s'", topic, message));

        try {
            byte[] payload;
            if (message.equals(""))
                payload = new byte[0];
            else
                payload = message.getBytes(Charset.defaultCharset());
            mqttClient.publish(topic, payload, 1, retained);
        } catch (MqttException e) {
            Log.e("mqtt", "publish failed - MQTT exception", e);
            broadcastServiceStatus("publish failed - MQTT exception: " + e.getMessage(), true);
        }
    }

    private void broadcastServiceStatus(String statusDescription, boolean isError) {
        Log.i("mqtt", statusDescription);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MQTT_STATUS_INTENT);
        broadcastIntent.putExtra(MQTT_MSG, statusDescription);
        broadcastIntent.putExtra(MQTT_MSG_IS_ERROR, isError);
        broadcastIntent.putExtra(MQTT_CONN_STATUS, connectionStatus);

        context.sendBroadcast(broadcastIntent);
    }

    private synchronized void connectToBroker() {
        Log.d("mqtt", "connecting");
        connectionStatus = MQTTConnectionStatus.CONNECTING;
        broadcastServiceStatus("Connecting...", false);

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setWill(TOPIC_CHA_SYS.concat(clientId), new byte[0], 1, true);

            IMqttToken token = mqttClient.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("mqtt", "connect.onSuccess");
                    if (connectionStatus == MQTTConnectionStatus.CONNECTING) //todo workaround for strange bug. onsuccess was called twice
                    {
                        connectionStatus = MQTTConnectionStatus.CONNECTED;
                        broadcastServiceStatus("Connected", false);

                        publish(TOPIC_CHA_SYS.concat(clientId), "connected", true);

                        publish("aquagodc/refresh", "1", false);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                subscribeToTopics();
                            }
                        }, "MQTTservice_subscribe").start();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
                    Log.e("mqtt", "connect failed", exception);
                    broadcastServiceStatus("Connect failed: " + exception.getMessage(), true);
                    connectToBroker();
                }
            });
        } catch (IllegalArgumentException e) {
            Log.e("mqtt", "connect failed - illegal argument", e);
            broadcastServiceStatus("connect failed - illegal argument: " + e.getMessage(), true);
        } catch (MqttException e) {
            Log.e("mqtt", "connect failed - MQTT exception", e);
            broadcastServiceStatus("connect failed - MQTT exception: " + e.getMessage(), true);
        }
    }

    private synchronized void subscribeToTopics() {
        broadcastServiceStatus("Subscribing...", false);

        try {
            IMqttToken subToken = mqttClient.subscribe("aquagod/#", 1);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("mqtt", "subscribe.onSuccess (aquagod/#)");
                    broadcastServiceStatus("connect.subscribed", false);
                    broadcastServiceStatus(brokerUrl, false);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // The subscription could not be performed, maybe the user was not authorized to subscribe on the specified topic e.g. using wildcards
                    broadcastServiceStatus("subscribe failed: " + exception.getMessage(), true);
                }
            });

            subToken = mqttClient.subscribe("$SYS/broker/connection/cha_wrt_remote/state", 1);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("mqtt", "subscribe.onSuccess (sys)");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // The subscription could not be performed, maybe the user was not authorized to subscribe on the specified topic e.g. using wildcards
                }
            });


        } catch (IllegalArgumentException e) {
            Log.e("mqtt", "subscribe failed - illegal argument", e);
            broadcastServiceStatus("subscribe failed - illegal argument: " + e.getMessage(), true);
        } catch (MqttException e) {
            Log.e("mqtt", "subscribe failed - MQTT exception", e);
            broadcastServiceStatus("subscribe failed - MQTT argument: " + e.getMessage(), true);
        }
    }


    //Checks if the MQTT client thinks it has an active connection
//    private boolean isAlreadyConnected() {
//        return (getMqttClient != null) && getMqttClient.isConnected();
//    }

//    private boolean isOnline() {
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
//        return cm.getActiveNetworkInfo() != null &&
//                cm.getActiveNetworkInfo().isAvailable() &&
//                cm.getActiveNetworkInfo().isConnected();
//    }

    private class MqttCallbackHandler implements MqttCallbackExtended {

        MqttCallbackHandler() {
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            Log.d("mqtt", "connect complete: " + serverURI);
        }

        @Override
        public void connectionLost(Throwable cause) {
            if (cause != null) {
                Log.d("mqtt", "connection lost: " + cause.getMessage());
                broadcastServiceStatus("connection lost: " + cause.getMessage(), false);
            }

            if (mqttClient != null) {
                //mqttClient.unregisterResources();
                mqttClient = null;
            }

            if (cause != null)
                start();
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            String payload = message.toString();

            Log.i("mqtt", String.format("message arrived. topic='%s', payload='%s'", topic, payload));


            Intent broadcastDataIntent = new Intent();
            broadcastDataIntent.setAction(MQTT_DATA_INTENT);
            try {
                switch (topic) {

                    case TOPIC_CHA_NOTIFICATION:
                        broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.WrtState);
                        broadcastDataIntent.putExtra("value", payload.equals("1"));
                        context.sendBroadcast(broadcastDataIntent);
                        return;

                    case TOPIC_CHA_ALERT:
                        broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.Alert);
                        broadcastDataIntent.putExtra("message", payload);
                        context.sendBroadcast(broadcastDataIntent);
                        return;

                    case TOPIC_AQUAGOD_CONTROLLER_STATE:
                        broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.AquagodControllerState);
                        broadcastDataIntent.putExtra("state", Integer.parseInt(payload, 16));
                        context.sendBroadcast(broadcastDataIntent);
                        return;


                    //region Water level
                    case TOPIC_AQUAGOD_ALIVE:
                        if (AquaControllerData.Instance != null)
                            AquaControllerData.Instance.setHaveData();
                        broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.AquagodControllerAlive);
                        broadcastDataIntent.putExtra("BoardTimeInSec", Long.parseLong(payload, 16));
                        context.sendBroadcast(broadcastDataIntent);
                        return;

                    case TOPIC_AQUAGOD_SETTINGS:
                        AquaControllerData.Instance.decodeSettings(payload);
                        broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.AquagodSettings);
                        context.sendBroadcast(broadcastDataIntent);
                        return;

                    //endregion

                }

                if (topic.startsWith(TOPIC_AQUAGOD_SENSOR_STATE)) {
                    int id = Integer.parseInt(topic.substring(TOPIC_AQUAGOD_SENSOR_STATE.length()), 16);

                    AquaControllerData.Instance.DecodeSensorState(id, payload);

                    broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.AquagodSensorState);
                    broadcastDataIntent.putExtra("id", id);
                    context.sendBroadcast(broadcastDataIntent);
                    return;
                }


                if (topic.startsWith(TOPIC_AQUAGOD_DEVICE_STATE)) {
                    int id = Integer.parseInt(topic.substring(TOPIC_AQUAGOD_DEVICE_STATE.length()), 16);

                    AquaControllerData.Instance.DecodeDeviceState(id, payload);

                    broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.AquagodDeviceState);
                    broadcastDataIntent.putExtra("id", id);
                    context.sendBroadcast(broadcastDataIntent);

                    return;
                }

                ////////////////////

                if (topic.startsWith(TOPIC_CHA_LOG)) {
                    String type = topic.substring(TOPIC_CHA_LOG.length());

                    broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.Log);
                    broadcastDataIntent.putExtra("type", type);
                    broadcastDataIntent.putExtra("log", payload);
                    context.sendBroadcast(broadcastDataIntent);
                }

                if (topic.startsWith(TOPIC_CHA_SYS)) {
                    String clientId = topic.substring(TOPIC_CHA_SYS.length());
                    switch (payload) {
                        case "connected":
                            if (!connectedClients.contains(clientId))
                                connectedClients.add(clientId);

                            broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.ClientConnected);
                            broadcastDataIntent.putExtra("id", clientId);
                            broadcastDataIntent.putExtra("value", true);
                            context.sendBroadcast(broadcastDataIntent);
                            break;

                        case "disconnected":
                        case "":
                            connectedClients.remove(clientId);

                            broadcastDataIntent.putExtra(MQTT_DATA_TYPE, MQTTReceivedDataType.ClientConnected);
                            broadcastDataIntent.putExtra("id", clientId);
                            broadcastDataIntent.putExtra("value", false);
                            context.sendBroadcast(broadcastDataIntent);
                            break;
                    }
                }
            } catch (Exception ex) {
                Log.e("mqtt", ex.getMessage(), ex);
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // Do nothing
        }
    }
}
