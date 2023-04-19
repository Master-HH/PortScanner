package framework.m_printer;

import framework.calender.HDate;
import framework.calender.HTime;

public abstract class CustomExeption extends RuntimeException {
  public final int inerErrorCode;
  public final Object data;
  public final HDate date;
  public final HTime time;
  public long ceSleep = Printer.CE_SLEEP_TIME;

  public CustomExeption(int inerErrorCode, Object data, HDate date, HTime time) {
    super(Printer.getCustomExeptionMessage(inerErrorCode,data));
    this.inerErrorCode = inerErrorCode;
    this.data = data;
    this.date = date;
    this.time = time;
  }

  /**
   * this method will call by printer and ...
   * you call manualy call for fixing for printing expected command with {@link #fix(Printer)} ()}
   */
  boolean run(final Printer printer){
    if(onExeption(printer)){
      if(!onFix(printer)){
        new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              Thread.sleep(ceSleep);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            
            printer.printAsUnknownError(this);
          }
        }).start();
      }
      return true;
    }else {
      return false;
    }
  }
  
  public void fix(Printer printer){
    printer.printAsKnownError(this);
  }
  
  /**
   * process and run and countinue the programe or not...
   * @return true => will call {@link #onFix(Printer)} ()} and prossecing as error <br> false => pass the Exeption
   */
  public abstract boolean onExeption(Printer printer);

  /**
   * process for fixing 
   * @return true => handed <br> false => will call as {@link Printer#printAsUnknownError(Object)}  ( printAsUnknownError )
   */
  public abstract boolean onFix(Printer printer);
}
