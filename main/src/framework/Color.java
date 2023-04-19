package framework;

public enum Color {
  reset("\033[0m"),red("\033[0;31m"),green("\033[0;32m");

  public static final boolean COLORABLE = false;
  private String color;

  Color(String color){
    this.color = color;
  }


  public static String printWithColor(Color color,String text){
    if (COLORABLE){
      return color.color + text;
    }
    return text;
  }

  public static String printAndReset(String text){
    return printWithColor(reset,text);
  }
}
