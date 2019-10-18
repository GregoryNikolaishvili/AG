package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

import ge.altasoft.gia.ag.ChaActivity;
import ge.altasoft.gia.ag.R;

public class RelayView extends DeviceView {

    public RelayView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
    }

    public RelayView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.relay_view_layout;
    }
}