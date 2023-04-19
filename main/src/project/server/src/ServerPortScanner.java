package project.server.src;

import java.util.Scanner;

public class ServerPortScanner {
  public static void main(String[] args) {
    System.out.println("starting " + ServerPortScanner.class.getSimpleName());

    System.out.println("enter your ip project.server address...");

    Scanner scanner = new Scanner(System.in);

    String host = scanner.nextLine(); // replace with the hostname or IP address of the target machine

    System.out.println("enter port...");

    int port = Integer.parseInt(scanner.nextLine());

    System.out.println("enter your minimum port number for listenning");

    int minPort = Integer.parseInt(scanner.nextLine()); // minimum port number

    System.out.println("enter your maximum port number for listenning");

    int maxPort = Integer.parseInt(scanner.nextLine());//65535; // maximum port number


  }


}
