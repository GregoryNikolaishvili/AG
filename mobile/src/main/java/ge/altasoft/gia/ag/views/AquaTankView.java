package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;

import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.WidgetType;
import ge.altasoft.gia.ag.R;

public class AquaTankView extends ChaWidget {

    private TextView tvTemperature;
    private ProgressBar tvHeater;
    private ImageView tvLight1;
    private ImageView tvLight2;
    private ImageView tvLight3;
    private ImageView tvLight4;
    private ImageView tvRecircPump;
    private ImageView tvFilter1;
    private ImageView tvFilter2;
    private ImageView tvOxygen;
    private View tvCaption;
    private View tvWaterOnTop;
    private View tvWaterOnBottom;
    private View tvWarning;

    public AquaTankView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
        initializeViews(context);
    }

    public AquaTankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public AquaTankView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    protected boolean canClick() {
        return false;
    }

    @Override
    protected void onClick() {

    }

    @Override
    public WidgetType getWidgetType() {
        return WidgetType.Sensor;
    }

    @Override
    public int getWidgetId() {
        return 0;
    }

    @Override
    protected int getPopupMenuResId() {
        return R.menu.th_sensor_popup_menu;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tank_view_layout, this);

        afterInflate();

        tvTemperature = (TextView) this.findViewById(R.id.temperature_value);
        tvHeater = (ProgressBar) this.findViewById(R.id.heater_value);

        tvLight1 = (ImageView) this.findViewById(R.id.light1_value);
        tvLight2 = (ImageView) this.findViewById(R.id.light2_value);
        tvLight3 = (ImageView) this.findViewById(R.id.light3_value);
        tvLight4 = (ImageView) this.findViewById(R.id.light4_value);

        tvRecircPump = (ImageView) this.findViewById(R.id.pump_value);
        tvFilter1 = (ImageView) this.findViewById(R.id.filter1_value);
        tvFilter2 = (ImageView) this.findViewById(R.id.filter2_value);
        tvOxygen = (ImageView) this.findViewById(R.id.o2_value);

        tvCaption = this.findViewById(R.id.caption);
        tvWaterOnTop = this.findViewById(R.id.water_on_top);
        tvWaterOnBottom = this.findViewById(R.id.water_on_bottom);

        tvWarning = this.findViewById(R.id.warning);

        tvWaterOnBottom.setBackgroundColor(Color.TRANSPARENT);

        ((TextView)this.findViewById(R.id.caption_text)).setText("Aqua");
    }


    @Override
    public void refresh() {
        boolean allOk = true;

        float fv = (AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_T_AQUARIUM_1).getState() + AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_T_AQUARIUM_2).getState()) / 20.0f;
        if (fv < 24.5 && fv > 25.5)
            allOk = false;
        tvTemperature.setText(String.format(Locale.US, "%.1f°", fv));
//        tvTemperature.setTextColor(this.sensorData.getTemperatureColor());


        int v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_AQUA_HEATER).getState();
        tvHeater.setProgress(v);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_LIGHT_1).getState();
        tvLight1.setBackgroundResource(v > 0 ? R.drawable.bulb_indicator_yellow : R.drawable.bulb_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_LIGHT_2).getState();
        tvLight2.setBackgroundResource(v > 0 ? R.drawable.bulb_indicator_yellow : R.drawable.bulb_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_LIGHT_3).getState();
        tvLight3.setBackgroundResource(v > 0 ? R.drawable.bulb_indicator_yellow : R.drawable.bulb_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_UV_LIGHT).getState();
        tvLight4.setBackgroundResource(v > 0 ? R.drawable.bulb_indicator_violet : R.drawable.bulb_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_AQUA_RECIRCULATE_PUMP).getState();
        if (v == 0)
            allOk = false;
        tvRecircPump.setBackgroundResource(v > 0 ? R.drawable.pump_indicator_on : R.drawable.pump_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_FILTER_1).getState();
        if (v == 0)
            allOk = false;
        tvFilter1.setBackgroundResource(v > 0 ? R.drawable.filter1_indicator_on : R.drawable.filter1_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_FILTER_2).getState();
        tvFilter2.setBackgroundResource(v > 0 ? R.drawable.filter1_indicator_on : R.drawable.filter1_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_O2).getState();
        tvOxygen.setBackgroundResource(v > 0 ? R.drawable.o2_indicator_on : R.drawable.o2_indicator_off);

        v = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_AQUA_WATER_HIGH).getState();
        tvWaterOnTop.setBackgroundColor(v > 0 ? Color.CYAN : Color.TRANSPARENT);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_MAINTENANCE_MODE).getState();
        tvCaption.setBackgroundColor(v > 0 ? Color.YELLOW : Color.TRANSPARENT);
        tvWarning.setBackgroundColor(allOk ? Color.TRANSPARENT : Color.YELLOW);

        //        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, -2);
//        if (this.sensorData.getLastSyncTime() < calendar.getTimeInMillis())
//            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, true));
//        else
//            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, false));
    }

    @Override
    protected long getLastSyncTime() {
        //return this.sensorData.getLastSyncTime();
        return 0;
    }

}