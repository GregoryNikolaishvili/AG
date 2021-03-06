package ge.altasoft.gia.ag.classes;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ge.altasoft.gia.ag.R;

public abstract class ChaFragment extends Fragment implements OnStartDragListener {

    protected ViewGroup rootView = null;

    private boolean dragMode = false;
    private TextView tvLoading;
    private ItemTouchHelper mItemTouchHelper;

    protected RecyclerView.Adapter<ItemViewHolder> adapter;

    protected RecyclerView recyclerView;

    protected abstract boolean canReorder();

    protected abstract int getLayoutResId();

    protected abstract RecyclerView.Adapter<ItemViewHolder> getRecycleAdapter();

    public abstract void rebuildUI(boolean isStart);

    public abstract void saveWidgetOrders();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(getLayoutResId(), container, false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tvLoading = new TextView(getContext());
        tvLoading.setLayoutParams(lp);
        tvLoading.setGravity(Gravity.CENTER);
        tvLoading.setText(getResources().getString(R.string.loading));
        rootView.addView(tvLoading, 0);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        return rootView;
    }


    private class MySimpleItemTouchHelperCallback extends SimpleItemTouchHelperCallback {
        MySimpleItemTouchHelperCallback(ItemTouchHelperAdapter _adapter) {
            super(_adapter);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return dragMode;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (recyclerView != null) {
            adapter = getRecycleAdapter();

            //recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);

            final GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 4);
            recyclerView.setLayoutManager(layoutManager);

            if (canReorder()) {
                ItemTouchHelper.Callback callback = new MySimpleItemTouchHelperCallback((ItemTouchHelperAdapter) adapter);
                mItemTouchHelper = new ItemTouchHelper(callback);
                mItemTouchHelper.attachToRecyclerView(recyclerView);
            }
        }
        rebuildUI(false);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        if (dragMode)
            mItemTouchHelper.startDrag(viewHolder);
    }

    public void setDraggableViews(boolean on) {
        if (!canReorder())
            return;

        dragMode = on;
        if (recyclerView != null) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View v = recyclerView.getChildAt(i);
                if (v instanceof ChaWidget)
                    ((ChaWidget) v).setDragMode(on);
            }
        }
    }

    protected void hideWaitingScreen() {
        if (tvLoading != null) {
            rootView.removeView(tvLoading);
            tvLoading = null;
        }
    }

    public void refreshWidgets() {
        if (rootView != null) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                ChaWidget w = getWidgetAt(recyclerView, i);
                if (w != null)
                    w.refresh();
            }
        }
    }
    protected ChaWidget getWidgetAt(RecyclerView recyclerView, int position) {
        if (recyclerView != null) {
            View v = recyclerView.getChildAt(position);
            if (v instanceof ChaWidget)
                return (ChaWidget) v;

            if (v instanceof ViewGroup)
                return (ChaWidget) ((ViewGroup) v).getChildAt(0);
            else
                return null;
        } else
            return null;
    }
}
