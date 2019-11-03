package ge.altasoft.gia.ag;

import android.support.v7.widget.RecyclerView;

import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaFragment;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.ItemViewHolder;
import ge.altasoft.gia.ag.classes.OnStartDragListener;
import ge.altasoft.gia.ag.other.SensorRecyclerListAdapter;

public class FragmentSensors extends ChaFragment implements OnStartDragListener {

    public FragmentSensors() {
    }

    @Override
    protected boolean canReorder() {
        return false;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_sensors;
    }

    @Override
    protected RecyclerView.Adapter<ItemViewHolder> getRecycleAdapter() {
        return new SensorRecyclerListAdapter();
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