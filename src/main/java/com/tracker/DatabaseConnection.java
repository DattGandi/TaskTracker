package com.tracker;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseConnection {
  public static Connection connectToDatabase(InputStream stream) {
    System.out.println("- Connect to Database -");

    Connection databaseConnection = null;
    ConnectionProperties properties = new ConnectionProperties();


    Scanner scanner = new Scanner(stream);

    getHostConnection(scanner, properties);

    System.out.println("Input the database name:");
    String database = scanner.nextLine();
    properties.setDatabase(database);

    scanner.close();


    try {
      System.out.println("Connecting to database " + database + "...");

      databaseConnection = properties.getConnection();

      System.out.println("Successful");
    } catch (SQLException e) {
      System.out.println("Failed");

      System.out.println("Error: Cannot connect to database.");
      System.out.println("Message: " + e.getMessage());
    }


    return databaseConnection;
  }

  public static Connection createDatabase(InputStream stream) {
    System.out.println("- Create new Database -");

    Connection databaseConnection = null;
    ConnectionProperties properties = new ConnectionProperties();


    Scanner scanner = new Scanner(stream);

    getHostConnection(scanner, properties);

    System.out.println("Input the name of the new database:");
    String database = confirmInputNotEmpty(scanner);

    scanner.close();


    try {
      System.out.println("Creating database " + database + "...");

      Connection con = properties.getConnection();
      Statement createDatabase = con.createStatement();

      createDatabase.execute("CREATE DATABASE " + database + ";");
      createDatabase.close();

      System.out.println("Successful");
    } catch (SQLException e) {
      System.out.println("Failed");

      System.out.println("Error: Creation of database failed.");
      System.out.println("Message: " + e.getMessage());
    }

    try {
      System.out.println("Connecting to database " + database + "...");

      properties.setDatabase(database);
      databaseConnection = properties.getConnection();

      System.out.println("Successful");
    } catch (SQLException e) {
      System.out.println("Failed");

      System.out.println("Error: Cannot connect to database.");
      System.out.println("Message: " + e.getMessage());

      System.out.println("Trying to delete the created database...");

      try {
        properties.setDatabase(null);

        Connection con = properties.getConnection();
        Statement dropDatabase = con.createStatement();

        dropDatabase.execute("DROP TABLE " + database + ";");

        System.out.println("Successful");
      } catch (SQLException ex) {
        System.out.println("Failed");

        System.out.println("Error: Could not delete database " + database + ".");
        System.out.println("Message: " + e.getMessage());
      }
    }


    return databaseConnection;
  }

  private static void getHostConnection(Scanner scanner, ConnectionProperties properties) {
    System.out.println("Input host server:\nPress enter to use localhost.");
    properties.setHost(getOrDefault(scanner, "localhost"));

    System.out.println("Input port:\nPress enter to use the default port (5432).");
    properties.setPort(confirmInputMatchesRegexDefault(
            scanner,
            "[0-9]+",
            "The port must only contain digits.",
            "5432"));

    System.out.println("Input username:");
    properties.setUsername(scanner.nextLine());

    System.out.println("Input password:");
    properties.setPassword(scanner.nextLine());
  }

  private static String getOrDefault(Scanner scanner, String defaultStr) {
    String result = scanner.nextLine();

    if(result.isEmpty()) {
      result = defaultStr;
    }


    return result;
  }

  private static String confirmInputMatchesRegexDefault(Scanner scanner,
                                                        String regex,
                                                        String description,
                                                        String defaultStr) {
    String result = null;

    while(result == null) {
      String currentLine = scanner.nextLine();

      if(currentLine.isEmpty()) {
        result = defaultStr;
      }
      else if(currentLine.matches(regex)) {
        result = currentLine;
      }
      else {
        System.out.println(description);
      }
    }


    return result;
  }

  private static String confirmInputNotEmpty(Scanner scanner) {
    String result = null;
    while(result == null) {
      String currentLine = scanner.nextLine();

      if(!currentLine.isEmpty()) {
        result = currentLine;
      }
      else {
        System.out.println("Input must not be empty.");
      }
    }

    return result;
  }
}
