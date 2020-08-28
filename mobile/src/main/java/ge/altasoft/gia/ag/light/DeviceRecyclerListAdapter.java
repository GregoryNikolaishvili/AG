package ge.altasoft.gia.ag.light;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.ItemTouchHelperAdapter;
import ge.altasoft.gia.ag.classes.ItemViewHolder;

public class DeviceRecyclerListAdapter extends RecyclerView.Adapter<ItemViewHolder> implements ItemTouchHelperAdapter {

    public DeviceRecyclerListAdapter() {
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

        ChaWidget w = AquaControllerData.Instance.createDeviceWidget(ll.getContext(), position, false);
        if (w != null) {
            int height = ViewCompat.getMinimumHeight(ll);
            w.setMinimumHeight(height);
            w.setLayoutParams(new FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            ll.addView(w);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        //mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return AquaControllerData.DEVICE_COUNT;
    }

}
