package com.tracker;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class DatabaseAccess {
  private ConnectionData connectionData;

  public DatabaseAccess(ConnectionData connectionData) throws IllegalArgumentException {
    if(connectionData == null || connectionData.isDataMissing()) {
      throw new IllegalArgumentException("Connection data must not be null.");
    }

    this.connectionData = connectionData;
  }

  public void startUserMenu() {
    int selectedIndex;
    do {
      System.out.println("- Choose action -");
      System.out.println("1. Show tasks");
      System.out.println("2. Add task");
      System.out.println("3. Edit task");
      System.out.println("4. Delete task");
      System.out.println("5. Exit");

      selectedIndex = getSelectedIndex();

      switch(selectedIndex) {
        case 1 -> {
          if(!showTasks()) {
            System.out.println("Error: Tasks could not be displayed.");
          }
        }
        case 2 -> {
          try {
            if(!addTask(new ShieldedInputStream(System.in))) {
              System.out.println("Error: Task could not be added.");
            }
          }
          catch(TaskAlreadyExistsException e) {
            System.out.println("A task already exists with this name.");
          }
        }
        case 3 -> {
          if(!editTask(new ShieldedInputStream(System.in))) {
            System.out.println("Error: Task could not be edited.");
          }
        }
        case 4 -> {
          if(!deleteTask(new ShieldedInputStream(System.in))) {
            System.out.println("Error: Task could not be deleted.");
          }
        }
      }

      if(selectedIndex != 5) {
        forceUserInput();
      }
    } while(selectedIndex != 5);


    System.out.println("Closing resources...");

    connectionData.reset();

    System.out.println("Shutting down...");
  }

  private int getSelectedIndex() {
    Scanner scanner = new Scanner(new ShieldedInputStream(System.in));

    int i = 0;
    while(i == 0) {
      switch(scanner.nextLine()) {
        case "1" -> i = 1;
        case "2" -> i = 2;
        case "3" -> i = 3;
        case "4" -> i = 4;
        case "5" -> i = 5;
        case "" -> {
          //do nothing
        }
        default -> System.out.println("Please enter a number between 1 and 5.");
      }
    }

    scanner.close();


    return i;
  }

  private void forceUserInput() {
    System.out.println("Press Enter to continue.");

    Scanner scanner = new Scanner(new ShieldedInputStream(System.in));
    scanner.nextLine();
    scanner.close();
  }

  public boolean showTasks() {
    boolean tasksDisplayed = false;

    System.out.println("- Show Tasks -");

    List<String> nameEntries = new ArrayList<String>();
    List<String> descriptionEntries = new ArrayList<String>();
    List<String> statusEntries = new ArrayList<String>();

    try {
      Connection con = connectionData.getConnection();

      Statement showTasks = con.createStatement();
      showTasks.execute("SELECT * FROM " + connectionData.getTaskTable() + ";");
      ResultSet result = showTasks.getResultSet();

      while(result.next()) {
        nameEntries.add(String.valueOf(result.getArray(1)));
        descriptionEntries.add(String.valueOf(result.getArray(2)));
        statusEntries.add(String.valueOf(result.getArray(3)));
      }

      showTasks.close();
    }
    catch(SQLException e) {
      //left empty
    }


    if(nameEntries.isEmpty()) {
      System.out.println("The table does not have any entries yet.");

      tasksDisplayed = true;
    }
    else {
      tasksDisplayed = displayFormatedData(nameEntries, descriptionEntries, statusEntries);
    }


    return tasksDisplayed;
  }

  private boolean displayFormatedData(List<String> nameEntries,
                                   List<String> descriptionEntries,
                                   List<String> statusEntries) {
    int nameSize;
    int descriptionSize;
    int statusSize;
    try {
      nameSize = getLongestString(nameEntries);
      descriptionSize = getLongestString(descriptionEntries);
      statusSize = getLongestString(statusEntries);
    }
    catch(NoSuchElementException e) {
      return false;
    }


    String name = connectionData.getNameColumn();
    String description = connectionData.getDescriptionColumn();
    String status = connectionData.getStatusColumn();

    nameSize = Integer.max(nameSize, name.length());
    descriptionSize = Integer.max(descriptionSize, description.length());
    statusSize = Integer.max(statusSize, status.length());

    String delimiter = "-".repeat(nameSize + descriptionSize + statusSize + 10);


    System.out.println(delimiter);

    name = name.toUpperCase() + " ".repeat(nameSize - name.length());
    description = description.toUpperCase() + " ".repeat(descriptionSize - description.length());
    status = status.toUpperCase() + " ".repeat(statusSize - status.length());

    System.out.println("| " + name + " | " + description + " | " + status + " |");

    System.out.println(delimiter);


    for(int x = 0; x < nameEntries.size(); x++) {
      String currentName = nameEntries.get(x);
      String currentDescription = descriptionEntries.get(x);
      String currentStatus = statusEntries.get(x);

      System.out.print("| ");
      System.out.print(currentName + " ".repeat(nameSize - currentName.length()));
      System.out.print(" | ");
      System.out.print(currentDescription + " ".repeat(descriptionSize - currentDescription.length()));
      System.out.print(" | ");
      System.out.print(currentStatus + " ".repeat(statusSize - currentStatus.length()));
      System.out.print(" |\n");
    }

    System.out.println(delimiter);


    return true;
  }

  private int getLongestString(List<String> entries) throws NoSuchElementException {
    return entries.stream().map(String::length).max(Integer::compareTo).orElseThrow();
  }

  public boolean addTask(InputStream stream) throws TaskAlreadyExistsException {
    boolean taskAdded = false;

    System.out.println("- Add Task -");

    Scanner scanner = new Scanner(stream);
    Connection con = connectionData.getConnection();


    String taskName = getTaskInput(scanner, "name");

    if(hasTask(taskName)) {
      scanner.close();

      throw new TaskAlreadyExistsException("The task '" + taskName + "' already exists.");
    }


    String taskDescription = getTaskInput(scanner, "description");
    String taskStatus = getTaskInput(scanner, "status");

    scanner.close();

    try {
      Statement addTask = con.createStatement();
      addTask.execute("INSERT INTO " + connectionData.getTaskTable() + " ("
              + connectionData.getNameColumn() + ", "
              + connectionData.getDescriptionColumn() + ", "
              + connectionData.getStatusColumn()
              + ") VALUES ("
              + "'" + taskName + "', "
              + "'" + taskDescription + "', "
              + "'" + taskStatus + "');");
      addTask.close();

      taskAdded = true;
    }
    catch(SQLException e) {
      //left empty
      String s = e.getMessage();
    }


    if(taskAdded) {
      System.out.println("The task '" + taskName + "' was successfully added.");
    }


    return taskAdded;
  }

  private String getTaskInput(Scanner scanner, String descriptor) {
    System.out.println("Input the " + descriptor + " of the task:");

    String input = null;

    while(input == null) {
      String currentLine = scanner.nextLine();

      if(currentLine.isEmpty()) {
        System.out.println("The " + descriptor + " must not be empty.");
      }
      else if(!currentLine.matches("[a-zA-Z0-9]+")) {
        System.out.println("The " + descriptor + " must only consist of letters and numbers.");
      }
      else {
        input = currentLine;
      }
    }


    return input;
  }

  public boolean editTask(InputStream stream) {
    boolean taskEdited = false;

    System.out.println("- Edit Task -");


    Scanner scanner = new Scanner(stream);
    String taskName = getTaskInput(scanner, "name");

    if(!hasTask(taskName)) {
      System.out.println("The task '" + taskName + "' does not exist.");
    }
    else {
      List<String> changeColumns = getChangeColumns(scanner);

      String newName = null;
      if(changeColumns.contains(connectionData.getNameColumn())) {
        newName = getTaskInput(scanner, "new name");
      }

      String newDescription = null;
      if(changeColumns.contains(connectionData.getDescriptionColumn())) {
        newDescription = getTaskInput(scanner, "new description");
      }

      String newStatus = null;
      if(changeColumns.contains(connectionData.getStatusColumn())) {
        newStatus = getTaskInput(scanner, "new status");
      }


      try {
        Connection con = connectionData.getConnection();

        Statement editTask = con.createStatement();


        boolean firstCol = true;
        String sqlCommand = "UPDATE " + connectionData.getTaskTable() + " SET ";

        if(newName != null) {
          firstCol = false;

          sqlCommand += connectionData.getNameColumn() + " = '" + newName + "'";
        }

        if(newDescription != null) {
          if(firstCol) {
            firstCol = false;
          }
          else {
            sqlCommand += ", ";
          }

          sqlCommand += connectionData.getDescriptionColumn() + " = '" + newDescription + "'";
        }

        if(newStatus != null) {
          if(!firstCol) {
            sqlCommand += ", ";
          }

          sqlCommand += connectionData.getStatusColumn() + " = '" + newStatus + "'";
        }

        sqlCommand += " WHERE " + connectionData.getNameColumn() + " = '" + taskName + "';";


        editTask.execute(sqlCommand);
        editTask.close();

        taskEdited = true;
      }
      catch(SQLException e) {
        //left empty
      }
    }

    scanner.close();


    if(taskEdited) {
      System.out.println("The task was successfully edited.");
    }


    return taskEdited;
  }

  private List<String> getChangeColumns(Scanner scanner) {
    System.out.println("Select the entries to change.");
    System.out.println("Multiple entries can be changed at the same time.");
    System.out.println("Separate multiple entries with ','.");
    System.out.println("To change all entries press Enter.");

    String nameColumn = connectionData.getNameColumn();
    String descriptionColumn = connectionData.getDescriptionColumn();
    String statusColumn = connectionData.getStatusColumn();
    System.out.println("Available: "
            + nameColumn + ", "
            + descriptionColumn + ", "
            + statusColumn);


    String input = null;
    while(input == null) {
      String currentLine = scanner.nextLine();

      if(currentLine.isEmpty()) {
        input = currentLine;
      }
      else {
        String[] strings = currentLine.split(",");

        boolean allStringsValid = true;
        for (String entry : strings) {
          String s = entry.trim();

          if (!s.equals(nameColumn) && !s.equals(descriptionColumn) && !s.equals(statusColumn)) {
            System.out.println("Inputs must match with the available columns: '" + s + "'.");

            allStringsValid = false;
          }
        }

        if (allStringsValid) {
          input = currentLine;
        }
      }
    }


    List<String> changeColumns = new ArrayList<String>();

    if(input.isEmpty()) {
      changeColumns.add(nameColumn);
      changeColumns.add(descriptionColumn);
      changeColumns.add(statusColumn);
    }
    else {
      String[] entries = input.split(",");
      for(int x = 0; x < entries.length; x++) {
        entries[x] = entries[x].trim();
      }

      changeColumns.addAll(List.of(entries));
    }


    return changeColumns;
  }

  public boolean deleteTask(InputStream stream) {
    boolean taskDeleted = false;

    System.out.println("- Delete Task -");


    Scanner scanner = new Scanner(stream);
    String taskName = getTaskInput(scanner, "name");
    scanner.close();

    if(!hasTask(taskName)) {
      System.out.println("The task '" + taskName + "' does not exist.");
    }
    else {
      try {
        Connection con = connectionData.getConnection();

        Statement deleteTask = con.createStatement();
        deleteTask.execute("DELETE FROM " + connectionData.getTaskTable()
                + " WHERE " + connectionData.getNameColumn() + " = '" + taskName + "';");
        deleteTask.close();

        taskDeleted = true;
      }
      catch(SQLException e) {
        //left empty
      }
    }


    if(taskDeleted) {
      System.out.println("The task was successfully deleted.");
    }


    return taskDeleted;
  }

  private boolean hasTask(String taskName) {
    boolean hasTask = false;

    try {
      Statement getTask = connectionData.getConnection().createStatement();
      getTask.execute("SELECT " + connectionData.getNameColumn()
              + " FROM " + connectionData.getTaskTable()
              + " WHERE " + connectionData.getNameColumn() + " = '" + taskName + "';");
      ResultSet result = getTask.getResultSet();

      hasTask = result.next();
      getTask.close();
    }
    catch(SQLException e) {
      //left empty
    }


    return hasTask;
  }
}
