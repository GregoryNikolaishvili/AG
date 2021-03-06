package ge.altasoft.gia.ag.classes;

import androidx.annotation.NonNull;

import java.util.Date;

public class DeviceData implements Comparable<DeviceData> {

    final private int id;
    private int order;
    private int state;

    private String name;
    private long lastSyncTime;

    protected DeviceData(int id) {
        this.id = id;
        this.order = id;
        this.state = 0;
        this.name = AquaControllerData.getDeviceName(id);
    }

    public long getLastSyncTime() {
        return this.lastSyncTime;
    }

    public int getId() {
        return this.id;
    }

    public int getState() {
        return this.state;
    }

    public String getName() {
        return this.name;
    }

    private void setLastSyncTime() {
        lastSyncTime = new Date().getTime();
    }

    protected void setState(int value) {
        this.state = value;
        setLastSyncTime();
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void encodeSettings(StringBuilder sb) {
    }

    public int decodeSettings(String response, int idx) {
        return idx;
    }

    @Override
    public int compareTo(@NonNull DeviceData o) {
        if (Integer.valueOf(this.order).equals(o.order)) {
            return Integer.valueOf(this.id).compareTo(o.id);
        } else {
            return Integer.valueOf(this.order).compareTo(o.order);
        }
    }
}
