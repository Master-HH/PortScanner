package framework.m_printer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import framework.calender.HDate;
import framework.calender.HTime;

public class LogPrinter extends Printer {


  static final String J_DATE = "date";
  static final String J_TIME = "time";
  static final String J_STRING_DATA = "st-data";
  static final String J_EX_NAME = "exeption-name";
  static final String J_INNER_CODE = "inner-error-code";
  static final String J_STRACK_TRACE = "strack-trace";
  static final String J_MESSAGE = "log";
  static final String J_MODE = "mode";
  static final String J_DATA = "data";

  private static final long EXTINCTION_TIME = 3000;
  private static final long SLEEP_TIME = EXTINCTION_TIME;
  public static final int VERSION = 1;
  static final JSONObject NULL_DATA = new JSONObject();

  //debuge mode
  public boolean writeStackTrace = true;
  public static final boolean ENABLE_THREAD_COLLECTOR = true;

  /**
   * set how much heavy can be the log file
   * if log file was very heavy , log file would be divided by some number
   * set limit if this value was true {@link #limitValue}
   */
  private boolean logLimit = true;
  /**
   * how much character could save into one log file
   * see {@link #logLimit}
   */
  public short limitValue = 2000/*char*/;

  private String fileNames = "log";

  JSONArray jsonArray;
  private Printer debugPrinter;
  File dir;
  long lastUpdate;
  int logNumber;
  boolean inSleep = false;
  private LogFilter logFilter = LogFilter.All;

  /**
   * @param dir            a folder for saving data
   */
  public LogPrinter(File dir) {
    this.dir = dir;
    this.jsonArray = new JSONArray();
    logNumber = 0;
    lastUpdate = System.currentTimeMillis();

    NULL_DATA.put("NULL_DATA", true);

    if (!dir.exists()) {
      dir.mkdirs();
    }

    getNewLogNumber();
  }

  LogPrinter(){
    NULL_DATA.put("NULL_DATA", true);
  }


  @Override
  public void println(String print, HDate date, HTime time) {
    if (logFilter == LogFilter.All) {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(J_DATE, date == null ? NULL_DATA : date.toJSON());
      jsonObject.put(J_TIME, time == null ? NULL_DATA : time.toJSON());
      jsonObject.put(J_STRING_DATA, print == null ? "" : print);

      jsonArray.put(jsonObject);

      checkUpdate();
    }
  }

  @Override
  public void printMode(int mode, Object data, HDate date, HTime time) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(J_DATE, date == null ? NULL_DATA : date.toJSON());
    jsonObject.put(J_TIME, time == null ? NULL_DATA : time.toJSON());
    jsonObject.put(J_MODE, mode);

    writeData(jsonObject, data, J_DATA);

    jsonArray.put(jsonObject);

    checkUpdate();
  }

  @Override
  public void print(Throwable throwable, HDate date, HTime time) {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(J_DATE, date == null ? NULL_DATA : date.toJSON());
    jsonObject.put(J_TIME, time == null ? NULL_DATA : time.toJSON());
    writeData(jsonObject,throwable,J_DATA);

    jsonArray.put(jsonObject);

    checkUpdate();
  }

  public void writeData(JSONObject jsonObject, Object data, String subName) {
    if (data == null){
      jsonObject.put(subName+"[NULL_DATA]",true);
      return;
    }
    if (data instanceof String || data instanceof Number) {
      jsonObject.put(subName + J_STRING_DATA, data);
    } else if (data instanceof Throwable) {
      jsonObject.put(subName + J_EX_NAME, data.getClass().getSimpleName());
      if (data instanceof CustomExeption) {
        CustomExeption customExeption = (CustomExeption) data;
        jsonObject.put(subName + J_DATE, customExeption.date == null ? NULL_DATA : customExeption.date.toJSON());
        jsonObject.put(subName + J_TIME, customExeption.time == null ? NULL_DATA : customExeption.time.toJSON());
        jsonObject.put(subName + J_INNER_CODE, customExeption.inerErrorCode);

        writeData(jsonObject, data, subName + "-[data]");
      }
      jsonObject.put(subName + J_MESSAGE, ((Throwable) data).getMessage());

      if (writeStackTrace) {
        jsonObject.put(subName+J_STRACK_TRACE+"[NULL_DATA]",false);
        writeStackTrace(jsonObject, (Throwable) data, subName+J_STRACK_TRACE);
      }
    } else {
      // TODO: 2022/?/? fix or check...
    }
  }

  public LogPrinter setLogLimit(boolean logLimit) {
    this.logLimit = logLimit;
    return this;
  }

  public LogPrinter setFileNames(String fileNames) {
    this.fileNames = fileNames;
    return this;
  }

  public LogPrinter setDebugPrinter(Printer debugPrinter) {
    this.debugPrinter = debugPrinter;
    return this;
  }

  public synchronized void checkUpdate() {
    if (System.currentTimeMillis() - lastUpdate >= EXTINCTION_TIME) {
      // time passed and need be update...
      String data = jsonArray.toString();
      if (data.length() > limitValue) {
        jsonArray = new JSONArray();
        logNumber++;
        getNewLogNumber();
      }
      lastUpdate = System.currentTimeMillis();
      update(data);
      if (!inSleep && ENABLE_THREAD_COLLECTOR) {
        inSleep = true;
        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              Thread.sleep(SLEEP_TIME * 2);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            inSleep = false;
            checkUpdate();
          }
        }).start();
      }
    } else if (!inSleep && ENABLE_THREAD_COLLECTOR) {
      inSleep = true;
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            Thread.sleep(SLEEP_TIME);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          inSleep = false;
          checkUpdate();
        }
      }).start();
    }
  }

  public synchronized void update(String data) {
    // TODO: 2022/?/? check need Exeptions be force close (System.exit(0)) or not...
    FileOutputStream stream = null;
    try {
      stream = new FileOutputStream(dir.getAbsolutePath() + "/" + fileNames + "-" + logNumber + ".log");
      onSave(data, stream);

    } catch (FileNotFoundException e) {
      e.printStackTrace();

      if (debugPrinter != null) {
        debugPrinter.print(e);
      }
    } catch (IOException e) {
      e.printStackTrace();

      if (debugPrinter != null) {
        debugPrinter.print(e);
      }
    }

    try {
      stream.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      stream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void getNewLogNumber(){
    if (new File(dir.getAbsolutePath() + "/" + fileNames + "-" + logNumber + ".log").exists()){
      logNumber++;
      getNewLogNumber();
    }
  }

  private void writeStackTrace(JSONObject jsonObject,Throwable throwable,String subName){
    // TODO: ۱۴/۰۸/۲۰۲۲ fix
  }

  public void onSave(String jsonArrayData, FileOutputStream stream) throws IOException {
    DataOutputStream dataOutputStream = new DataOutputStream(stream);

    byte[] bytes = jsonArrayData.getBytes();

    dataOutputStream.writeInt(VERSION);
    dataOutputStream.writeInt(bytes.length);
    dataOutputStream.write(bytes);
  }

  public LogPrinter setLogFilter(LogFilter logFilter) {
    this.logFilter = logFilter;
    return this;
  }

  public enum LogFilter{
    DebugLogOnly,All
  }
}
