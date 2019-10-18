package ge.altasoft.gia.ag.views;

import android.content.Context;
import android.util.AttributeSet;

import ge.altasoft.gia.ag.R;

public class FishView extends DeviceView {

       public FishView(Context context, boolean fromDashboard) {
        super(context, fromDashboard);
    }

    public FishView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FishView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getLayoutId()
    {
        return R.layout.fish_view_layout;
    }

    @Override
    protected void setState(ButtonState state) {
        buttonState = state;

        switch (state) {
            case UNKNOWN:
                ivPicture.setImageResource(R.drawable.fish_indicator_unknown);
                break;
            case ON:
                ivPicture.setImageResource(R.drawable.fish_indicator_on);
                break;
            case OFF:
                ivPicture.setImageResource(R.drawable.fish_indicator_off);
                break;
            case WAIT:
                ivPicture.setImageResource(R.drawable.fish_indicator_wait);
                break;
        }
    }

//    @Override
//    protected boolean canClick() {
//        return true;
//    }

}