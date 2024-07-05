package repository;

import model.Tasks;

import java.sql.SQLException;
import java.util.List;

public interface TasksRepository {

    Tasks addTask(Tasks task, long employeeId) throws SQLException;

    Tasks updateTask(Tasks task, long taskId) throws SQLException;

    void deleteTask(long taskId) throws SQLException;

    Tasks getTaskById(long taskId) throws SQLException;

    List<Tasks> getTasksByEmployeeId(long employeeId) throws SQLException;

}
