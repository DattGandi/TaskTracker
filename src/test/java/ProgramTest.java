import com.tracker.ConnectionData;
import com.tracker.DatabaseAccess;
import com.tracker.DatabaseConnection;
import com.tracker.TaskAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ProgramTest {
  @Test
  @DisplayName("Connect to database")
  void testConnectToDatabase() {
    ConnectionData con = getDatabaseConnection();

    assertNotNull(con);
    assertFalse(con.isDataMissing());
  }

  private ConnectionData getDatabaseConnection() {
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
  @DisplayName("Show tasks")
  void testShowTasks() {
    ConnectionData con = getDatabaseConnection();
    if(con.isDataMissing()) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }

    DatabaseAccess access = new DatabaseAccess(con);
    assertTrue(access.showTasks());
  }

  @Test
  @DisplayName("Add new task")
  void testAddNewTask() {
    ConnectionData con = getDatabaseConnection();
    if(con.isDataMissing()) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }


    DatabaseAccess access = new DatabaseAccess(con);

    String taskName = "task1";
    deletePotentialCreatedTask(access, taskName);

    String simulatedUserInput = taskName + "\n"; //name of the task
    simulatedUserInput += "description1\n"; //description of the task
    simulatedUserInput += "status1\n"; //status of the task

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

    try {
      assertTrue(access.addTask(stream));
    }
    catch(TaskAlreadyExistsException e) {
      fail("The specified task 'task1' already existed. Task cleanup with 'deletePotentialCreatedTask' was not successful. See 'testDeleteTask'.");
    }
  }

  @Test
  @DisplayName("Edit task")
  void testEditTask() {
    ConnectionData con = getDatabaseConnection();
    if(con.isDataMissing()) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }


    DatabaseAccess access = new DatabaseAccess(con);

    String taskName = "task2";
    deletePotentialCreatedTask(access, "NEW" + taskName);

    String simulatedUserInput = taskName + "\n"; //name of the task
    simulatedUserInput += "description2\n"; //description of the task
    simulatedUserInput += "status2\n"; //status of the task

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

    try {
      if(!access.addTask(stream)) {
        fail("Failed to add task. See 'testAddNewTask'.");
      }
    }
    catch(TaskAlreadyExistsException e) {
      fail("The specified task 'task2' already existed. Task cleanup with 'deletePotentialCreatedTask' was not successful. See 'testDeleteTask'.");
    }


    String simulatedUserInput2 = taskName + "\n"; //name of the task
    simulatedUserInput2 += "\n"; //select all options
    simulatedUserInput2 += "NEW" + taskName + "\n"; //new name
    simulatedUserInput2 += "NEWdescription2\n"; //new description
    simulatedUserInput2 += "NEWstatus2\n"; //new status

    byte[] bytes2 = simulatedUserInput2.getBytes();
    ByteArrayInputStream stream2 = new ByteArrayInputStream(bytes2);

    assertTrue(access.editTask(stream2));
  }

  @Test
  @DisplayName("Delete task")
  void testDeleteTask() {
    ConnectionData con = getDatabaseConnection();
    if(con.isDataMissing()) {
      fail("Failed to establish database connection. See 'testConnectToDatabase'.");
    }


    DatabaseAccess access = new DatabaseAccess(con);

    String taskName = "task3";
    deletePotentialCreatedTask(access, taskName);

    String simulatedUserInput = taskName + "\n"; //name of the task
    simulatedUserInput += "description3\n"; //description of the task
    simulatedUserInput += "status3\n"; //status of the task

    byte[] bytes = simulatedUserInput.getBytes();
    ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

    try {
      if(!access.addTask(stream)) {
        fail("Failed to add task. See 'testAddNewTask'.");
      }
    }
    catch(TaskAlreadyExistsException e) {
      fail("The specified task 'task3' already existed. Task cleanup with 'deletePotentialCreatedTask' was not successful. See 'testDeleteTask'.");
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
