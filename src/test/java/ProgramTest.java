import com.tracker.DatabaseAccess;
import com.tracker.DatabaseConnection;
import com.tracker.TaskAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramTest {
  @BeforeEach
  void setup() {

  }

  @Test
  @DisplayName("Connect to database")
  void testConnectToDatabase() {
    Connection con = getDatabaseConnection();

    assertNotNull(con);
  }

  private Connection getDatabaseConnection() {
    String simulatedUserInput = "\n"; //server name, default: 'localhost'
    simulatedUserInput += "4185\n"; //port, default: '5432'
    simulatedUserInput += "postgres\n"; //username
    simulatedUserInput += "password\n"; //password
    simulatedUserInput += "test\n"; //database name

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);


    return DatabaseConnection.connectToDatabase(stream);
  }

  @Test
  @DisplayName("Add new task")
  void testAddNewTask() {
    Connection con = getDatabaseConnection();
    if(con == null) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }


    DatabaseAccess access = new DatabaseAccess(con);

    String taskName = "task1";
    deletePotentialCreatedTask(access, taskName);

    String simulatedUserInput = taskName + "\n"; //name of the task
    simulatedUserInput += "\n"; //description of the task, default: empty

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

    try {
      assertTrue(access.addTask(stream));
    }
    catch(TaskAlreadyExistsException e) {
      fail("The specified task 'task1' already existed. Task cleanup with 'deletePotentialCreatedTask' was not successful.");
    }
  }

  @Test
  @DisplayName("Show tasks")
  void testShowTasks() {
    Connection con = getDatabaseConnection();
    if(con == null) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }

    DatabaseAccess access = new DatabaseAccess(con);
    assertTrue(access.showTasks());
  }

  @Test
  @DisplayName("Edit task")
  void testEditTask() {
    Connection con = getDatabaseConnection();
    if(con == null) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }


    DatabaseAccess access = new DatabaseAccess(con);

    String taskName = "task2";
    deletePotentialCreatedTask(access, taskName);

    String simulatedUserInput = taskName + "\n"; //name of the task
    simulatedUserInput += "\n"; //description of the task, default: empty

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

    try {
      if(!access.addTask(stream)) {
        fail("Failed to add task. See 'testAddNewTask'.");
      }
    }
    catch(TaskAlreadyExistsException e) {
      fail("The specified task 'task2' already existed. Task cleanup with 'deletePotentialCreatedTask' was not successful.");
    }


    String simulatedUserInput2 = taskName + "\n"; //name of the task

    byte[] bytes2 = simulatedUserInput2.getBytes();
    ByteArrayInputStream stream2 = new ByteArrayInputStream(bytes2);

    assertTrue(access.deleteTask(stream2));
  }

  @Test
  @DisplayName("Delete task")
  void testDeleteTask() {
    Connection con = getDatabaseConnection();
    if(con == null) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }


    DatabaseAccess access = new DatabaseAccess(con);

    String taskName = "task3";
    deletePotentialCreatedTask(access, taskName);

    String simulatedUserInput = taskName + "\n"; //name of the task
    simulatedUserInput += "\n"; //description of the task, default: empty

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

    try {
      if(!access.addTask(stream)) {
        fail("Failed to add task. See 'testAddNewTask'.");
      }
    }
    catch(TaskAlreadyExistsException e) {
      fail("The specified task 'task3' already existed. Task cleanup with 'deletePotentialCreatedTask' was not successful.");
    }


    String simulatedUserInput2 = taskName + "\n"; //name of the task

    byte[] bytes2 = simulatedUserInput2.getBytes();
    ByteArrayInputStream stream2 = new ByteArrayInputStream(bytes2);

    assertTrue(access.deleteTask(stream2));
  }

  private void deletePotentialCreatedTask(DatabaseAccess access, String taskName) {
    access.deleteTask(new ByteArrayInputStream((taskName + "\n").getBytes()));
  }
}
