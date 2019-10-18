package ge.altasoft.gia.ag.classes;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import ge.altasoft.gia.ag.views.DeviceView;
import ge.altasoft.gia.ag.views.SensorView;

public class AquaControllerData {

    public final static int DEVICE_COUNT = 30;
    public final static int SENSOR_COUNT = 17;

    // Device Ids
    final static int DEVICE_LIGHT_1 = 0;
    final static int DEVICE_LIGHT_2 = 1;
    final static int DEVICE_LIGHT_3 = 2;
    final static int DEVICE_LIGHT_4 = 3;
    final static int DEVICE_LIGHT_5 = 4;
    final static int DEVICE_EXHAUST_FAN = 5;
    final static int DEVICE_FEEDER_1 = 6;
    final static int DEVICE_FEEDER_2 = 7;

    // 2nd board
    final static int DEVICE_O2 = 8;
    final static int DEVICE_CO2 = 9;
    final static int DEVICE_CO2_PUMP = 10;
    final static int DEVICE_WATER_DRAIN_PUMP = 11;
    final static int DEVICE_DEVICE_220_A = 12;
    final static int DEVICE_DEVICE_220_B = 13;
    final static int DEVICE_MOON_LIGHT = 14;
    final static int DEVICE_BOARD_2_RELAY_FREE = 15;

    // 3rd board
    final static int DEVICE_WATER_FILL_PUMP = 16;
    final static int DEVICE_DOSING_PUMP_MACRO = 17; // BOARD_3_RELAY_4
    final static int DEVICE_DOSING_PUMP_MICRO = 18; // SUMP_O2
    final static int DEVICE_SUMP_RECIRCULATE_PUMP = 19;
    final static int DEVICE_UV_LIGHT = 20;
    final static int DEVICE_FILTER_2 = 21;
    final static int DEVICE_FILTER_1 = 22;
    final static int DEVICE_HOSPITAL_LIGHT = 23;

    // SSR
    final static int DEVICE_HEATER = 24;
    final static int DEVICE_SUMP_HEATER = 25;
    final static int DEVICE_HOSPITAL_HEATER = 26;

    final static int DEVICE_SOLENOID = 27; // main water shutoff
    final static int DEVICE_BOARD_FAN = 28;
    final static int DEVICE_MAINTENANCE_MODE = 29;

    // sensor IDs
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

    ////////

    final public static AquaControllerData Instance = new AquaControllerData();

    private boolean isActive;
    private boolean haveSettings;
    private long boardTimeInSec = 0;

    final private DeviceData[] deviceValues;
    final private SensorData[] sensorValues;

    private boolean widgetOrderChanged;

    private AquaControllerData() {
        isActive = false;
        widgetOrderChanged = false;
        haveSettings = false;

        deviceValues = new DeviceData[DEVICE_COUNT];
        sensorValues = new SensorData[SENSOR_COUNT];

        for (int i = 0; i < DEVICE_COUNT; i++)
            deviceValues[i] = new DeviceData(i);

        for (int i = 0; i < SENSOR_COUNT; i++)
            sensorValues[i] = new SensorData(i);
    }

    public boolean haveSettings() {
        return haveSettings;
    }

    public boolean isAlive() {
        return boardTimeInSec > 0;
    }

    public DeviceData[] sortedDevices() {
        DeviceData[] r = Arrays.copyOf(deviceValues, deviceValues.length);
        Arrays.sort(r);
        return r;
    }

    public SensorData[] sortedSensors() {
        SensorData[] r = Arrays.copyOf(sensorValues, sensorValues.length);
        Arrays.sort(r);
        return r;
    }

    public boolean isActive() {
        return isActive;
    }

    protected void setDeviceValue(int index, DeviceData value) {
        deviceValues[index] = value;
    }

    protected void setSensorValue(int index, SensorData value) {
        sensorValues[index] = value;
    }

    public DeviceData getDeviceValue(int id) {
        return deviceValues[id];
    }

    public SensorData getSensorValue(int id) {
        return sensorValues[id];
    }

    public void DecodeDeviceState(int id, String response) {

        deviceValues[id].setState(Short.parseShort(response, 16));
    }

    public void DecodeSensorState(int id, String response) {

        sensorValues[id].setState(Short.parseShort(response, 16));
    }

    protected void setIsActive(boolean value) {
        this.isActive = value;
    }

    private void setHaveSettings() {
        this.haveSettings = true;
    }


    public DeviceData getDeviceFromUIIndex(int index) {
        DeviceData[] r = Arrays.copyOf(deviceValues, deviceValues.length);
        Arrays.sort(r);

        return r[index];
    }

    public SensorData getSensorFromUIIndex(int index) {
        SensorData[] r = Arrays.copyOf(sensorValues, sensorValues.length);
        Arrays.sort(r);

        return r[index];
    }

    public boolean widgetOrderChanged() {
        return this.widgetOrderChanged;
    }

    public void setWidgetOrderChanged(boolean value) {

        this.widgetOrderChanged = value;
    }


