package project.client;

import java.util.Scanner;

import project.server.src.ServerPortScanner;

public class ClientScannerPort {
  public static void main(String[] args) {
    System.out.println("starting "+ ServerPortScanner.class.getSimpleName());

    System.out.println("enter your ip main server address...");

    Scanner scanner = new Scanner(System.in);

    String mainServer = scanner.nextLine();


    System.out.println("enter port...");

    int port = Integer.parseInt(scanner.nextLine());

    System.out.println("enter your ip target server address...");

    String host = scanner.nextLine();

  }
}
