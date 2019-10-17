package ge.altasoft.gia.ag.other;

import android.support.v7.widget.RecyclerView;

import ge.altasoft.gia.ag.R;
import ge.altasoft.gia.ag.classes.ChaFragment;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.ItemViewHolder;
import ge.altasoft.gia.ag.classes.OnStartDragListener;
import ge.altasoft.gia.ag.classes.WidgetType;

public class FragmentSensors extends ChaFragment implements OnStartDragListener {

    public FragmentSensors() {
    }

    @Override
    protected boolean canReorder() {
        return false;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_other_sensors;
    }

    @Override
    protected RecyclerView.Adapter<ItemViewHolder> getRecycleAdapter() {
        return new OtherSensorRecyclerListAdapter();
    }

    @Override
    public void checkSensors() {
        if (rootView != null) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                ChaWidget w = getWidgetAt(recyclerView, i);
                if (w != null)
                    w.refresh();
            }
        }
    }

    @Override
    public void saveWidgetOrders() {
    }


    @Override
    public void rebuildUI(boolean isStart) {
        if ((rootView == null) || (AquaControllerData.Instance == null))
            return;

        hideWaitingScreen();

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ChaWidget w = getWidgetAt(recyclerView, i);
            if (w != null)
                w.refresh();
        }
    }

    public void drawState(WidgetType wt, int widgetId) {
        if (rootView == null)
            return;

        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            ChaWidget w = getWidgetAt(recyclerView, i);
            if (w != null) {
                if ((w.getWidgetId() == widgetId) && (w.getWidgetType() == wt)) {
                    w.refresh();
                    break;
                }
            }
        }
    }
}