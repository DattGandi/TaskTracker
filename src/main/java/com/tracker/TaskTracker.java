package com.tracker;

import java.sql.Connection;
import java.util.Scanner;

public class TaskTracker {
  public static void main(String[] args) {
    System.out.println("Welcome to TaskTracker!");
    System.out.println("1. Connect to existing Task Database");
    System.out.println("2. Create new Task Database");
    System.out.println("3. Exit");


    int selectedIndex = getSelectedIndex();

    ConnectionData databaseConnection;
    if(selectedIndex == 1) {
      databaseConnection = DatabaseConnection.connectToDatabase(new ShieldedInputStream(System.in));
    }
    else if(selectedIndex == 2) {
      databaseConnection = DatabaseConnection.createDatabase(new ShieldedInputStream(System.in));
    }
    else {
      System.out.println("Shutting down...");
      return;
    }

    if(databaseConnection.isDataMissing()) {
      System.out.println("Error: Database connection failed.");
      System.out.println("Shutting down...");

      return;
    }


    DatabaseAccess databaseAccess = new DatabaseAccess(databaseConnection);
    databaseAccess.startUserMenu();
  }

  private static int getSelectedIndex() {
    Scanner scanner = new Scanner(new ShieldedInputStream(System.in));

    int i = 0;
    while(i == 0) {
      switch(scanner.nextLine()) {
        case "1" -> i = 1;
        case "2" -> i = 2;
        case "3" -> i = 3;
        default -> System.out.println("Please enter a number between 1 and 3.");
      }
    }

    scanner.close();


    return i;
  }
}