    public static String getDeviceName(int id) {
        switch (id) {
            case DEVICE_LIGHT_1:
                return "Aqua 1";
            case DEVICE_LIGHT_2:
                return "Aqua 2";
            case DEVICE_LIGHT_3:
                return "Aqua 3";
            case DEVICE_LIGHT_4:
                return "Aqua 4";
            case DEVICE_LIGHT_5:
                return "Aqua 5";
            case DEVICE_EXHAUST_FAN:
                return "Exhaust fan";
            case DEVICE_FEEDER_1:
                return "Feeder 1";
            case DEVICE_FEEDER_2:
                return "Feeder 2";
            case O2:
                return "O2";
            case CO2:
                return "CO2";
            case DEVICE_CO2_PUMP:
                return "CO2 pump";
            case DEVICE_WATER_DRAIN_PUMP:
                return "Water drain pump";
            case DEVICE_DEVICE_220_A:
                return "220v A";
            case DEVICE_DEVICE_220_B:
                return "220v B";
            case DEVICE_MOON_LIGHT:
                return "Moon light";
            case DEVICE_BOARD_2_RELAY_FREE:
                return "Free";
            case DEVICE_WATER_FILL_PUMP:
                return "Water fill pump";
            case DEVICE_DOSING_PUMP_MACRO:
                return "Dosing macro";
            case DEVICE_DOSING_PUMP_MICRO:
                return "Dosing micro";
            case DEVICE_SUMP_RECIRCULATE_PUMP:
                return "Sump pump";
            case DEVICE_UV_LIGHT:
                return "UV light";
            case DEVICE_FILTER_2:
                return "Filter 2";
            case DEVICE_FILTER_1:
                return "Filter 1";
            case DEVICE_HOSPITAL_LIGHT:
                return "Hospital light";
            case DEVICE_HEATER:
                return "Aqua heater";
            case DEVICE_SUMP_HEATER:
                return "Sump heater";
            case DEVICE_HOSPITAL_HEATER:
                return "Hospital heater";
            case DEVICE_SOLENOID:
                return "Water solenoid";
            case DEVICE_BOARD_FAN:
                return "Board fan";
            case DEVICE_MAINTENANCE_MODE:
                return "Maint. mode";
            default:
                return "Device #" + id;
        }

    }

    public DeviceView createDeviceWidget(Context context, int position, boolean fromDashboard) {

        DeviceView w;

        switch (position) {
            case DEVICE_EXHAUST_FAN:
            case DEVICE_FEEDER_1:
            case DEVICE_FEEDER_2:
            case O2:
            case CO2:
            case DEVICE_CO2_PUMP:
                w = new DeviceView(context, fromDashboard);
                break;
            default:
                w = new DeviceView(context, fromDashboard);
                break;
        }

        w.setValue(getDeviceValue(position));
        return w;
    }


    public static String getSensorName(int id) {
        switch (id) {

            case SENSOR_T_AQUARIUM_1:
                return "Aaqua T1";
            case SENSOR_T_AQUARIUM_2:
                return "Aqua T2";
            case SENSOR_T_AQUARIUM_3:
                return "Aqua T3";
            case SENSOR_AQUA_WATER_HIGH:
                return "Aqua water high";
            case SENSOR_T_SUMP:
                return "Sump T";
            case SENSOR_WL_SUMP:
                return "Sump water level";
            case SENSOR_WL_SUMP_MM:
                return "Sump water level (mm)";
            case SENSOR_SUMP_WATER_HIGH:
                return "Sump water high";
            case SENSOR_SUMP_WATER_LOW:
                return "Sump water low";
            case SENSOR_T_HOSPITAL:
                return "Hospital T";
            case SENSOR_HOSPITAL_WATER_LOW:
                return "Hospital water low";
            case SENSOR_T_ROOM:
                return "Room T";
            case SENSOR_H_ROOM:
                return "Room H";
            case SENSOR_T_BOARD:
                return "Board T";
            case SENSOR_RPM_BOARD_FAN:
                return "Board Fan RPM";
            case SENSOR_WATER_IS_ON_FLOOR_1:
                return "Water is on floor 1";
            case SENSOR_WATER_IS_ON_FLOOR_2:
                return "Water is on floor 2";
            default:
                return "Sensor #" + id;
        }
    }

    public SensorView createSensorWidget(Context context, int position, boolean fromDashboard) {
        SensorView w = new SensorView(context, fromDashboard);
        w.setValue(getSensorValue(position));
        return w;
    }


    protected static <K, V extends Comparable<V>> Map<K, V> sortByOrder(final Map<K, V> map) {
        Comparator<K> valueComparator = new Comparator<K>() {
            public int compare(K k1, K k2) {
                int compare = -map.get(k2).compareTo(map.get(k1));
                if (compare == 0) return -1;
                else return compare;
            }
        };
        Map<K, V> sortedByValues = new TreeMap<>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    //region Encode/Decode
    public String encodeSettings() {
        StringBuilder sb = new StringBuilder();

        sb.append(isActive ? 'T' : 'F');
        for (int i = 0; i < deviceValues.length; i++)
            deviceValues[i].encodeSettings(sb);

        return sb.toString();
    }

    public void decodeSettings(String response) {
        Log.d("decode relay settings", response);

        setIsActive(response.charAt(0) != 'F');

        int idx = 1;
        for (int i = 0; i < deviceValues.length; i++)
            idx = deviceValues[i].decodeSettings(response, idx);

        setHaveSettings();
    }

    public void SetAlive(long boardTimeInSec) {
        this.boardTimeInSec = boardTimeInSec;
    }

    //endregion
    //endregion
}