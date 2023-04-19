package project.client;

import org.json.JSONArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import static project.H.print;

public class ScannerClient {
  private static Socket socket = null;
  private static DataInputStream dataInputStream = null;
  private static DataOutputStream dataOutputStream = null;


  public static void start(final String server, final String mainServer, final int mianPort, final int timeout) {
    final JSONArray result = new JSONArray();
    final JSONArray scaned = new JSONArray();
    final ArrayList<Integer> allPorts = new ArrayList<Integer>();
    final Random random = new Random();

    final Thread portManaget = new Thread(new Runnable() {
      @Override
      public void run() {
        int index = 0;
        while (!socket.isClosed()) {
          Socket socket = new Socket();
          try {
            socket.setSoTimeout(timeout);
            scaned.put(allPorts.get(index));
            socket.connect(new InetSocketAddress(server, allPorts.get(index)));

            OutputStream outputStream = socket.getOutputStream();

            for (int i = 0; i < 1024; i++) {
              outputStream.write(random.nextInt(1));
            }

            result.put(allPorts.get(index));
            Thread.sleep(1000);
          } catch (Throwable e) {
            e.printStackTrace();
            print(e);
          }finally {
            try {
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          index++;
        }

      }
    }, "port_manager");

    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          socket = new Socket(mainServer, mianPort);
          dataInputStream = new DataInputStream(socket.getInputStream());
          dataOutputStream = new DataOutputStream(socket.getOutputStream());

          while (true) {
            switch (dataInputStream.read()) {
              case 10://adding port
                allPorts.add(dataInputStream.readInt());
                break;
              case 20://finished range of port
                break;
              case 30://finished service
                break;
              case 100://starting scanning
                portManaget.start();
                break;
            }
          }
        } catch (UnknownHostException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }, "c)main_server").start();

  }
}
