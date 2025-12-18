package org.example;

import java.sql.*;
import java.util.Properties;


public class Main {
  public static void main(String[] args) {
    Properties props = new Properties();
    props.setProperty("user", "postgres");
    props.setProperty("password", "password");

    try {
      Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:4185/postgres", props);

      Statement st1 = con.createStatement();

      String sqlQuery = "CREATE TABLE IF NOT EXISTS test (" +
              "id INT NOT NULL PRIMARY KEY," +
              "name VARCHAR(255)" +
              ");";
      ResultSet rs = st1.executeQuery(sqlQuery);

      st1.close();
      con.close();
    }
    catch(SQLException e) {
      System.out.println("Error while trying to establish connection.\nShutting down...");

      System.out.println(e.getSQLState());
      e.printStackTrace();

      return;
    }
  }
}
