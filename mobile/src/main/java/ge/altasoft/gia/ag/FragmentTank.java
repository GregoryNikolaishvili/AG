package ge.altasoft.gia.ag;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaFragment;
import ge.altasoft.gia.ag.classes.ItemViewHolder;
import ge.altasoft.gia.ag.views.AquaTankView;
import ge.altasoft.gia.ag.views.HospitalTankView;
import ge.altasoft.gia.ag.views.SumpTankView;


public class FragmentTank extends ChaFragment {

    public FragmentTank() {
    }

    @Override
    protected boolean canReorder() {
        return false;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tank;
    }

    @Override
    protected RecyclerView.Adapter<ItemViewHolder> getRecycleAdapter() {
        return null;
    }

    @Override
    public void saveWidgetOrders() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final CardView cv = ((CardView) rootView.findViewById(R.id.boilerMode));

        cv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                PopupMenu popupMenu = new PopupMenu(getContext(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ((TextView) cv.getChildAt(0)).setText("⏱"); // ⌛
                        cv.setEnabled(false);

//                        switch (item.getItemId()) {
//                            case R.id.item_summer:
//                                ((ChaActivity) getActivity()).publish("chac/ts/mode", String.valueOf(BOILER_MODE_SUMMER), false);
//                                break;
//                            case R.id.item_summer_away:
//                                ((ChaActivity) getActivity()).publish("chac/ts/mode", String.valueOf(BOILER_MODE_SUMMER_AWAY), false);
//                                break;
//                            case R.id.item_summer_and_pool:
//                                ((ChaActivity) getActivity()).publish("chac/ts/mode", String.valueOf(BOILER_MODE_SUMMER_POOL), false);
//                                break;
//                            case R.id.item_summer_and_pool_away:
//                                ((ChaActivity) getActivity()).publish("chac/ts/mode", String.valueOf(BOILER_MODE_SUMMER_POOL_AWAY), false);
//                                break;
//                            case R.id.item_winter:
//                                ((ChaActivity) getActivity()).publish("chac/ts/mode", String.valueOf(BOILER_MODE_WINTER), false);
//                                break;
//                            case R.id.item_winter_away:
//                                ((ChaActivity) getActivity()).publish("chac/ts/mode", String.valueOf(BOILER_MODE_WINTER_AWAY), false);
//                                break;
//                        }
                        return false;
                    }
                });
                popupMenu.inflate(R.menu.boiler_mode_popup_menu);
                popupMenu.show();

                return true;
            }
        });


        final View boilerLayout = rootView.findViewById(R.id.boilerLayout);

        return rootView;
    }


    @Override
    public void refreshWidgets() {
        rebuildUI(false);
    }

    @Override
    public void rebuildUI(boolean isStart) {
        if (rootView == null)
            return;
        if (AquaControllerData.Instance == null)
            return;
        if (!AquaControllerData.Instance.haveSettings())
            return;

        hideWaitingScreen();

        drawSensorAndRelayStates();
    }

    private Date getNowMinus4Hour() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -4);
        if (calendar.get(Calendar.MINUTE) >= 30)
            calendar.set(Calendar.MINUTE, 30);
        else
            calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    //    public void drawSensorState(int id) {
