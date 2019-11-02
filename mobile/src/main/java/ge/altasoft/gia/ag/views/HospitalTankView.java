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
import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.WidgetType;

public class HospitalTankView extends ChaWidget {

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

    public HospitalTankView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
        initializeViews(context);
    }

    public HospitalTankView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public HospitalTankView(Context context, AttributeSet attrs, int defStyle) {
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

        tvWaterOnTop.setBackgroundColor(Color.TRANSPARENT);
        tvLight2.setBackgroundResource(R.drawable.none);
        tvLight3.setBackgroundResource(R.drawable.none);
        tvLight4.setBackgroundResource(R.drawable.none);

        tvRecircPump.setBackgroundResource(R.drawable.none);
        tvFilter1.setBackgroundResource(R.drawable.none);
        tvFilter2.setBackgroundResource(R.drawable.none);
        tvOxygen.setBackgroundResource(R.drawable.none);

        ((TextView) this.findViewById(R.id.caption_text)).setText("Hosp");
    }


    @Override
    public void refresh() {
        boolean allOk = true;

        float fv = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_T_HOSPITAL).getState() / 10.0f;
        if (fv < 24.5 && fv > 25.5)
            allOk = false;
        tvTemperature.setText(String.format(Locale.US, "%.1f°", fv));
//        tvTemperature.setTextColor(this.sensorData.getTemperatureColor());

        int v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_HOSPITAL_HEATER).getState();
        tvHeater.setProgress(v);

        v = AquaControllerData.Instance.getDeviceValue(AquaControllerData.DEVICE_HOSPITAL_LIGHT).getState();
        tvLight1.setBackgroundResource(v > 0 ? R.drawable.bulb_indicator_yellow : R.drawable.bulb_indicator_off);

        v = AquaControllerData.Instance.getSensorValue(AquaControllerData.SENSOR_HOSPITAL_WATER_LOW).getState();
        tvWaterOnBottom.setBackgroundColor(v > 0 ? Color.CYAN : Color.TRANSPARENT);

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