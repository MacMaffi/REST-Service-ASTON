package testservice;

import model.Activity;
import model.Employee;
import model.Tasks;
import model.dto.EmployeeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.ActivityRepository;
import repository.EmployeeRepository;
import repository.TasksRepository;
import service.impl.EmployeeServiceImpl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @InjectMocks
    private EmployeeServiceImpl employeeService;
    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    ActivityRepository activityRepository;

    private Activity activity;
    private Employee employee;
    private EmployeeDto employeeDto;
    private EmployeeDto employeeDtoExpected;
    private Long employeeId;
    private Tasks tasks;
    private Long activityId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        activity = new Activity(1L, "Мафия");
        employee = new Employee(1L, "Иван", "Петров", "Разработчик");
        employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        employeeDtoExpected = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(1L), List.of());
        employeeId = 1L;
        tasks = new Tasks(1L, "Задача","На рассмотрении");
        activityId = 1L;

    }

    @Test
    void addEmployee_WhenNormalReturnEmployee() throws SQLException {
        Mockito
                .when(employeeRepository.addEmployee(employee, List.of(1L)))
                .thenReturn(employee);
        Mockito
                .when(activityRepository.getActivityById(1L))
                .thenReturn(activity);

        EmployeeDto addedEmployee = employeeService.addEmployee(employeeDto, List.of(1L));
        assertThat(employeeDtoExpected, equalTo(addedEmployee));

        Mockito.
                verify(employeeRepository, Mockito.times(1)).
                getEmployeeById(1L);
        Mockito
                .verify(activityRepository, Mockito.times(1))
                .getActivityById(1L);
    }

    @Test
    void addEmployee_whenActivityNotFound_thenThrowException() throws SQLException {
        Mockito
                .when(activityRepository.getActivityById(activity.getId()))
                .thenThrow(new IllegalArgumentException("Activity not found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> employeeService.addEmployee(employeeDto, List.of(activity.getId())));

        assertThat(e.getMessage(), equalTo("Activity not found"));
        Mockito
                .verify(activityRepository, Mockito.times(1))
                .getActivityById(activity.getId());
    }

    @Test
    void updateEmployee_WhenNormalReturnEmployee() throws SQLException {
        employeeDto.setActivity(List.of(1L));
        Mockito
                .doReturn(employee)
                .when(employeeRepository).updateEmployee(employee);

        Mockito
                .doReturn(List.of(activity))
                .when(employeeRepository).findActivityByEmployeeId(1L);

        Mockito
                .doReturn(new ArrayList<>())
                .when(tasksRepository).getTasksByEmployeeId(1L);

        EmployeeDto updateEmployee = employeeService.updateEmployee(employeeDtoExpected, employee.getId());
        assertThat(updateEmployee, equalTo(employeeDto));
        Mockito
                .verify(employeeRepository, times(1))
                .findActivityByEmployeeId(employee.getId());
        Mockito
                .verify(tasksRepository, times(1))
                .getTasksByEmployeeId(employee.getId());
    }

    @Test
    void updateEmployee_whenEmployeeNotFound_thenThrowException() throws SQLException {
        Mockito
                .when(employeeRepository.getEmployeeById(employee.getId()))
                .thenThrow(new IllegalArgumentException("Employee not found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> employeeService.updateEmployee(employeeDto, employee.getId()));

        assertThat(e.getMessage(), equalTo("Employee not found"));
        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeeById(employee.getId());
    }

    @Test
    void deleteEmployeeWhenNormal() throws SQLException {
        Mockito
                .doNothing()
                .when(employeeRepository).deleteEmployee(employeeId);

        employeeService.deleteEmployee(employeeId);
        Mockito
                .verify(employeeRepository, times(1))
                .deleteEmployee(employeeId);
    }

    @Test
    void deleteEmployee_WhenEmployeeNotFound() throws SQLException {
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenThrow(new IllegalArgumentException("Employee not found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> employeeService.deleteEmployee(employeeId));

        assertThat(e.getMessage(), equalTo("Employee not found"));
        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeeById(employeeId);
    }

    @Test
    void getEmployeeById_WhenNormalReturnEmployee() throws SQLException {
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenReturn(employee);

        EmployeeDto findEmployee = employeeService.getEmployeeById(employeeId);
        assertThat(employeeDto, equalTo(findEmployee));
        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeeById(employeeId);
    }

    @Test
    void getEmployeeById_WhenNormalWithTasks_ReturnEmployee() throws SQLException {
        employee.setActivities(List.of(activity));
        tasks.setEmployee(employee);
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenReturn(employee);
        Mockito
                .when(employeeRepository.findActivityByEmployeeId(employeeId))
                .thenReturn(List.of(activity));
        Mockito
                .when(tasksRepository.getTasksByEmployeeId(employeeId))
                .thenReturn(List.of(tasks));

        EmployeeDto findEmployee = employeeService.getEmployeeById(employeeId);
        assertThat(findEmployee.getTasks().size(), equalTo(1));
        Mockito
                .verify(tasksRepository, Mockito.times(1))
                .getTasksByEmployeeId(employeeId);

        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeeById(employeeId);

        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .findActivityByEmployeeId(employeeId);
    }

    @Test
    void getEmployeeById_WhenEmployeeNotFound() throws SQLException {
        Mockito
                .when(employeeRepository.getEmployeeById(employeeId))
                .thenThrow(new IllegalArgumentException("Employee not found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> employeeService.getEmployeeById(employeeId));

        assertThat(e.getMessage(), equalTo("Employee not found"));
        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeeById(employeeId);
    }

    @Test
    void getEmployeesByActivityId_WhenNormal_ReturnEmployees() throws SQLException {
        Mockito
                .when(employeeRepository.getEmployeesByActivityId(activityId))
                .thenReturn(List.of(employee));

        Mockito
                .when(activityRepository.getActivityById(activityId))
                .thenReturn(new Activity(1L,"Дизайн"));

        List<EmployeeDto> employees = employeeService.getEmployeesByActivityId(activityId);
        assertThat(employees.size(), equalTo(1));
        Mockito
                .verify(employeeRepository, Mockito.times(1))
                .getEmployeesByActivityId(activityId);

        Mockito
                .verify(activityRepository, Mockito.times(1))
                .getActivityById(activityId);
    }

    @Test
    void getEmployeesByActivityId_WhenActivityNotFound() throws SQLException {
        Mockito
                .when(activityRepository.getActivityById(activityId))
                .thenThrow(new IllegalArgumentException("Activity not found"));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> employeeService.getEmployeesByActivityId(activityId));

        assertThat(e.getMessage(), equalTo("Activity not found"));
        Mockito
                .verify(activityRepository, Mockito.times(1))
                .getActivityById(activityId);
    }
}
