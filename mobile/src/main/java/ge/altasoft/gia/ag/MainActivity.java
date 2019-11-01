package ge.altasoft.gia.ag;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Map;

import ge.altasoft.gia.ag.classes.AquaControllerData;
import ge.altasoft.gia.ag.classes.ChaFragment;
import ge.altasoft.gia.ag.classes.WidgetType;
import ge.altasoft.gia.ag.other.WaterLevelSettingsActivity;

public class MainActivity extends ChaActivity {

    private final Handler timerHandler = new Handler();

    private SectionsPagerAdapter pagerAdapter;

    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            for (int i = 0; i < pagerAdapter.getCount(); i++)
                ((ChaFragment) pagerAdapter.getItem(i)).refreshWidgets();

            timerHandler.postDelayed(this, 60000);
        }
    };
    private Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Utils.isTablet(this))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Country House Automation");

        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), Utils.isTablet(this));

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(pagerAdapter);

        if (savedInstanceState == null) {
            viewPager.setCurrentItem(1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        this.mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_ok:
            case R.id.action_cancel:
                this.mainMenu.findItem(R.id.action_ok).setVisible(false);
                this.mainMenu.findItem(R.id.action_cancel).setVisible(false);
                this.mainMenu.findItem(R.id.action_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

                for (int I = 2; I < this.mainMenu.size(); I++)
                    this.mainMenu.getItem(I).setEnabled(true);

                for (int i = 0; i < pagerAdapter.getCount(); i++)
                    ((ChaFragment) pagerAdapter.getItem(i)).setDraggableViews(false);


                if (id == R.id.action_ok) {
                    for (int i = 0; i < pagerAdapter.getCount(); i++)
                        ((ChaFragment) pagerAdapter.getItem(i)).saveWidgetOrders();
                } else
                    rebuildUI(false);

                return true;

            case R.id.action_reorder_widgets:
                this.mainMenu.findItem(R.id.action_ok).setVisible(true);
                this.mainMenu.findItem(R.id.action_cancel).setVisible(true);
                this.mainMenu.findItem(R.id.action_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

                for (int I = 2; I < this.mainMenu.size(); I++)
                    this.mainMenu.getItem(I).setEnabled(false);

                for (int i = 0; i < pagerAdapter.getCount(); i++)
                    ((ChaFragment) pagerAdapter.getItem(i)).setDraggableViews(true);

                return true;


            case R.id.action_refresh:
                publish("aquagodc/refresh", "1", false);
                return true;

            case R.id.action_show_info:
                showNetworkInfo();
                return true;

            case R.id.who_is_online:
                Intent intent = new Intent(this, WhoIsOnlineActivity.class);
                intent.putStringArrayListExtra("list", getMqttClient().getConnectedClientList());
                startActivity(intent);
                return true;

            case R.id.action_settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), Utils.ACTIVITY_REQUEST_RESULT_SETTINGS);
                return true;

            case R.id.action_water_level_settings:
                startActivityForResult(new Intent(this, WaterLevelSettingsActivity.class), Utils.ACTIVITY_REQUEST_RESULT_SETTINGS);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        rebuildUI(true);

        timerHandler.postDelayed(timerRunnable, 60000);

        //Utils.analyseStorage(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();

        drawControllerStatus(false, R.id.controllerIsOnlineDash);
        drawControllerStatus(false, R.id.controllerIsOnlineDevice);
        drawControllerStatus(false, R.id.controllerIsOnlineSensor);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case Utils.ACTIVITY_REQUEST_RESULT_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    publish("aquagodc/settings", AquaControllerData.Instance.encodeSettings(), false);
                }
                clearUnneededPreferences();
                break;
        }
    }

    private void clearUnneededPreferences() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (!(key.equals("mtqq_url_local") || key.equals("mtqq_url_global") || key.equals("dashboard_items")))
                editor.remove(key);
        }
        editor.apply();
    }

    private void rebuildUI(boolean isStart) {
        for (int i = 0; i < pagerAdapter.getCount(); i++)
            ((ChaFragment) pagerAdapter.getItem(i)).rebuildUI(isStart);
    }

    @Override
    public void processMqttData(MqttClientLocal.MQTTReceivedDataType dataType, Intent intent) {
        super.processMqttData(dataType, intent);

        int id;
        int state;
        long boardTimeInSec;
        StringBuilder sb;

        switch (dataType) {
            case WrtState:
                drawWrtStatus(Utils.lastMqttConnectionWrtIsOnline, R.id.wrtIsOnlineDash);
                drawWrtStatus(Utils.lastMqttConnectionWrtIsOnline, R.id.wrtIsOnlineDevice);
                drawWrtStatus(Utils.lastMqttConnectionWrtIsOnline, R.id.wrtIsOnlineSensor);
                break;

            case ClientConnected:
                String clientId = intent.getStringExtra("id");
                boolean value;
                switch (clientId) {
                    case "Aquagod3":
                        value = intent.getBooleanExtra("value", false);
                        drawControllerStatus(value && AquaControllerData.Instance.isAlive(), R.id.controllerIsOnlineDash);
                        drawControllerStatus(value && AquaControllerData.Instance.isAlive(), R.id.controllerIsOnlineDash);
                        drawControllerStatus(value && AquaControllerData.Instance.isAlive(), R.id.controllerIsOnlineSensor);
                        break;
                }
                break;

            case AquagodControllerAlive:
                boardTimeInSec = intent.getLongExtra("BoardTimeInSec", 0);
                AquaControllerData.Instance.SetAlive(boardTimeInSec);
                drawControllerStatus(AquaControllerData.Instance.isAlive(), R.id.controllerIsOnlineDash);
                drawControllerStatus(AquaControllerData.Instance.isAlive(), R.id.controllerIsOnlineDevice);
                drawControllerStatus(AquaControllerData.Instance.isAlive(), R.id.controllerIsOnlineSensor);
                break;

            case AquagodControllerState:
                sb = new StringBuilder();
                state = intent.getIntExtra("state", 0);

                if (state != 0) {
                    if ((state & Utils.ERR_SYSTEM) != 0) {
                        sb.append("System error");
                        sb.append("\r\n");
                    }
                    if ((state & Utils.ERR_TIME_NOT_SET) != 0) {
                        sb.append("Time not set");
                        sb.append("\r\n");
                    }
                    if ((state & Utils.ERR_INTERNET) != 0) {
                        sb.append("No internet connection error");
                        sb.append("\r\n");
                    }
                    if ((state & Utils.ERR_ULTRASONIC) != 0) {
                        sb.append("Ultrasonic sensor error");
                        sb.append("\r\n");
                    }
                    if ((state & Utils.ERR_TEMPERATURE_1) != 0) {
                        sb.append("Temperature sensor #1 error ");
                        sb.append("\r\n");
                    }

                    if ((state & Utils.ERR_TEMPERATURE_2) != 0) {
                        sb.append("Temperature sensor #2 error");
                        sb.append("\r\n");
                    }
                    if ((state & Utils.ERR_TEMPERATURE_3) != 0) {
                        sb.append("Temperature sensor #3 error");
                        sb.append("\r\n");
                    }
                    if ((state & Utils.ERR_TEMPERATURE_4) != 0) {
                        sb.append("Temperature sensor #4 error");
                        sb.append("\r\n");
                    }
                    if ((state & Utils.ERR_TEMPERATURE_5) != 0) {
                        sb.append("Temperature sensor #5 error");
                        sb.append("\r\n");
                    }


                    if (sb.length() >= 2) // delete last \r\n
                        sb.setLength(sb.length() - 2);

                    if (pagerAdapter.fragmentDashboard != null)
                        pagerAdapter.fragmentDashboard.drawControllersState("WL", sb);

                    Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 3000);
                } else if (pagerAdapter.fragmentDashboard != null)
                    pagerAdapter.fragmentDashboard.drawControllersState("WL", null);

                break;
            //endregion

            case Log:
                break;


            case AquagodDeviceState:
                id = intent.getIntExtra("id", -1);
                pagerAdapter.fragmentDevices.drawState(id);
                pagerAdapter.fragmentDashboard.drawWidgetState(WidgetType.Device, id);
                pagerAdapter.fragmentTank.rebuildUI(false);
                break;

            case AquagodSensorState:
                id = intent.getIntExtra("id", -1);
                pagerAdapter.fragmentSensors.drawState(id);
                pagerAdapter.fragmentDashboard.drawWidgetState(WidgetType.Sensor, id);
                pagerAdapter.fragmentTank.rebuildUI(false);
                break;

            case AquagodSettings:
                pagerAdapter.fragmentSensors.rebuildUI(false);
                pagerAdapter.fragmentDashboard.rebuildUI(false);
                break;
        }
    }

    private void drawControllerStatus(boolean isOK, int resId) {
        ImageView image = (ImageView) findViewById(resId);
        if (image != null) {
            image.setImageResource(isOK ? R.drawable.circle_green : R.drawable.circle_red);
        }
    }

    private void drawWrtStatus(boolean isOK, int resId) {
        ImageView image = (ImageView) findViewById(resId);
        if (image != null) {
            image.setImageResource(isOK ? R.drawable.wifi_on : R.drawable.wifi_off);
        }
    }

    private void showNetworkInfo() {
        StringBuilder sb = new StringBuilder();

        String info = Utils.getNetworkInfo(this);
        if (info != null)
            sb.append(info);

        String url = Utils.getMtqqBrokerUrl(this);
        if (url == null)
            url = "(null)";
        sb.append("\nUrl - ");
        sb.append(url);

        Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
    }

    private static class SectionsPagerAdapter extends FragmentPagerAdapter {

        final private boolean isLandscape;

        FragmentBoiler fragmentTank = null;
        FragmentDashboard fragmentDashboard = null;
        FragmentDevices fragmentDevices = null;
        FragmentSensors fragmentSensors = null;

        SectionsPagerAdapter(FragmentManager fm, boolean isLandscape) {
            super(fm);

            this.isLandscape = isLandscape;

            fragmentTank = new FragmentBoiler();
            fragmentDashboard = new FragmentDashboard();
            fragmentDevices = new FragmentDevices();
            fragmentSensors = new FragmentSensors();
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return fragmentTank;
                case 1:
                    return fragmentDashboard;
                case 2:
                    return fragmentDevices;
                case 3:
                    return fragmentSensors;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Tank";
                case 1:
                    return "Dashboard";
                case 2:
                    return "Devices";
                case 3:
                    return "Sensors";
            }
            return null;
        }

        @Override
        public float getPageWidth(int position) {
            return (isLandscape ? 0.5f : 1.0f);
        }
    }
}