//        if (rootView == null)
//            return;
//
//        int resId;
//        switch (id) {
//            case ThermostatControllerData.BOILER_SENSOR_SOLAR_PANEL:
//                resId = R.id.boilerSensorSolarPanel;
//                break;
//            case ThermostatControllerData.BOILER_SENSOR_BOTTOM:
//                resId = R.id.boilerSensorTankBottom;
//                break;
//            case ThermostatControllerData.BOILER_SENSOR_TOP:
//                resId = R.id.boilerSensorTankTop;
//                break;
////            case ThermostatControllerData.BOILER_SENSOR_FURNACE:
////                resId = R.id.boilerSensorFurnace;
////                break;
//            default:
//                resId = 0;
//        }
//
//        if (resId != 0) {
//            ((BoilerSensorView) rootView.findViewById(resId)).setSensorData(ThermostatControllerData.Instance.boilerSensors(id));
//
//            BoilerSensorData data = ThermostatControllerData.Instance.boilerSensors(id);
//            XYSeries series = xyDataSet.getSeriesAt(id);
//
//            long tm = getNowMinus4Hour().getTime();
//            while ((series.getItemCount() > 0) && (series.getX(0) < tm))
//                series.remove(0);
//
//            float v = data.getTemperature();
//            if (!Float.isNaN(v)) {
//                series.add(data.getLastSyncTime(), v);
//
//                if ((mMaxXX != null) && (data.getLastSyncTime() >= mMaxXX.getTime())) {
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(mMaxXX);
//                    calendar.add(Calendar.MINUTE, 30);
//                    mMaxXX = calendar.getTime();
//                    mRenderer.setXAxisMax(mMaxXX.getTime());
//                    mRenderer.addXTextLabel(mMaxXX.getTime(), new SimpleDateFormat("HH:mm", Locale.US).format(mMaxXX));
//                }
//            }
//
//            mChartView.repaint();
//        }
//    }
//
//    public void drawPumpState(int id) {
//        if (rootView == null)
//            return;
//
//        int resId;
//        switch (id) {
//            case ThermostatControllerData.BOILER_SOLAR_PUMP:
//                resId = R.id.boilerPumpSolarPanel;
//                break;
//            case ThermostatControllerData.BOILER_HEATING_PUMP:
//                resId = R.id.boilerPumpHeating;
//                break;
//            case ThermostatControllerData.BOILER_FURNACE:
//                resId = R.id.boilerFurnace;
//                break;
//            //case ThermostatControllerData.BOILER_FURNACE_CIRC_PUMP:
//            //    //resId = R.id.boilerFurnace;
//            //    break;
//            default:
//                resId = 0;
//        }
//
//        if (resId != 0)
//            ((PumpView) rootView.findViewById(resId)).setState(ThermostatControllerData.Instance.boilerPumps(id).getState());
//
//    }
//
    private void drawSensorAndRelayStates() {
        ((AquaTankView) rootView.findViewById(R.id.aquaBigView)).refresh();
        ((HospitalTankView) rootView.findViewById(R.id.hospitalBigView)).refresh();
        ((SumpTankView) rootView.findViewById(R.id.sumpBigView)).refresh();
//
//        ((BoilerSensorView) rootView.findViewById(R.id.boilerSensorTankBottom)).setSensorData(ThermostatControllerData.Instance.boilerSensors(ThermostatControllerData.BOILER_SENSOR_BOTTOM));
//        ((BoilerSensorView) rootView.findViewById(R.id.boilerSensorTankTop)).setSensorData(ThermostatControllerData.Instance.boilerSensors(ThermostatControllerData.BOILER_SENSOR_TOP));
//        ((BoilerSensorView) rootView.findViewById(R.id.boilerSensorFurnace)).setSensorData(ThermostatControllerData.Instance.boilerSensors(ThermostatControllerData.BOILER_SENSOR_FURNACE));
//
//        ((PumpView) rootView.findViewById(R.id.boilerPumpSolarPanel)).setState(ThermostatControllerData.Instance.boilerPumps(ThermostatControllerData.BOILER_SOLAR_PUMP).getState());
//        ((PumpView) rootView.findViewById(R.id.boilerPumpHeating)).setState(ThermostatControllerData.Instance.boilerPumps(ThermostatControllerData.BOILER_HEATING_PUMP).getState());
//        ((PumpView) rootView.findViewById(R.id.boilerFurnace)).setState(ThermostatControllerData.Instance.boilerPumps(ThermostatControllerData.BOILER_FURNACE).getState());
//
//        drawFooter();
    }

    private void drawFooter() {
        CardView cv = ((CardView) rootView.findViewById(R.id.boilerMode));
        //((TextView) cv.getChildAt(0)).setText(ThermostatControllerData.Instance.getBoilerModeText());
        cv.setEnabled(true);
    }
}
