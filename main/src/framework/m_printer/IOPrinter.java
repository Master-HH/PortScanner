package framework.m_printer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import framework.calender.HDate;
import framework.calender.HTime;

public class IOPrinter extends Printer {
  public JSONArray dataCache;
  public JSONArray ids = new JSONArray();

  public static final int MODE_PRINT_LN = 1000;
  public static final int MODE_PRINT_MODE = 2000;
  public static final int MODE_PRINT_THROWABLE = 3000;

  public LogPrinter logPrinter = new LogPrinter() {
    @Override
    public void checkUpdate() {
      JSONArray jsonArray = this.jsonArray;
      for (int i = 0; i < jsonArray.length(); i++) {
        jsonArray.put(jsonArray.getJSONObject(i));
      }

      this.jsonArray = new JSONArray();
    }
  }, userLogPrinter;

  public IOPrinter() {
    dataCache = new JSONArray();
  }

  public IOPrinter(LogPrinter input) throws IOPrinterExeption,JSONException {
    dataCache = readLogPrinter(input);
  }

  @Override
  public void println(String print, HDate date, HTime time) {
    logPrinter.println(print, date, time);

    if (userLogPrinter != null) {
      userLogPrinter.println(print, date, time);
    }
  }

  @Override
  public void printMode(int mode, Object data, HDate date, HTime time) {
    logPrinter.printMode(mode, data, date, time);

    if (userLogPrinter != null) {
      userLogPrinter.printMode(mode, data, date, time);
    }
  }

  @Override
  public void print(Throwable throwable, HDate date, HTime time) {
    logPrinter.print(throwable, date, time);

    if (userLogPrinter != null) {
      userLogPrinter.print(throwable, date, time);
    }
  }

  public IOPrinter setLogPrinter(File dir) {
    userLogPrinter = new LogPrinter(dir);
    return this;
  }

  public LogPrinter enableLogPrinter(File dir) {
    setLogPrinter(dir);

    return userLogPrinter;
  }

  public LogPrinter getLogPrinter() {
    return userLogPrinter;
  }

  public JSONArray getData() {
    return dataCache;
  }

  public void writeInto(Printer printer) {
    JSONArray data = new JSONArray(dataCache.toString());

    write(data, printer);
  }

  public static void write(JSONArray data, Printer output) {
    for (int i = 0; i < data.length(); i++) {
      try {
        JSONObject jsonObject = data.getJSONObject(i);
        if (jsonObject.opt(LogPrinter.J_STRING_DATA) != null) {
          // println
          output.println(jsonObject.getString(LogPrinter.J_STRING_DATA), getDate(jsonObject.getJSONObject(LogPrinter.J_DATE)), getTime(jsonObject.getJSONObject(LogPrinter.J_TIME)));
        } else if (jsonObject.optInt(LogPrinter.J_MODE, Printer.NULL_MODE) != Printer.NULL_MODE) {
          //printMode
          output.printMode(jsonObject.getInt(LogPrinter.J_MODE), readData(jsonObject, LogPrinter.J_DATA), getDate(jsonObject.getJSONObject(LogPrinter.J_DATE)), getTime(jsonObject.getJSONObject(LogPrinter.J_TIME)));
        } else if (jsonObject.optString(LogPrinter.J_DATA + LogPrinter.J_EX_NAME, null) != null || jsonObject.optBoolean(LogPrinter.J_DATA + "NULL_DATA", false)) {
          //print a Throwable
          output.print((MException) readData(jsonObject, LogPrinter.J_DATA), getDate(jsonObject.getJSONObject(LogPrinter.J_DATE)), getTime(jsonObject.getJSONObject(LogPrinter.J_TIME)));
        } else {
          // TODO: ۱۵/۰۸/۲۰۲۲ fix
          output.printAsUnknownError(data.toString());
        }
      }catch (IOPrinterExeption e){
        // TODO: ۱۵/۰۸/۲۰۲۲ fix
        output.printAsUnknownError(data.toString());
      }
    }
  }

