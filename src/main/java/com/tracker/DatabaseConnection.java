package com.tracker;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {
  public static ConnectionData connectToDatabase(InputStream stream) {
    System.out.println("- Connect to Database -");

    ConnectionData databaseConnection = new ConnectionData();
    ConnectionProperties properties = new ConnectionProperties();


    Scanner scanner = new Scanner(stream);

    getHostConnection(scanner, properties);

    System.out.println("Input the database name:");
    String database = scanner.nextLine();
    properties.setDatabase(database);

    scanner.close();


    try {
      System.out.println("Connecting to database " + database + "...");

      databaseConnection.setConnection(properties.getConnection());

      System.out.println("Successful");
    }
    catch(SQLException e) {
      System.out.println("Failed");

      System.out.println("Error: Cannot connect to database.");
      System.out.println("Message: " + e.getMessage());
    }


    if(!assertTaskTableExists(databaseConnection)) {
      databaseConnection.reset();
    }


    return databaseConnection;
  }

  public static ConnectionData createDatabase(InputStream stream) {
    System.out.println("- Create new Database -");

    ConnectionData databaseConnection = new ConnectionData();
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
    }
    catch(SQLException e) {
      System.out.println("Failed");

      System.out.println("Error: Creation of database failed.");
      System.out.println("Message: " + e.getMessage());
    }

    try {
      System.out.println("Connecting to database " + database + "...");

      properties.setDatabase(database);
      databaseConnection.setConnection(properties.getConnection());

      System.out.println("Successful");
    }
    catch(SQLException e) {
      System.out.println("Failed");

      System.out.println("Error: Cannot connect to database.");
      System.out.println("Message: " + e.getMessage());

      System.out.println("Trying to delete the created database...");

      try {
        properties.setDatabase(null);

        Connection con = properties.getConnection();
        Statement dropDatabase = con.createStatement();

        dropDatabase.execute("DROP TABLE " + database + ";");
        dropDatabase.close();

        System.out.println("Successful");
      }
      catch(SQLException ex) {
        System.out.println("Failed");

        System.out.println("Error: Could not delete database " + database + ".");
        System.out.println("Message: " + e.getMessage());
      }
    }


    if(databaseConnection.getConnection() != null) {
      createTaskTable(databaseConnection);
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

  private static boolean assertTaskTableExists(ConnectionData connectionData) {
    if(connectionData.getConnection() == null) {
      return false;
    }


    boolean taskTableFound = false;

    int counter = 0;
    while(!taskTableFound && counter < 10) {
      try {
        Statement getTable = connectionData.getConnection().createStatement();
        String sCounter = counter == 0 ? "" : Integer.toString(counter);
        getTable.execute("SELECT * FROM tasks" + sCounter + ";");

        ResultSetMetaData metaData = getTable.getResultSet().getMetaData();
        if(metaData.getColumnCount() == 3
                && metaData.getColumnTypeName(1).equals("varchar")
                && metaData.getColumnTypeName(2).equals("varchar")
                && metaData.getColumnTypeName(3).equals("varchar")) {
            taskTableFound = true;

            connectionData.setTaskTable("tasks" + sCounter);
            connectionData.setNameColumn(metaData.getColumnLabel(1));
            connectionData.setDescriptionColumn(metaData.getColumnLabel(2));
            connectionData.setStatusColumn(metaData.getColumnLabel(3));
        }

        getTable.close();
      }
      catch(SQLException e) {
        //left empty
      }

      counter++;
    }

    if(!taskTableFound) {
      System.out.println("Could not find a task table.");

      taskTableFound = createTaskTable(connectionData);
    }


    return taskTableFound;
  }

  private static boolean createTaskTable(ConnectionData connectionData) {
    System.out.println("Creating task table...");


    boolean result = false;

    int counter = 0;
    try {
      while(counter < 10) {
        String sCounter = counter == 0 ? "" : Integer.toString(counter);

        Statement getTableX = connectionData.getConnection().createStatement();
        getTableX.execute("SELECT * FROM tasks" + sCounter + ";");
        getTableX.close();

        counter++;
      }
    }
    catch(SQLException e) {
      String tableName = "tasks" + (counter == 0 ? "" : Integer.toString(counter));
      String nameColumn = "name";
      String descriptionColumn = "description";
      String statusColumn = "status";

      try {
        Statement createTable = connectionData.getConnection().createStatement();
        createTable.execute("CREATE TABLE " + tableName + " (name VARCHAR(255) PRIMARY KEY, description VARCHAR(255), status VARCHAR(255));");
        createTable.close();

        result = true;
      }
      catch(SQLException ex) {
        System.out.println("Error: Task table could not be created.");
      }
    }


    if(result) {
      System.out.println("Successful");

      String sCounter = counter == 0 ? "" : Integer.toString(counter);
      String tableName = "tasks" + sCounter;

      connectionData.setTaskTable(tableName);
      connectionData.setNameColumn("name");
      connectionData.setDescriptionColumn("description");
      connectionData.setStatusColumn("completed");

      System.out.println("Created task table under the name '" + tableName + "'.");
    }
    else {
      System.out.println("Failed");
    }


    return result;
  }
}
