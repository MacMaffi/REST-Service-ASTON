package testservice;

import model.Employee;
import model.Tasks;
import model.dto.TasksDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.EmployeeRepository;
import repository.TasksRepository;
import service.impl.TaskServiceImpl;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TasksServiceTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TasksRepository taskRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private Tasks tasks;
    private Tasks nextTask;
    private TasksDto taskDto;
    private TasksDto addTask;
    private Employee employee;
    private Long employeeId;
    private Long taskId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new Employee(1L, "Иван", "Петров", "Разработчик");
        tasks = new Tasks(1L, "Задача", "На рассмотрении");
        nextTask = new Tasks(2L, "Задача", "На рассмотрении");
        taskDto = new TasksDto("Иван", "Петров", "Задача", "На рассмотрении");
        addTask = new TasksDto("Иван", "Петров", "Задача", "На рассмотрении");
        employeeId = 1L;
        taskId = 1L;
    }

    @Test
    void addTask_whenNormal_thenReturnTask() throws SQLException {
        Mockito
                .when(taskRepository.addTask(tasks, employeeId))
                .thenReturn(nextTask);
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenReturn(employee);

        TasksDto nextTaskDto = taskService.addTasks(taskDto, employeeId);
        assertThat(addTask, equalTo(nextTaskDto));
        Mockito
                .verify(taskRepository, Mockito.times(1))
                .addTask(tasks, employeeId);
    }

    @Test
    void addTask_whenEmployeeNotFound_thenThrowException() throws SQLException {
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenThrow(new SQLException("Employee Not Found"));

        SQLException e = assertThrows(SQLException.class,
                () -> taskService.addTasks(taskDto, employeeId));
        assertThat(e.getMessage(), equalTo("Employee Not Found"));
        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeeById(employeeId);
    }

    @Test
    void updateTask_whenNormal_thenReturnUpdatedTask() throws SQLException {
        tasks.setEmployee(employee);
        nextTask.setEmployee(employee);
        Mockito
                .when(taskRepository.updateTask(tasks,tasks.getId()))
                .thenReturn(nextTask);

        Mockito
                .when(taskRepository.getTaskById(tasks.getId()))
                .thenReturn(tasks);

        TasksDto updatedTask = taskService.updateTasks(taskDto, tasks.getId());
        assertThat(taskDto, equalTo(updatedTask));
        Mockito
                .verify(taskRepository, Mockito.times(1))
                .updateTask(tasks,tasks.getId());
    }

    @Test
    void updateTask_whenTaskNotFound_thenThrowException() throws SQLException {
        Mockito
                .when(taskRepository.getTaskById(tasks.getId()))
                .thenThrow(new SQLException("Task Not Found"));

        SQLException e = assertThrows(SQLException.class,
                () -> taskService.updateTasks(taskDto, taskId));
        assertThat(e.getMessage(), equalTo("Task Not Found"));
        Mockito
                .verify(taskRepository, Mockito.times(1))
                .updateTask(tasks,tasks.getId());
    }

    @Test
    void deleteTask_whenNormal_thenDeleteTask() throws SQLException {
        Mockito
                .doNothing()
                .when(taskRepository).deleteTask(taskId);

        taskService.deleteTasks(taskId);
        Mockito
                .verify(taskRepository, Mockito.times(1))
                .deleteTask(taskId);
    }

    @Test
    void deleteTask_whenTaskNotFound_thenThrowException() throws SQLException {
        Mockito
                .doThrow(new SQLException("Task Not Found"))
                .when(taskRepository).getTaskById(taskId);

        SQLException e = assertThrows(SQLException.class,
                () -> taskService.deleteTasks(taskId));
        assertThat(e.getMessage(), equalTo("Task Not Found"));
        Mockito
                .verify(taskRepository, Mockito.times(1))
                .getTaskById(taskId);
    }

    @Test
    void getTasksByEmployeeId_whenNormal_thenReturnTasks() throws SQLException {
        tasks.setEmployee(employee);
        nextTask.setEmployee(employee);
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenReturn(employee);

        Mockito
                .when(taskRepository.getTasksByEmployeeId(employeeId))
                .thenReturn(List.of(tasks, nextTask));

        List<TasksDto> tasksDtoByEmployee = taskService.getTasksByEmployeeId(employeeId);
        assertThat(taskDto, equalTo(tasksDtoByEmployee));
        Mockito
                .verify(taskRepository, Mockito.times(1))
                .getTasksByEmployeeId(employeeId);
    }

    @Test
    void getTasksByEmployeeId_whenEmployeeNotFound_thenThrowException() throws SQLException {
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenThrow(new SQLException("Employee Not Found"));

        SQLException e = assertThrows(SQLException.class,
                () -> taskService.getTasksByEmployeeId(employeeId));
        assertThat(e.getMessage(), equalTo("Employee Not Found"));
        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeeById(employeeId);
    }
}

