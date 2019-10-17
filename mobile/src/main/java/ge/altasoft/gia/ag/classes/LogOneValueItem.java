package ge.altasoft.gia.ag.classes;

import java.util.Date;

public class LogOneValueItem {
    public final Date date;
    public final int state;

    public LogOneValueItem(Date date, int state) {
        this.date = date;
        this.state = state;
    }
}
