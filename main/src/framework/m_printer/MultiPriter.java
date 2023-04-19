package framework.m_printer;

import framework.calender.HDate;
import framework.calender.HTime;

public class MultiPriter extends Printer {

  private Printer[] printers;

  public MultiPriter(Printer... printers){
    setNewPrinters(printers);
  }


  @Override
  public void println(String print, HDate date, HTime time) {
    for (Printer printer : printers) {
      printer.println(print,date,time);
    }
  }

  @Override
  public void printMode(int mode, Object data, HDate date, HTime time) {
    for (Printer printer : printers) {
      printer.printMode(mode, data, date, time);
    }
  }

  @Override
  public void print(Throwable throwable, HDate date, HTime time) {
    for (Printer printer : printers) {
      printer.print(throwable,date,time);
    }
  }

  public MultiPriter setNewPrinters(Printer... printers){
    if (printers == null || printers.length == 0){
      throw new NullPointerException("printers is emty and can not set them into multiPrinter["+toString()+"]");
    }
    this.printers = printers;
    return this;
  }

  public Printer[] getPrinters() {
    return printers;
  }
}
