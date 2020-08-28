package ge.altasoft.gia.ag;

import androidx.recyclerview.widget.RecyclerView;
import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaFragment;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.ItemViewHolder;
import ge.altasoft.gia.ag.classes.OnStartDragListener;
import ge.altasoft.gia.ag.light.DeviceRecyclerListAdapter;

public class FragmentDevices extends ChaFragment implements OnStartDragListener {

    public FragmentDevices() {
    }

    @Override
    protected boolean canReorder() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_light;
    }

    @Override
    protected RecyclerView.Adapter<ItemViewHolder> getRecycleAdapter() {
        return new DeviceRecyclerListAdapter();
    }

    @Override
    public void saveWidgetOrders() {
    }

    @Override
    public void rebuildUI(boolean isStart) {
        if (rootView == null)
            return;
        if (!AquaControllerData.Instance.haveSettings())
            return;

        hideWaitingScreen();

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ChaWidget w = getWidgetAt(recyclerView, i);
            if (w != null)
                w.refresh();
        }
    }

    public void drawState(int widgetId) {
        if (rootView == null)
            return;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ChaWidget w = getWidgetAt(recyclerView, i);
            if (w != null) {
                if (w.getWidgetId() == widgetId) {
                    w.refresh();
                    break;
                }
            }
        }
    }
}