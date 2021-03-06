package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;

import ge.altasoft.gia.ag.R;

public class PumpView extends DeviceView {


    public PumpView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
    }

    public PumpView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PumpView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pump_view_layout;
    }

    @Override
    protected void setState(ButtonState state) {

        buttonState = state;

        switch (state) {
            case UNKNOWN:
                ivPicture.setImageResource(R.drawable.pump_indicator_unknown);
                break;
            case ON:
                ivPicture.setImageResource(R.drawable.pump_indicator_on);
                break;
            case OFF:
                ivPicture.setImageResource(R.drawable.pump_indicator_off);
                break;
            case WAIT:
                ivPicture.setImageResource(R.drawable.pump_indicator_wait);
                break;
        }
    }

    @Override
    public void refresh() {
        super.refresh();

        if (value.getState() > 1)
            tvDeviceName.setText(tvDeviceName.getText() + "\n" + Integer.toString(value.getState()));
    }
}