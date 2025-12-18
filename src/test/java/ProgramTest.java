import com.tracker.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProgramTest {
  @BeforeEach
  void setup() {

  }

  @Test
  @DisplayName("Connect to database")
  void testConnectToDatabase() {
    String simulatedUserInput = "\n"; //server name, default: localhost
    simulatedUserInput += "4185\n"; //port, default: 5432
    simulatedUserInput += "postgres\n"; //username
    simulatedUserInput += "password\n"; //password
    simulatedUserInput += "test\n"; //database name

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);


    Connection con = DatabaseConnection.connectToDatabase(stream);

    assertNotNull(con);
  }

  @Test
  @DisplayName("Add new task")
  void testAddNewTask() {

  }

  @Test
  @DisplayName("Show tasks")
  void testShowTasks() {

  }

  @Test
  @DisplayName("Edit task")
  void testEditTask() {

  }

  @Test
  @DisplayName("Delete task")
  void testDeleteTask() {
    
  }
}
