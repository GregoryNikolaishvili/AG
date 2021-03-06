package ge.altasoft.gia.ag;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaFragment;
import ge.altasoft.gia.ag.classes.ChaWidget;
import ge.altasoft.gia.ag.classes.DashboardItems;
import ge.altasoft.gia.ag.classes.ItemViewHolder;
import ge.altasoft.gia.ag.classes.OnStartDragListener;
import ge.altasoft.gia.ag.classes.WidgetType;

public class FragmentDashboard extends ChaFragment implements OnStartDragListener {

    private final Map<String, String> controllerStates = new HashMap<>();

    public FragmentDashboard() {
    }

    @Override
    protected boolean canReorder() {
        return true;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_dashboard;
    }

    @Override
    protected RecyclerView.Adapter<ItemViewHolder> getRecycleAdapter() {
        return new DashboardRecyclerListAdapter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        DashboardItems.restoreFromPreferences(getContext());

        return rootView;
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

        //drawFooter();
    }

    public void drawWidgetState(WidgetType wt, int widgetId) {
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


    @Override
    public void saveWidgetOrders() {
        if (DashboardItems.widgetOrderChanged()) {
            DashboardItems.clear();
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                ChaWidget w = getWidgetAt(recyclerView, i);
                if (w != null)
                    DashboardItems.add(w.getWidgetType(), w.getWidgetId());
            }

            DashboardItems.saveToPreferences(getActivity());

            DashboardItems.setWidgetOrderChanged(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        getContext().registerReceiver(broadcastDataReceiver, new IntentFilter("ge.altasoft.gia.DASH_CHANGED"));
    }

    @Override
    public void onStop() {
        super.onStop();

        getContext().unregisterReceiver(broadcastDataReceiver);
    }


    final private BroadcastReceiver broadcastDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (adapter != null)
                adapter.notifyDataSetChanged();
        }
    };

    public void drawControllersState(String scope, StringBuilder sb) {
        if (rootView == null)
            return;

        if ((sb == null) || (sb.length() == 0))
            controllerStates.put(scope, "");
        else {
            String msg = sb.toString().replace('\r', ',').replace('\n', ' ');
            controllerStates.put(scope, msg);
        }

        StringBuilder msgBuilder = new StringBuilder();

        for (Map.Entry<String, String> pairs : controllerStates.entrySet()) {
            scope = pairs.getKey();
            String value = pairs.getValue();
            if ((value != null) && (value.length() > 0)) {
                msgBuilder.append(scope);
                msgBuilder.append(": ");
                msgBuilder.append(value);

                msgBuilder.append("\n");
            }
        }

        if (msgBuilder.length() >= 1) // delete last "\n"
            msgBuilder.setLength(msgBuilder.length() - 1);

        ((TextView) rootView.findViewById(R.id.controllersStatus)).setText(msgBuilder.toString());
    }


}
