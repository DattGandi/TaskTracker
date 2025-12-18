package com.tracker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProperties {
  private String host = null;
  private String port = null;
  private String username = null;
  private String password = null;
  private String database = null;

  public ConnectionProperties() {
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public Connection getConnection() throws SQLException {
    String url = "jdbc:postgresql://" + host + ":" + port + "/";
    if(database != null) {
      url += database;
    }

    Properties properties = new Properties();
    properties.setProperty("user", username);
    properties.setProperty("password", password);

    return DriverManager.getConnection(url, properties);
  }
}
