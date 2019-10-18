package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import ge.altasoft.gia.ag.ChaActivity;
import ge.altasoft.gia.ag.R;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.DeviceData;
import ge.altasoft.gia.ag.classes.WidgetType;

public abstract class DeviceView extends ChaWidget {

    protected enum ButtonState {UNKNOWN, ON, OFF, WAIT}

    protected ButtonState buttonState = ButtonState.UNKNOWN;

    protected DeviceData value;

    protected TextView tvDeviceName;
    protected ImageView ivPicture;

    public DeviceView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
        initializeViews(context);
    }

    public DeviceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public DeviceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    @Override
    protected boolean canClick() {
        return true;
    }

    @Override
    public WidgetType getWidgetType() {
        return WidgetType.Device;
    }

    @Override
    public int getWidgetId() {
        return value.getId();
    }

    @Override
    protected int getPopupMenuResId() {
        return R.menu.device_popup_menu;
    }

    @Override
    protected void onClick() {
        ((ChaActivity) getContext()).publish(String.format(Locale.US, "aquagodc/state/%01X", value.getId()), buttonState == ButtonState.OFF ? "1" : "0", false);
        setState(ButtonState.WAIT);
    }

    protected abstract int getLayoutId();

    protected void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(getLayoutId(), this);

        afterInflate();

        tvDeviceName = ((TextView) this.findViewById(R.id.device_name));
        ivPicture = ((ImageView) findViewById(R.id.relay_light));
    }

    protected void setState(ButtonState state) {
        buttonState = state;

        switch (state) {
            case UNKNOWN:
                ivPicture.setImageResource(R.drawable.button_onoff_indicator_unknown);
                break;
            case ON:
                ivPicture.setImageResource(R.drawable.button_onoff_indicator_on);
                break;
            case OFF:
                ivPicture.setImageResource(R.drawable.button_onoff_indicator_off);
                break;
            case WAIT:
                ivPicture.setImageResource(R.drawable.button_onoff_indicator_wait);
                break;
        }
    }

    public void setValue(DeviceData value) {
        this.value = value;
        refresh();
    }

    @Override
    public void refresh() {
        tvDeviceName.setText(this.value.getName());
        setState(this.value.getState() != 0 ? ButtonState.ON : ButtonState.OFF);
    }

    @Override
    protected long getLastSyncTime()
    {
        return 0;
    }

}