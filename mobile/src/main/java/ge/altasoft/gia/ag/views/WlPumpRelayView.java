//package ge.altasoft.gia.ag.views;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.Locale;
//
//import ge.altasoft.gia.ag.ChaActivity;
//import ge.altasoft.gia.ag.R;
//import ge.altasoft.gia.ag.classes.ChaWidget;
//import ge.altasoft.gia.ag.classes.DeviceData;
//import ge.altasoft.gia.ag.classes.WidgetType;
//
//public class WlPumpRelayView extends ChaWidget {
//
//    private enum ButtonState {UNKNOWN, ON, OFF, WAIT}
//
//    private ButtonState buttonState = ButtonState.UNKNOWN;
//
//    private DeviceData deviceData;
//
//    private TextView tvRelayName;
//    private ImageView ivLight;
//
//    public WlPumpRelayView(Context context, boolean fromDashboard) {
//        super(context, fromDashboard);
//        initializeViews(context);
//    }
//
//    public WlPumpRelayView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initializeViews(context);
//    }
//
//    public WlPumpRelayView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        initializeViews(context);
//    }
//
//    @Override
//    protected boolean canClick() {
//        return true;
//    }
//
//    @Override
//    public WidgetType getWidgetType() {
//        return WidgetType.WaterLevelPumpRelay;
//    }
//
//    @Override
//    public int getWidgetId() {
//        return deviceData.getId();
//    }
//
//    @Override
//    protected int getPopupMenuResId() {
//        return R.menu.device_popup_menu;
//    }
//
//    @Override
//    protected void onClick() {
//        ((ChaActivity) getContext()).publish(String.format(Locale.US, "chac/wl/state/%01X", deviceData.getId()), buttonState == ButtonState.OFF ? "1" : "0", false);
//        setState(ButtonState.WAIT);
//    }
//
//    private void initializeViews(Context context) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        inflater.inflate(R.layout.wl_pump_relay_layout, this);
//
//        afterInflate();
//
//        tvRelayName = ((TextView) this.findViewById(R.id.relay_name));
//        ivLight = ((ImageView) findViewById(R.id.relay_light));
//    }
//
//    private void setState(ButtonState value) {
//        buttonState = value;
//
//        switch (value) {
//            case UNKNOWN:
//                ivLight.setImageResource(R.drawable.pump_off);
//                break;
//            case ON:
//                ivLight.setImageResource(R.drawable.pump_90);
//                break;
//            case OFF:
//                ivLight.setImageResource(R.drawable.pump_standby_0);
//                break;
//            case WAIT:
//                ivLight.setImageResource(R.drawable.pump_wait);
//                break;
//        }
//    }
//
////    public LightDeviceData getRelayData() {
////        return this.deviceData;
////    }
//
//    public void setDeviceData(DeviceData value) {
//        this.deviceData = value;
//        refresh();
//    }
//
//    @Override
//    public void refresh() {
//        tvRelayName.setText(this.deviceData.getName()); // + ", order=" + String.valueOf(value.getOrder()));
//        setState(this.deviceData.getState() != 0 ? ButtonState.ON : ButtonState.OFF);
//    }
//
//    @Override
//    protected long getLastSyncTime()
//    {
//        return 0;
//    }
//
//}