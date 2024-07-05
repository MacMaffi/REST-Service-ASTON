package repository.impl;

import model.Tasks;
import repository.TasksRepository;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TasksRepositoryImp implements TasksRepository {

    private static final TasksRepositoryImp INSTANCE = new TasksRepositoryImp();

    private TasksRepositoryImp() {}

    private static final String ADD_TASK = """
            INSERT INTO tasks(title, status, employee_id) VALUES (?,?,?)
            """;

    private static final String UPDATE_TASK = """
            UPDATE tasks SET title = ?, status = ? WHERE id = ?
            """;

    private static final String DELETE_TASK = """
            DELETE FROM tasks WHERE id = ?
            """;

    private static final String GET_TASK_BY_ID = """
            SELECT * FROM tasks WHERE id = ?
            """;

    private static final String GET_TASKS_BY_EMPLOYEE_ID = """
            SELECT * FROM tasks WHERE employee_id = ?
            """;

    @Override
    public Tasks addTask(Tasks task, long employeeId) throws SQLException {
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(ADD_TASK,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getStatus());
            ps.setLong(3, employeeId);

            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("Task not added");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()){
                task.setId(rs.getLong("id"));
            }
        }
        return task;
    }

    @Override
    public Tasks updateTask(Tasks task, long taskId) throws SQLException {
        Tasks updateTask = null;
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(UPDATE_TASK)) {
            ps.setString(1, task.getTitle());
            ps.setString(2, task.getStatus());
            ps.setLong(3, taskId);
            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("Task not updated");
            }

            var ps1 = connection.prepareStatement("SELECT * FROM tasks WHERE id = ?");
            ps1.setLong(1, taskId);
            try (var resultSet = ps1.executeQuery()) {
                if (resultSet.next()) {
                    return buildTasks(resultSet);
                }
            }
            ps1.close();
        }
        updateTask.setEmployee(task.getEmployee());
        return updateTask;
    }

    @Override
    public void deleteTask(long taskId) throws SQLException {
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(DELETE_TASK)) {
            ps.setLong(1, taskId);
            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("Task not deleted");
            }
        }
    }

    @Override
    public Tasks getTaskById(long taskId) throws SQLException {
        Tasks task = null;
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(GET_TASK_BY_ID)) {
            ps.setLong(1, taskId);
            try (var resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    task = buildTasks(resultSet);
                }
            }
        }
        if(task == null) {
            throw new SQLException("Task not found");
        } else {
            return task;
        }
    }

    @Override
    public List<Tasks> getTasksByEmployeeId(long employeeId) throws SQLException {
        List<Tasks> tasks = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(GET_TASKS_BY_EMPLOYEE_ID)) {
            ps.setLong(1, employeeId);
            try (var resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    tasks.add(buildTasks(resultSet));
                }
            }
        }
        if (tasks.isEmpty()) {
            throw new SQLException("Tasks not found");
        } else {
            return tasks;
        }
    }

    public static TasksRepositoryImp getInstance() {
        return INSTANCE;
    }

    private Tasks buildTasks(ResultSet rs) throws SQLException {
        return new Tasks(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("status")
        );
    }
}
