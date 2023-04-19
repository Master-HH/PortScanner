package project.server.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import project.H;

import static project.H.print;

public class ScannerServer {
  public static void start(final String mainServer, final int mainServerPort, final int maxThread, final int minPort, final int maxPort) {
    H.start();
    final ArrayList<Integer> ports = new ArrayList<Integer>();
    final ArrayList<Boolean> run = new ArrayList<Boolean>();

    new Thread(new Runnable() {
      int indexPort = minPort;

      @Override
      public void run() {
        boolean finished = false;

        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
          Socket socket = new Socket(mainServer, mainServerPort);
          dataInputStream = new DataInputStream(socket.getInputStream());
          dataOutputStream = new DataOutputStream(socket.getOutputStream());

          for (int i = 0; i < maxThread; i++) {
            dataOutputStream.write(10);//new port
            dataOutputStream.writeInt(newPort());
          }


          dataOutputStream.write(100);//start listenning....

          while (true) {
            switch (dataInputStream.read()) {
              case 1:
                // disable port and new port
                int diablePort = dataInputStream.readInt();
                run.set(ports.indexOf(diablePort), false);

                dataOutputStream.write(10);//new port
                dataOutputStream.writeInt(newPort());
                break;
            }

            if (indexPort > maxPort) {
              dataOutputStream.write(20);//notify finishing all ports
              finished = true;
            }

            if (finished) {
              if (checkFinished()) {
                dataOutputStream.write(30);//finishing all opens ports
                break;
              }
            }
          }
          socket.close();
        } catch (UnknownHostException e) {
          e.printStackTrace();
          print(e);

        } catch (IOException e) {
          e.printStackTrace();
          print(e);
        }
      }

      public boolean checkFinished() {
        for (Boolean aBoolean : run) {
          if (aBoolean) {
            return false;
          }
        }

        return true;
      }

      public int newPort() {
        ports.add(indexPort);
        run.add(true);

        newThread(ports.size() - 1);
        indexPort++;
        return indexPort - 1;
      }

      public void newThread(final int index) {

        new Thread(new Runnable() {

          ServerSocket serverSocket = null;

          @Override
          public void run() {
            int index = 0;
            try {
              serverSocket = new ServerSocket(ports.get(index));
              Socket socket = serverSocket.accept();
              InputStream inputStream = socket.getInputStream();

              while (!socket.isClosed()){
                if(inputStream.read() == -1){
                  break;
                }
                index++;
              }

            } catch (Throwable e) {
              e.printStackTrace();
              print(e);
            }finally {
              close(index);
            }

          }

          public void close(int buff){
            printbuff(buff);

            try {
              serverSocket.close();
            } catch (Throwable e) {
              e.printStackTrace();
            }
          }
        }, "port_watch_" + ports.get(index)).start();
      }
    }, "s)mian-server").start();
//    printer.println("new order:maxThread:"+maxPort);
    for (int port = minPort; port <= maxPort; port++) {
      try {
      } catch (Exception e) {
        // port is closed or filtered
      }
    }
  }

  private static void printbuff(int buff) {
    // TODO: ۱۹/۰۴/۲۰۲۳ to do
  }
}
