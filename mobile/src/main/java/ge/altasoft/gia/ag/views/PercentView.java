package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.Locale;

import ge.altasoft.gia.ag.R;
import ge.altasoft.gia.ag.Utils;

public class PercentView extends SensorView {

    public PercentView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
    }

    public PercentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected int getLayoutId() {
        return R.layout.sensor_view_layout;
    }

    @Override
    public void refresh() {

        tvSensorName.setText(value.getName());

        int x = value.getState();
        tvValue.setText(x + "%");
        tvValue.setTextColor(defaultTextColor);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -2);
        if (value.getLastSyncTime() < calendar.getTimeInMillis())
            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, true));
        else
            cardView.setCardBackgroundColor(Utils.getCardBackgroundColor(false, false));
    }
}