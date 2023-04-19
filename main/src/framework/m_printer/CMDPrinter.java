package framework.m_printer;

import framework.calender.HDate;
import framework.calender.HTime;

public class CMDPrinter extends Printer {
  @Override
  public void println(String print, HDate date, HTime time) {
    print(print, date, time);
  }

  @Override
  public void printMode(int errorMode, Object data, HDate date, HTime time) {
    switch (errorMode){
      case Printer.KNOWN_ERROR_MODE:
        print("error HH:error Mode: "+errorMode+"\r\n error\r\n error \t\t data:\r\n"+data,date,time);
        break;
      case Printer.UNKNOWN_ERROR_MODE:
        print("error HH:error Mode: "+errorMode+ "(UNKNOWN_ERROR_MODE) \r\n crash\r\n crash \t\t data:\r\n"+data,date,time);
        break;
    }
  }

  @Override
  public void print(Throwable throwable, HDate date, HTime time) {
    print(throwable.getClass().getName()+" \t message: \r\n"+throwable.getMessage(),date,time);
  }

  public static void print(String print, HDate date, HTime time) {
    String out = "";
    if (date != null) out = date.toString()+" \t ";
    if (time != null) out = out + time.toString()+" \t : ";
    System.out.println(out + print);
  }
}
