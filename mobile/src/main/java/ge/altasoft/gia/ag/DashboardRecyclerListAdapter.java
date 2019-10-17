package ge.altasoft.gia.ag;

import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.DashboardItem;
import ge.altasoft.gia.ag.classes.DashboardItems;
import ge.altasoft.gia.ag.classes.ItemTouchHelperAdapter;
import ge.altasoft.gia.ag.classes.ItemViewHolder;
import ge.altasoft.gia.ag.classes.OnStartDragListener;
import ge.altasoft.gia.ag.light.LightControllerData;
import ge.altasoft.gia.ag.light.LightRelayData;
import ge.altasoft.gia.ag.other.AquaControllerData;
import ge.altasoft.gia.ag.other.WaterLevelData;
import ge.altasoft.gia.ag.other.WlPumpRelayData;
import ge.altasoft.gia.ag.views.LightRelayView;
import ge.altasoft.gia.ag.views.SensorView;
import ge.altasoft.gia.ag.views.WlPumpRelayView;

class DashboardRecyclerListAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter {

    private final OnStartDragListener mDragStartListener;

    DashboardRecyclerListAdapter(OnStartDragListener dragStartListener) {
        mDragStartListener = dragStartListener;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = new FrameLayout(parent.getContext());

        int height = parent.getMeasuredHeight() / 4;
        itemView.setMinimumHeight(height);

        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {

        ViewGroup ll = (ViewGroup) holder.itemView;
        if (ll.getChildCount() > 0)
            ll.removeAllViews();

        ChaWidget w;

        DashboardItem item = DashboardItems.getItemAt(position);
        switch (item.type) {
            case LightRelay:
                LightRelayData rd = LightControllerData.Instance.relays(item.id);
                w = new LightRelayView(ll.getContext(), true);
                ((LightRelayView) w).setRelayData(rd);
                break;

            case WaterLevelSensor:
                int value = AquaControllerData.Instance.getSensorValue(item.id);
                w = new SensorView(ll.getContext(), true);
                ((SensorView) w).setValue(value);
                break;

            case WaterLevelPumpRelay:
                WlPumpRelayData wrd = AquaControllerData.Instance.relays(item.id);
                w = new WlPumpRelayView(ll.getContext(), true);
                ((WlPumpRelayView) w).setRelayData(wrd);
                break;

            default:
                return;
        }

        int height = ViewCompat.getMinimumHeight(ll);
        w.setMinimumHeight(height);
        w.setLayoutParams(new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        ll.addView(w);
        View handleView = w.findViewById(R.id.main_layout);

        //Start a drag whenever the handle view it touched
        handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public void onItemDismiss(int position) {
        //mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        DashboardItems.setWidgetOrderChanged(true);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return DashboardItems.size();
    }
}
