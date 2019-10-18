package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import ge.altasoft.gia.ag.R;
import ge.altasoft.gia.ag.Utils;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.SensorData;
import ge.altasoft.gia.ag.classes.WidgetType;

public class TemperatureView extends SensorView {

    public TemperatureView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
    }

    public TemperatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TemperatureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected int getLayoutId()
    {
        return R.layout.temperature_view_layout;
    }

    @Override
    public void refresh() {
        tvSensorName.setText(value.getName());

        int x = value.getState();
        if (x == 9999)
            tvValue.setText("- - - - ");
        else
            tvValue.setText(String.format(Locale.US, "%.1f°", x / 10.0));

//        if (this.waterLevelData.getFloatSwitchIsOn())
//            tvValue.setTextColor(Color.CYAN);
//        else
        tvValue.setTextColor(defaultTextColor);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);
        if (value.getLastSyncTime() < calendar.getTimeInMillis())
            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, true));
        else
            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, false));
    }
}