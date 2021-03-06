package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Locale;

import ge.altasoft.gia.ag.ChaActivity;
import ge.altasoft.gia.ag.R;

public class HeaterView extends DeviceView {

    public HeaterView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
    }

    public HeaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.heater_view_layout;
    }

    @Override
    protected void onClick() {
        int newState = value.getState() + 10;
        if (newState > 100)
            newState = 0;
        ((ChaActivity) getContext()).publish(String.format(Locale.US, "aquagodc/state/%02X", value.getId()), String.format(Locale.US, "%04X", newState), false);
        setState(ButtonState.WAIT);
    }

    @Override
    protected void setState(ButtonState state) {
        buttonState = state;

        switch (state) {
            case UNKNOWN:
                ivPicture.setImageResource(R.drawable.heater_indicator_unknown);
                break;
            case ON:
                ivPicture.setImageResource(R.drawable.heater_indicator_on);
                break;
            case OFF:
                ivPicture.setImageResource(R.drawable.heater_indicator_off);
                break;
            case WAIT:
                ivPicture.setImageResource(R.drawable.heater_indicator_wait);
                break;
        }


    }

    @Override
    public void refresh() {
        super.refresh();

        if (value.getState() > 0)
            tvDeviceName.setText(tvDeviceName.getText() + "\n" + Integer.toString(value.getState()) + " %");
    }
}