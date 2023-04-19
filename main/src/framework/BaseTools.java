package framework;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class BaseTools {
  public static final String COPY_TELEGRAM_SIMBOL = "‘";

  public static String toString(StackTraceElement[] stackTrace){
    return Arrays.toString(stackTrace);
  }

  public static String limit(String all, int max) {
    String limit1 = all.replace("\n", " ");
    if (max >= 6 && limit1.length() > max) {
      return limit1.substring(0, max - 4) + " ...";
    }
    return limit1;
  }

  public static int max(int... data){
    int out = Integer.MIN_VALUE;
    for (int datum : data) {
      if (out < datum){
        out = datum;
      }
    }
    return out;
  }

  public static int max(ArrayList<Integer> data){
    int out = Integer.MIN_VALUE;
    for (int datum : data) {
      if (out < datum){
        out = datum;
      }
    }
    return out;
  }

  public static boolean measurement(String data, char[] correct) {
    ArrayList<Character> chats = new ArrayList<Character>();
    for (int i = 0; i < correct.length; i++) {
      chats.add(correct[i]);
    }
    for (char c : data.toCharArray()) {
      if (!chats.contains(c)) {
        return false;
      }
    }

    return true;
  }

  public static String generateFileName(String folder, String name) {
    String newName = name;
    int number = 0;
    while (new File(folder + "/" + newName).exists()) {
      number++;
      newName = name + number;
    }
    return newName;
  }

  public static boolean isLanguage(String language) {
    return Locale.getDefault().getLanguage().equalsIgnoreCase(language);
  }

  public static void closeStream(OutputStream stream) {
    flush(stream);
    close(stream);
  }

  public static void closeStream(InputStream stream){
    close(stream);
  }

  public static void flush(Flushable flushable){
    try {
      flushable.flush();
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public static void close(Closeable closeable){
    try {
      closeable.close();
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public static String streamToString(InputStream inputStream) {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder stringBuilder = new StringBuilder();

    String line = null;
    try {
      while ((line = bufferedReader.readLine()) != null) {
        stringBuilder.append(line).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return stringBuilder.toString();
  }

  public static void delete(File file) {
    if (!file.exists()) {
      return;
    }
    if (file.isDirectory()) {
      for (File listFile : file.listFiles()) {
        delete(listFile);
      }
    } else {
      file.delete();
    }
  }

  // app tools

  public static final CharSequence FULL_IT = "وارد کردن این فیلد اجباری است...";
  public static final char[] CORRECT_CHAR_USER_NAME = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
    'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

  public static String getErrorOfUserNameWork(String userName) {
    // TODO: 18/07/2020 change lan
    if (userName.length() == 0) {
      return FULL_IT.toString();
    }
    if (userName.length() < 3) {
      return "نام کاربری باید بیشتر از 3 کاراکتر باشد...";
    }
    if (!measurement(userName.toLowerCase(), CORRECT_CHAR_USER_NAME)) {
      return "شناسه مجاز به وارد کردن حروف و اعداد انگلیسی است...";
    }
    return null;
  }


  public enum Language {
    Persian, English
  }
}
