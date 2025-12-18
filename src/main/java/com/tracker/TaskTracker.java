package com.tracker;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class TaskTracker {
  public static void main(String[] args) {
    System.out.println("Welcome to TaskTracker!");
    System.out.println("1. Connect to existing Database");
    System.out.println("2. Create new Database");
    System.out.println("3. Exit");

    Scanner scanner = new Scanner(new ShieldedInputStream());

    int i = 0;
    while(i == 0) {
      switch(scanner.nextLine()) {
        case "1": {
          i = 1;
          break;
        }

        case "2": {
          i = 2;
          break;
        }

        case "3": {
          i = 3;
          break;
        }

        default: {
          System.out.println("Please enter a number between 1 and 3.");
          break;
        }
      }
    }

    scanner.close();


    Connection databaseConnection;
    if(i == 1) {
      databaseConnection = DatabaseConnection.connectToDatabase(new ShieldedInputStream());
    }
    else if(i == 2) {
      databaseConnection = DatabaseConnection.createDatabase(new ShieldedInputStream());
    }
    else {
      System.out.println("Shutting down...");
      return;
    }

    if(databaseConnection == null) {
      System.out.println("Error: Database connection failed.");
      System.out.println("Shutting down...");

      return;
    }


    DatabaseAccess databaseAccess = new DatabaseAccess(databaseConnection);
    databaseAccess.startUserMenu();
  }
}
