package framework.calender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class HTime {
  public static final String J_HOUR = "hour";
  public static final String J_MIN = "min";
  public static final String J_SEC = "sec";

  public final int hour, min, sec;

  public HTime(int hour, int min, int sec) {
    this.hour = hour;
    this.min = min;
    this.sec = sec;
  }

  public HTime(Calendar calendar) {
    this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
  }

  public HTime(){
    this(Calendar.getInstance());
  }

  public HTime(HCalendar calender){
    this(calender.getYear(),calender.getMonth(),calender.getDay());
  }

  public HTime(JSONObject jsonObject) throws JSONException {
    this(jsonObject.getInt(J_HOUR), jsonObject.getInt(J_MIN), jsonObject.getInt(J_SEC));
  }

  public HTime(DataInputStream inputStream) throws IOException {
    this(inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
  }

  public static HTime[] pars(ArrayList<HTime> data) {
    HTime[] dates = new HTime[data.size()];
    for (int i = 0; i < data.size(); i++) {
      dates[i] = data.get(i);
    }
    return dates;
  }

  public Calendar getCalendar(){
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY,hour);
    calendar.set(Calendar.MINUTE,min);
    calendar.set(Calendar.SECOND,sec);
    return calendar;
  }

  public boolean equale(HTime o) {
    return o.hour == hour && o.min == min && o.sec == sec;
  }

  public void writeInStream(OutputStream stream) throws IOException {
    DataOutputStream outputStream = new DataOutputStream(stream);
    writeInStream(outputStream);
  }

  public void writeInStream(DataOutputStream outputStream) throws IOException {
    outputStream.writeInt(hour);
    outputStream.writeInt(min);
    outputStream.writeInt(sec);
  }

  public JSONObject toJSON() {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put(J_HOUR, hour);
      jsonObject.put(J_MIN, min);
      jsonObject.put(J_SEC, sec);
      return jsonObject;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String toString(String space) {
    return String.format("%02d"+space+"%02d"+space+"%02d", hour,min,sec);
  }
  @Override
  public String toString(){
    return toString(" : ");
  }
}
