package service;

import model.Tasks;
import model.dto.TasksDto;

import java.sql.SQLException;
import java.util.List;

public interface TasksService {

    TasksDto addTasks(TasksDto tasksDto, long id) throws SQLException;

    TasksDto updateTasks(TasksDto tasksDto, long id) throws SQLException;

    void deleteTasks(long id) throws SQLException;

    List<TasksDto> getTasksByEmployeeId(long id) throws SQLException;
}
