package com.tracker;

import java.sql.Connection;

public class DatabaseAccess {
  Connection connection;

  public DatabaseAccess(Connection connection) {
    this.connection = connection;
  }

  public void startUserMenu() {
    System.out.println("DEBUG: started menu");
    //TODO: use while loop until user ends program, make it possible for user to modify database
  }
}
