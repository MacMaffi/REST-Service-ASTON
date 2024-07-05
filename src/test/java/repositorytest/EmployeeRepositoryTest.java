package repositorytest;

import model.Activity;
import model.Employee;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.EmployeeRepository;
import repository.impl.EmployeeRepositoryImp;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class EmployeeRepositoryTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("company")
            .withUsername("root")
            .withPassword("root")
            .withInitScript("schema.sql");

    EmployeeRepository employeeRepository;

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
        employeeRepository = EmployeeRepositoryImp.getInstance();
    }

    private static void setupProperties() {
        System.setProperty("db.driver", "org.postgresql.Driver");
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
    }

    @Test
    void addEmployee() throws SQLException {
        Employee newEmployee = new Employee(0L, "Денис", "Петров", "Разработчик");
        List<Long> activityList = List.of(1L, 2L);
        Employee employee = employeeRepository.addEmployee(newEmployee, activityList);
        newEmployee.setId(employee.getId());
        assertThat(newEmployee, equalTo(employee));
    }

    @Test
    void updateEmployee() throws SQLException {
        Employee employee = new Employee(0L, "Иван", "Иванов", "Разработчик");
        Employee newEmployee = employeeRepository.addEmployee(employee, List.of());

        newEmployee.setFirstName("Дмитрий");
        Employee updatedEmployee = employeeRepository.updateEmployee(newEmployee);
        assertThat(updatedEmployee, equalTo(newEmployee));
    }

    @Test
    void deleteEmployee() throws SQLException {
        Employee newEmployee = new Employee(0L, "Анна", "Козлова", "Менеджер");
        Employee employee = employeeRepository.addEmployee(newEmployee, List.of());
        employeeRepository.deleteEmployee(employee.getId());
        assertThrows(SQLException.class, () -> employeeRepository.getEmployeeById(employee.getId()));
    }

    @Test
    void getEmployeeById() throws SQLException {
        Employee newEmployee = new Employee(0L, "Алексей", "Смирнов", "Дизайнер");
        Employee employee = employeeRepository.addEmployee(newEmployee, List.of());
        Employee foundEmployee = employeeRepository.getEmployeeById(employee.getId());
        assertThat(foundEmployee, equalTo(employee));
    }

    @Test
    void findActivityByEmployeeId() throws SQLException {
        Employee newEmployee = new Employee(0L, "Charlie", "Brown", "Tester");
        Employee employee = employeeRepository.addEmployee(newEmployee, List.of(1L, 2L));
        List<Activity> activities = employeeRepository.findActivityByEmployeeId(employee.getId());
        assertNotNull(activities);
        assertFalse(activities.isEmpty());
    }

    @Test
    void getEmployeesByActivityId() throws SQLException {
        List<Employee> employees = employeeRepository.getEmployeesByActivityId(1L);
        assertNotNull(employees);
        assertFalse(employees.isEmpty());
    }

    @Test
    void addEmployeeWhenAlreadyExists() throws SQLException {
        Employee newEmployee = new Employee(0L, "Денис", "Петров", "Разработчик");
        employeeRepository.addEmployee(newEmployee, List.of());
        assertThrows(SQLException.class, () -> employeeRepository.addEmployee(newEmployee, List.of()));
    }

    @Test
    void updateEmployeeWhenDoesNotExist() throws SQLException {
        Employee newEmployee = new Employee(99L, "Valera", "Leontiev", "Singer");
        assertThrows(SQLException.class, () -> employeeRepository.updateEmployee(newEmployee));
    }

    @Test
    void deleteEmployeeWhenDoesNotExist() throws SQLException {
        assertThrows(SQLException.class, () -> employeeRepository.deleteEmployee(99L));
    }

    @Test
    void getEmployeeByIdWhenDoesNotExist() throws SQLException {
        assertThrows(SQLException.class, () -> employeeRepository.getEmployeeById(99L));
    }
}
