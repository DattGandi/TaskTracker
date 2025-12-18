package com.tracker;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionData {
  private Connection connection;

  private String taskTable;
  private String nameColumn;
  private String descriptionColumn;
  private String statusColumn;


  public ConnectionData() {
    connection = null;
    taskTable = null;
    nameColumn = null;
    descriptionColumn = null;
    statusColumn = null;
  }


  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  public void setTaskTable(String taskTable) {
    this.taskTable = taskTable;
  }

  public void setNameColumn(String nameColumn) {
    this.nameColumn = nameColumn;
  }

  public void setDescriptionColumn(String descriptionColumn) {
    this.descriptionColumn = descriptionColumn;
  }

  public void setStatusColumn(String statusColumn) {
    this.statusColumn = statusColumn;
  }

  public Connection getConnection() {
    return connection;
  }

  public String getTaskTable() {
    return taskTable;
  }

  public String getNameColumn() {
    return nameColumn;
  }

  public String getDescriptionColumn() {
    return descriptionColumn;
  }

  public String getStatusColumn() {
    return statusColumn;
  }

  public void reset() {
    if(connection != null) {
      try {
        connection.close();
      }
      catch(SQLException e) {
        System.out.println("Error: Failed to close database connection.");
      }
    }

    connection = null;
    taskTable = null;
    nameColumn = null;
    descriptionColumn = null;
    statusColumn = null;
  }

  public boolean isDataMissing() {
    return connection == null || taskTable == null || statusColumn == null;
  }
}
