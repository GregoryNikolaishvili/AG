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

import ge.altasoft.gia.ag.R;
import ge.altasoft.gia.ag.Utils;
import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.WidgetType;

public class SumpTankView extends ChaWidget {

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
    private TextView tvRightText;

    private ImageView tvPumpDrain;
    private ImageView tvPumpFill;

    public SumpTankView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
        initializeViews(context);
    }

    public SumpTankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SumpTankView(Context context, AttributeSet attrs, int defStyle) {
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
        return 1;
    }

    @Override
    protected int getPopupMenuResId() {
        return R.menu.th_sensor_popup_menu;
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tank_view_layout, this);

        afterInflate();

        tvRightText = (TextView) this.findViewById(R.id.right_text);

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

        tvPumpDrain = (ImageView) this.findViewById(R.id.top_pump_value);
        tvPumpFill = (ImageView) this.findViewById(R.id.top_pump2_value);

        tvLight1.setBackgroundResource(R.drawable.none);
        tvLight2.setBackgroundResource(R.drawable.none);
        tvLight3.setBackgroundResource(R.drawable.none);
        tvLight4.setBackgroundResource(R.drawable.none);

        tvOxygen.setBackgroundResource(R.drawable.none);

        this.findViewById(R.id.pump_layout_middle).setVisibility(GONE);
        this.findViewById(R.id.pump_layout_top).setVisibility(VISIBLE);

        ((TextView) this.findViewById(R.id.caption_text)).setText("Sump");
    }


    @Override
    public void refresh() {
        if (!AquaControllerData.Instance.haveData())
            return;

        boolean allOk = true;

        float presetT = AquaControllerData.Instance.getSettings().aquariumTemperature / 10.0f;

        float fv = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_T_SUMP).getState() / 10.0f;
        tvTemperature.setText(String.format(Locale.US, "%.1f°", fv));
        if (fv > presetT + 0.5f)
        {
            tvTemperature.setTextColor(Utils.COLOR_TEMP_HIGH);
            //allOk = false;
        }
        else
        if (fv < presetT - 0.5f)
        {
            tvTemperature.setTextColor(Utils.COLOR_TEMP_LOW);
            //allOk = false;
        }
        else
        {
            tvTemperature.setTextColor(Utils.COLOR_TEMP_NORMAL);
        }

        int v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_SUMP_HEATER).getState();
        tvHeater.setProgress(v);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_SUMP_RECIRCULATE_PUMP).getState();
        if (v == 0)
            allOk = false;
        tvRecircPump.setBackgroundResource(v > 0 ? R.drawable.pump_indicator_on : R.drawable.pump_indicator_off);

        v = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_WATER_IS_ON_FLOOR_1).getState();
        tvFilter1.setBackgroundResource(v > 0 ? R.drawable.water_droplets_on : R.drawable.water_droplets_off);

        v = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_WATER_IS_ON_FLOOR_2).getState();
        tvFilter2.setBackgroundResource(v > 0 ? R.drawable.water_droplets_on: R.drawable.water_droplets_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_SOLENOID).getState();
        tvOxygen.setBackgroundResource(v > 0 ? R.drawable.water_indicator_on: R.drawable.water_indicator_off);

        v = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_SUMP_WATER_HIGH).getState();
        tvWaterOnTop.setBackgroundColor(v > 0 ? Color.TRANSPARENT : Color.YELLOW);

        v = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_SUMP_WATER_LOW).getState();
        tvWaterOnBottom.setBackgroundColor(v > 0 ? Color.CYAN : Color.TRANSPARENT);

        v = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_WL_SUMP).getState();
        tvRightText.setText(v + " %");

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_WATER_DRAIN_PUMP).getState();
        tvPumpDrain.setBackgroundResource(v > 0 ? R.drawable.drain_indicator_on: R.drawable.drain_indicator_off);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_WATER_FILL_PUMP).getState();
        tvPumpFill.setBackgroundResource(v > 0 ? R.drawable.fill_indicator_on: R.drawable.fill_indicator_off);

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