package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.Calendar;

import ge.altasoft.gia.ag.R;
import ge.altasoft.gia.ag.Utils;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.SensorData;
import ge.altasoft.gia.ag.classes.WidgetType;

public class SensorView extends ChaWidget {

    protected TextView tvSensorName;
    protected TextView tvValue;

    protected int defaultTextColor;

    protected SensorData value;

    public SensorView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
        initializeViews(context);
    }

    public SensorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public SensorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    protected int getLayoutId() {
        return R.layout.sensor_view_layout;
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
        return value.getId();
    }

    @Override
    protected int getPopupMenuResId() {
        return R.menu.sensor_popup_menu;
    }


    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getLayoutId(), this);

        afterInflate();

        tvSensorName = (TextView) this.findViewById(R.id.sensor_name);
        tvValue = (TextView) this.findViewById(R.id.sensor_value);
        defaultTextColor = tvValue.getCurrentTextColor();
    }

    public void setValue(SensorData value) {
        this.value = value;
        refresh();
    }

    @Override
    public void refresh() {
        tvSensorName.setText(value.getName());

        Integer x = value.getState();
        tvValue.setText(x.toString());

        tvValue.setTextColor(defaultTextColor);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);
        if (value.getLastSyncTime() < calendar.getTimeInMillis())
            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, true));
        else
            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, false));
    }

    @Override
    protected long getLastSyncTime() {
        //return this.waterLevelData.getLastSyncTime();
        //todo
        return 0;
    }
}