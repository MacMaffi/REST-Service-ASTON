package repositorytest;

import model.Tasks;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.TasksRepository;
import repository.impl.TasksRepositoryImp;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class TasksRepositoryTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("company")
            .withUsername("root")
            .withPassword("root")
            .withInitScript("schema.sql");

    TasksRepository tasksRepository;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
        setupProperties();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        setupProperties();
        tasksRepository = TasksRepositoryImp.getInstance();
    }

    private static void setupProperties() {
        System.setProperty("db.driver", "org.postgresql.Driver");
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
    }

    @Test
    void addTask() throws SQLException {
        Tasks newTask = new Tasks(0L, "Составление отчета", "На утверждении");
        Tasks task = tasksRepository.addTask(newTask, 1L);
        newTask.setId(task.getId());
        assertThat(newTask, equalTo(task));
    }

    @Test
    void updateTask() throws SQLException {
        Tasks task = new Tasks(0L, "Код-ревью", "На утверждении");
        Tasks newTask = tasksRepository.addTask(task, 1L);

        newTask.setTitle("Рефакторинг кода");
        newTask.setStatus("В процессе");
        Tasks updatedTask = tasksRepository.updateTask(newTask, newTask.getId());
        assertThat(updatedTask, equalTo(newTask));
    }

    @Test
    void deleteTask() throws SQLException {
        Tasks newTask = new Tasks(0L, "Тестирование системы", "Завершено");
        Tasks task = tasksRepository.addTask(newTask, 1L);
        tasksRepository.deleteTask(task.getId());
        assertThrows(SQLException.class, () -> tasksRepository.getTaskById(task.getId()));
    }

    @Test
    void getTaskById() throws SQLException {
        Tasks newTask = new Tasks(0L, "Управление проектом", "В процессе");
        Tasks task = tasksRepository.addTask(newTask, 1L);
        Tasks fetchedTask = tasksRepository.getTaskById(task.getId());
        assertThat(fetchedTask, equalTo(task));
    }

    @Test
    void getTasksByEmployeeId() throws SQLException {
        List<Tasks> tasks = tasksRepository.getTasksByEmployeeId(1L);
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
    }

    @Test
    void addTaskWhenAlreadyExists() throws SQLException {
        Tasks newTask = new Tasks(0L, "Составление отчета", "На утверждении");
        tasksRepository.addTask(newTask, 1L);
        assertThrows(SQLException.class, () -> tasksRepository.addTask(newTask, 1L));
    }

    @Test
    void updateTaskWhenNotExists() throws SQLException {
        Tasks newTask = new Tasks(99L, "Uknown", "Uknown");
        assertThrows(SQLException.class, () -> tasksRepository.updateTask(newTask, newTask.getId()));
    }

    @Test
    void deleteTaskWhenNotExists() throws SQLException {
        assertThrows(SQLException.class, () -> tasksRepository.deleteTask(99L));
    }

    @Test
    void getTaskByIdWhenNotExists() throws SQLException {
        assertThrows(SQLException.class, () -> tasksRepository.getTaskById(99L));
    }
}