  private static Object readData(JSONObject jsonObject, String subName) throws IOPrinterExeption {
    if (jsonObject.optBoolean(subName + "[NULL_DATA]", false)) {
      return null;
    }

    if (jsonObject.get(subName + LogPrinter.J_STRING_DATA) != null) {
      // string or number
      return jsonObject.get(subName+LogPrinter.J_STRING_DATA);
    }else if (jsonObject.optString(subName+LogPrinter.J_EX_NAME,null) != null){
      //Throwable
      MException exception = new MException(jsonObject.getString(subName+LogPrinter.J_MESSAGE));
      if (!jsonObject.optBoolean(subName+LogPrinter.J_STRACK_TRACE+"[NULL_DATA]",true)){
        //writeStackTrace == true
        exception.setStackTrace(readStackTrace(jsonObject,subName+LogPrinter.J_STRACK_TRACE));
      }

      if(jsonObject.opt(subName+LogPrinter.J_INNER_CODE) != null){
        //Custom Exeption
        if(jsonObject.optJSONObject(subName + LogPrinter.J_DATE) != null){
          exception.date = getDate(jsonObject.getJSONObject(subName + LogPrinter.J_DATE));
        }
        if(jsonObject.optJSONObject(subName + LogPrinter.J_TIME) != null){
          exception.time = getTime(jsonObject.getJSONObject(subName + LogPrinter.J_TIME));
        }
        exception.innerCode = jsonObject.getInt(subName+LogPrinter.J_INNER_CODE);
        exception.data = readData(jsonObject,subName + "-[data]");
      }
      return exception;
    }

    throw new IOPrinterExeption(false,null);
  }

  private static HDate getDate(JSONObject jsonObject) {
    if (jsonObject.optBoolean("NULL_DATA", false)) {
      return null;
    }
    return new HDate(jsonObject);
  }

  private static HTime getTime(JSONObject jsonObject) {
    if (jsonObject.optBoolean("NULL_DATA", false)) {
      return null;
    }
    return new HTime(jsonObject);
  }

  public static JSONArray readLogPrinter(LogPrinter logPrinter) throws JSONException, IOPrinterExeption {
    return readLogPrinter(logPrinter.dir);
  }

  public static JSONArray readLogPrinter(File dir) throws JSONException, IOPrinterExeption {
    if (!dir.exists()) {
      throw new IOPrinterExeption(true, dir);
    }

    JSONArray out = new JSONArray();

    if (dir.isDirectory()) {
      for (File file : dir.listFiles()) {
        readFile(out, file);
      }
    } else {
      readFile(out, dir);
    }

    return out;
  }

  private static void readFile(JSONArray jsonArray, File dir) throws JSONException, IOPrinterExeption {
    DataInputStream stream = null;
    try {
      stream = new DataInputStream(new FileInputStream(dir));

      if (stream.readInt() != LogPrinter.VERSION) {
        throw new IOPrinterExeption(false, dir);
      }
      int size = stream.readInt();

      byte[] bytes = new byte[size];
      stream.readFully(bytes);
      JSONArray fileData = new JSONArray(new String(bytes));
      for (int i = 0; i < fileData.length(); i++) {
        jsonArray.put(fileData.getJSONObject(i));
      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      throw new IOPrinterExeption(true, dir);
    } catch (IOException e) {
      e.printStackTrace();

      // TODO: ۱۵/۰۸/۲۰۲۲ fix
    }

    try {
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static StackTraceElement[] readStackTrace(JSONObject jsonObject, String subName) {
    // TODO: ۱۴/۰۸/۲۰۲۲ fix

    return new StackTraceElement[0];
  }

  public static class IOPrinterExeption extends Exception {
    private final boolean notFind;
    private final File dir;

    private IOPrinterExeption(boolean notFind, File dir) {
      this.notFind = notFind;
      this.dir = dir;
    }

    public boolean isFileNotFoundException() {
      return notFind;
    }


    public boolean isVersionMismatchException() {
      return !notFind;
    }

    public File getDirException() {
      return dir;
    }
  }

  public static class MException extends Exception{
    public HTime time;
    public HDate date;
    public int innerCode;
    public Object data;

    public MException(String message) {
      super(message);
    }
  }
}
