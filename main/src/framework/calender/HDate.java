package framework.calender;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class HDate {
  public static final String J_YEAR = "year";
  public static final String J_MONTH = "month";
  public static final String J_DAY = "day";

  public final int year, month, day;

  public HDate(int year, int month, int day) {
    this.year = year;
    this.month = month;
    this.day = day;
  }

  public HDate(Calendar calendar) {
    this(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
  }

  public HDate(){
    this(Calendar.getInstance());
  }

  public HDate(HCalendar calender){
    this(calender.getYear(),calender.getMonth(),calender.getDay());
  }

  public HDate(JSONObject jsonObject) throws JSONException {
    this(jsonObject.getInt(J_YEAR), jsonObject.getInt(J_MONTH), jsonObject.getInt(J_DAY));
  }

  public HDate(DataInputStream inputStream) throws IOException {
    this(inputStream.readInt(), inputStream.readInt(), inputStream.readInt());
  }

  public static HDate[] pars(ArrayList<HDate> data) {
    HDate[] dates = new HDate[data.size()];
    for (int i = 0; i < data.size(); i++) {
      dates[i] = data.get(i);
    }
    return dates;
  }

  public Calendar getCalendar(){
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR,year);
    calendar.set(Calendar.MONTH,month-1);
    calendar.set(Calendar.DAY_OF_MONTH,day);
    return calendar;
  }

  public HCalendar getHCalenderToPersion(){
    return new HCalendar(HCalendar.Type.ToPersion,this);
  }

  public HCalendar getHCalenderToGreqorian(){
    return new HCalendar(HCalendar.Type.ToGreqorian,this);
  }

  public boolean equale(HDate o) {
    return o.year == year && o.month == month && o.day == day;
  }

  public void writeInStream(OutputStream stream) throws IOException {
    DataOutputStream outputStream = new DataOutputStream(stream);
    writeInStream(outputStream);
  }

  public void writeInStream(DataOutputStream outputStream) throws IOException {
    outputStream.writeInt(year);
    outputStream.writeInt(month);
    outputStream.writeInt(day);
  }

  public JSONObject toJSON() {
    JSONObject jsonObject = new JSONObject();
    try {
      jsonObject.put(J_YEAR, year);
      jsonObject.put(J_MONTH, month);
      jsonObject.put(J_DAY, day);
      return jsonObject;
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public String toString(String space) {
    return String.format("%04d"+space+"%02d"+space+"%02d", year,month,day);
  }
  @Override
  public String toString(){
    return toString(" / ");
  }
}
