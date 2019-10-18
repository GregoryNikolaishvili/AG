//package ge.altasoft.gia.ag.light;
//
//import android.content.SharedPreferences;
//
//import ge.altasoft.gia.ag.classes.RelayControllerData;
//
//
//public final class DeviceControllerData extends RelayControllerData {
//
//    final static int RELAY_COUNT = 12;
//
//    public final static DeviceControllerData Instance = new DeviceControllerData();
//
//    private DeviceControllerData() {
//        super();
//
//        for (int i = 0; i < RELAY_COUNT; i++) {
//            LightDeviceData relay = new LightDeviceData(i);
//            setDeviceValue(i, relay);
//        }
//    }
//
//    @Override
//    public int relayCount() {
//        return RELAY_COUNT;
//    }
//
//    public LightDeviceData relays(int index) {
//        return (LightDeviceData) super.relays(index);
//    }
//
//    //region Encode/Decode
//    void decode(SharedPreferences prefs) {
//
//        setIsActive(prefs.getBoolean("l_automatic_mode", false));
//
//        for (int i = 0; i < RELAY_COUNT; i++)
//            relays(i).decodeSettings(prefs);
//    }
//
//    void saveToPreferences(SharedPreferences prefs) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("l_automatic_mode", isActive());
//
//        for (int i = 0; i < RELAY_COUNT; i++)
//            relays(i).encodeSettings(editor);
//
//        editor.apply();
//    }
//
//    //endregion
//}
//
//
