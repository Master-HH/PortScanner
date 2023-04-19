package project;
import java.io.File;

import framework.m_printer.CMDPrinter;
import framework.m_printer.LogPrinter;
import framework.m_printer.MultiPriter;
import framework.m_printer.Printer;

public final class H {
  public static File rootProject;
  public static Printer printer;

  public static void start() {
    rootProject = new File("data");
    if (!rootProject.exists()) {
      rootProject.mkdir();
    }

    File file = new File(rootProject, "logs");

    if (!file.exists()){
      file.mkdirs();
    }
    printer = new MultiPriter(new CMDPrinter(), new LogPrinter(file.getAbsoluteFile()));
  }

  public static void print(Throwable e){
    // TODO: ۱۹/۰۴/۲۰۲۳ todo 
  }
}
