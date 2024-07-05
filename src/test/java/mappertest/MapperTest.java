package mappertest;

import model.Activity;
import model.Employee;
import model.Tasks;
import model.dto.ActivityDto;
import model.dto.EmployeeDto;
import model.dto.TasksDto;
import model.mapper.ActivityMapper;
import model.mapper.EmployeeMapper;
import model.mapper.TasksMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class MapperTest {

    private ActivityDto activityDto;
    private Activity activity;
    private Employee employee;
    private Employee otherEmployee;
    private EmployeeDto employeeDto;
    private Tasks tasks;
    private TasksDto tasksDto;

    @BeforeEach
    void createActivityAndActivityDto(){
        employee = new Employee(1L, "Иван", "Петров", "Разработчик");
        otherEmployee = new Employee(2L, "Никита", "Николаев", "Тестировщик");

        activityDto = new ActivityDto("Мафия", List.of(employee, otherEmployee));
        activity = new Activity(1L, "Мафия",List.of(employee, otherEmployee));

        tasks = new Tasks(1L, "Создание микросервиса","На утверждении");
        tasksDto = new TasksDto("Иван","Петров","Создание микросервиса","На утверждении");
        employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик",List.of(1L),List.of(tasksDto));

        employee.setActivities(List.of(activity));
        employee.setTasks(List.of(tasks));
        otherEmployee.setActivities(List.of(activity));
    }

    @Test
    void toActivityDto(){
        ActivityDto newActivityDto = ActivityMapper.toActivityDto(activity);
        assertThat(newActivityDto, equalTo(activityDto));
    }

    @Test
    void toActivity(){
        Activity newActivity = ActivityMapper.toActivity(1L,activityDto);
        assertThat(newActivity.getName(), equalTo(activity.getName()));
        assertThat(newActivity.getEmployees(), equalTo(activity.getEmployees()));
    }

    @Test
    void toEmployeeDto(){
        EmployeeDto newEmployeeDto = EmployeeMapper.toEmployeeDto(employee);
        assertThat(newEmployeeDto, equalTo(employeeDto));
    }

    @Test
    void toEmployee(){
        Employee newEmployee = EmployeeMapper.toEmployee(employeeDto, 1L);
        assertThat(newEmployee.getId(),equalTo(employee.getId()));
        assertThat(newEmployee.getFirstName(),equalTo(employee.getFirstName()));
        assertThat(newEmployee.getLastName(),equalTo(employee.getLastName()));
        assertThat(newEmployee.getPosition(),equalTo(employee.getPosition()));
    }

    @Test
    void toTasksDto(){
        TasksDto newTasksDto = TasksMapper.toTasksDto(tasks);
        assertThat(newTasksDto, equalTo(tasksDto));
    }

    @Test
    void toTasks(){
        Tasks newTasks = TasksMapper.toTasks(tasksDto, 1L);
        assertThat(newTasks.getId(),equalTo(tasks.getId()));
        assertThat(newTasks.getTitle(),equalTo(tasks.getTitle()));
        assertThat(newTasks.getStatus(),equalTo(tasks.getStatus()));
        assertThat(newTasks.getEmployee().getFirstName(), equalTo(tasksDto.getFirstName()));
        assertThat(newTasks.getEmployee().getLastName(), equalTo(tasksDto.getLastName()));
    }
}
