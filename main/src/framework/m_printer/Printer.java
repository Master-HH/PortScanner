package framework.m_printer;

import framework.calender.HDate;
import framework.calender.HTime;

public abstract class Printer {
  public static final long CE_SLEEP_TIME = 3000;
  public boolean needCallExeptionOnErrorMode = true;

  public static final int KNOWN_ERROR_MODE = 700;
  public static final int UNKNOWN_ERROR_MODE = 600;
  public static final int NULL_MODE = Integer.MIN_VALUE;

  private boolean printDate = true, printTime = true;

  Printer() {

  }

  public static String getCustomExeptionMessage(int inerErrorCode, Object data) {
    return "HH error:Custom \t    inerErrorCode : "+inerErrorCode+" \t    data: \r\n "+data;
  }

  public abstract void println(String print, HDate date, HTime time);

  public abstract void printMode(int mode, Object data, HDate date, HTime time);

  public abstract void print(Throwable throwable, HDate date, HTime time);

  public void println(){
    println("",null,null);
  }

  /*public void println(Color color,String print){
    println(color,print,printDate ? new HDate() : null, printTime ? new HTime() : null);
  }*/

  public void println(String print) {

    println(print,printDate ? new HDate() : null, printTime ? new HTime() : null);
  }

  public void println(String print, HTime time) {
    println(print, printDate ? new HDate() : null,  printTime ? time : null);
  }

  public void print(Throwable throwable) {
    print(throwable,printDate ? new HDate() : null,  printTime ? new HTime() : null);
  }

  public void print(Throwable throwable, HTime time) {
    print(throwable, printDate ? new HDate() : null,  printTime ? time : null);
  }

  public void printMode(int errorMode, Object data) {
    printMode(errorMode, data, printDate ? new HDate() : null,  printTime ? new HTime() : null);
  }

  public void printMode(int errorMode, Object data, HTime time) {
    printMode(errorMode, data,printDate ? new HDate() : null,  printTime ? time : null);
  }

  public Printer setFilter(Filter filter) {
    switch (filter) {
      case developerMode:
        printTime = false;
      case DisableDate:
        printDate = false;
        break;
      case DisableTime:
        printTime = false;
        break;
      default:
        printDate = true;
        printTime = true;
        break;
    }
    return this;
  }

  /**
   * could be a dialog or main command that stop user from work with program
   * <p>
   * error code 600
   */
  public void printAsUnknownError(Object data) {
    printMode(UNKNOWN_ERROR_MODE, data, new HDate(), new HTime());
  }

  /**
   * error code 700
   */
  public void printAsKnownError(CustomExeption exeption) {
    if (exeption.run(Printer.this)) {
      printMode(KNOWN_ERROR_MODE, exeption);
      if (needCallExeptionOnErrorMode) {
        print(exeption);
      }
    } else {
      // low exeption ( warning)
      print(exeption);
    }
  }

  public enum Filter {
    DisableDate, DisableTime, developerMode
  }
}
