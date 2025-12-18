package com.tracker;

import java.io.InputStream;
import java.sql.Connection;

public class DatabaseAccess {
  private Connection connection;

  public DatabaseAccess(Connection connection) throws IllegalArgumentException {
    if(connection == null) {
      throw new IllegalArgumentException("Connection must not be null.");
    }

    this.connection = connection;
  }

  public void startUserMenu() {
    System.out.println("DEBUG: started menu");
    //TODO: use while loop until user ends program, make it possible for user to modify database
  }

  public boolean addTask(InputStream stream) throws TaskAlreadyExistsException {
    return false;
  }

  public boolean showTasks() {
    return false;
  }

  public boolean editTask(InputStream stream) {
    return false;
  }

  public boolean deleteTask(InputStream stream) {
    return false;
  }
}
