package ge.altasoft.gia.ag.classes;

public enum WidgetType {
    Device,
    Sensor;

    public static WidgetType fromInt(int value) {
        switch (value) {
            case 0:
                return Device;
            case 1:
                return Sensor;
            default:
                return null;
        }
    }
}
