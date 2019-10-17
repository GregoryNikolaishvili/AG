package ge.altasoft.gia.ag.other;

import android.content.Context;
import android.content.SharedPreferences;

import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.RelayControllerData;
import ge.altasoft.gia.ag.classes.RelayData;
import ge.altasoft.gia.ag.views.SensorView;

public final class AquaControllerData extends RelayControllerData {

    final static int DEVICE_COUNT = 30;
    final static int SENSOR_COUNT = 16;

    final static int SENSOR_T_AQUARIUM_1 = 0;
    final static int SENSOR_T_AQUARIUM_2 = 1;
    final static int SENSOR_T_AQUARIUM_3 = 2;
    final static int SENSOR_AQUA_WATER_HIGH = 3;

    final static int SENSOR_T_SUMP = 4;
    final static int SENSOR_WL_SUMP = 5;
    final static int SENSOR_WL_SUMP_MM = 6;
    final static int SENSOR_SUMP_WATER_HIGH = 7;
    final static int SENSOR_SUMP_WATER_LOW = 8;

    final static int SENSOR_T_HOSPITAL = 9;
    final static int SENSOR_HOSPITAL_WATER_LOW = 10;

    final static int SENSOR_T_ROOM = 11;
    final static int SENSOR_H_ROOM = 12;

    final static int SENSOR_T_BOARD = 13;
    final static int SENSOR_RPM_BOARD_FAN = 14;

    final static int SENSOR_WATER_IS_ON_FLOOR_1 = 15;
    final static int SENSOR_WATER_IS_ON_FLOOR_2 = 16;

    final public static AquaControllerData Instance = new AquaControllerData();

    //private final WaterLevelData[] waterLevelDatas;

    private final int[] sensorValues;

    private AquaControllerData() {

        sensorValues = new int[SENSOR_COUNT];

        for (int i = 0; i < DEVICE_COUNT; i++) {
            RelayData relay = new WlPumpRelayData(i);
            setRelay(i, relay);
        }
    }

    @Override
    public int relayCount() {
        return DEVICE_COUNT;
    }

    public WlPumpRelayData relays(int index) {
        return (WlPumpRelayData) super.relays(index);
    }

    int sensorCount() {
        return SENSOR_COUNT;
    }

    ChaWidget createWidget(Context context, int position, boolean fromDashboard) {
        SensorView w = new SensorView(context, fromDashboard);
        w.setValue(getSensorValue(position));
        return w;

    }


    public void DecodeState(int id, String response) {

        sensorValues[id] = Short.parseShort(response, 16);
    }

    public int getSensorValue(int id) {
        return sensorValues[id];
    }

    //    public void decodeWaterLevelSettings(String payload) {
//
//        int idx = 0;
//        idx = waterLevelDatas[0].decodeSettings(payload, idx);
//        idx = waterLevelDatas[1].decodeSettings(payload, idx);
//        waterLevelDatas[2].decodeSettings(payload, idx);
//    }
//
//    public String encodeWaterLevelSettings() {
//        StringBuilder sb = new StringBuilder();
//
//        waterLevelDatas[0].encodeSettings(sb);
//        waterLevelDatas[1].encodeSettings(sb);
//        waterLevelDatas[2].encodeSettings(sb);
//
//        return sb.toString();
//    }
//
    void decode(SharedPreferences prefs) {

        setIsActive(prefs.getBoolean("automatic_mode", false));

        for (int i = 0; i < DEVICE_COUNT; i++)
            relays(i).decodeSettings(prefs);

//        waterLevelDatas[0].decodeSettings(prefs);
//        waterLevelDatas[1].decodeSettings(prefs);
//        waterLevelDatas[2].decodeSettings(prefs);
    }

    void saveToPreferences(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("automatic_mode", isActive());

        for (int i = 0; i < DEVICE_COUNT; i++)
            relays(i).encodeSettings(editor);
//
//        waterLevelDatas[0].encodeSettings(editor);
//        waterLevelDatas[1].encodeSettings(editor);
//        waterLevelDatas[2].encodeSettings(editor);

        editor.apply();
    }

}
