package testservice;

import model.Activity;
import model.Employee;
import model.dto.ActivityDto;
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
import service.impl.ActivityServiceImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @InjectMocks
    ActivityServiceImpl activityService;

    @Mock
    ActivityRepository activityRepository;

    @Mock
    EmployeeRepository employeeRepository;

    private ActivityDto activityDto;
    private Activity activity;
    private Employee employee;
    private Employee otherEmployee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new Employee(1L, "Иван", "Петров", "Разработчик");
        otherEmployee = new Employee(2L, "Никита", "Николаев", "Тестировщик");

        activityDto = new ActivityDto("Мафия", List.of(employee, otherEmployee));
        activity = new Activity(1L, "Мафия", List.of(employee, otherEmployee));
    }

    @Test
    void addActivity_WhenNormal_ReturnActivity() throws SQLException {
        Mockito
                .when(activityRepository.addActivity(activity))
                .thenReturn(activity);

        ActivityDto savedActivity = activityService.addActivity(activityDto);

        assertThat(savedActivity, equalTo(activityDto));
        Mockito.verify(activityRepository, Mockito.times(1)).addActivity(any());
    }

    @Test
    void addActivity_WhenSQLExceptionThrown() throws SQLException {
        Mockito
                .when(activityRepository.addActivity(activity))
                .thenThrow(new SQLException("Activity is not added"));
        SQLException e =assertThrows(SQLException.class,
                () -> activityService.addActivity(activityDto));
        assertThat(e.getMessage(), equalTo("Activity is not added"));
        Mockito.verify(activityRepository, Mockito.times(1)).addActivity(any());
    }

    @Test
    void updateActivity_WhenNormalReturnActivity() throws SQLException {
        Mockito
                .when(activityRepository.updateActivity(activity))
                .thenReturn(activity);
        ActivityDto updatedActivity = activityService.updateActivity(activityDto, 1L);
        assertThat(activityDto, equalTo(updatedActivity));
        Mockito.verify(activityRepository, Mockito.times(1)).updateActivity(any());
    }

    @Test
    void updateActivity_WhenNotFoundActivity_ReturnUpdateActivity() throws SQLException {
        Mockito
                .when(activityRepository.getActivityById(activity.getId()))
                .thenThrow(new IllegalArgumentException("Activity not found"));
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> activityService.updateActivity(activityDto, 1L));
        assertThat(e.getMessage(), equalTo("Activity not found"));
        Mockito.verify(activityRepository, Mockito.times(1)).getActivityById(1L);
    }

    @Test
    void updateActivity_WhenSQLExceptionThrown() throws SQLException {
        Mockito
                .when(activityRepository.updateActivity(activity))
                .thenThrow(new SQLException("Activity is not updated"));
        SQLException e = assertThrows(SQLException.class,
                () -> activityService.updateActivity(activityDto, 1L));
        assertThat(e.getMessage(), equalTo("Activity is not updated"));
        Mockito.verify(activityRepository, Mockito.times(1)).updateActivity(activity);
    }

    @Test
    void deleteActivity_WhenNormal() throws SQLException {
        Mockito
                .doNothing()
                .when(activityRepository)
                .deleteActivity(activity.getId());
        activityService.deleteActivity(activity.getId());
        Mockito.verify(activityRepository, Mockito.times(1)).deleteActivity(activity.getId());
    }

    @Test
    void deleteActivity_WhenSQLExceptionThrown() throws SQLException {
        Mockito
                .doThrow(new SQLException("Activity not found"))
                .when(activityRepository).deleteActivity(activity.getId());

        SQLException e = assertThrows(SQLException.class,
                () -> activityService.deleteActivity(activity.getId()));

        assertThat(e.getMessage(), equalTo("Activity not found"));
        Mockito.verify(activityRepository, Mockito.times(1)).deleteActivity(activity.getId());
    }

    @Test
    void getActivityById_WhenNormalReturnActivity() throws SQLException {
        Mockito
                .when(activityRepository.getActivityById(activity.getId()))
                .thenReturn(activity);

        ActivityDto foundActivity = activityService.getActivityById(activity.getId());

        assertThat(foundActivity, equalTo(activityDto));
        Mockito.verify(activityRepository, Mockito.times(1)).getActivityById(activity.getId());
    }

    @Test
    void getActivityById_WhenSQLExceptionThrown() throws SQLException {
        Mockito
                .when(activityRepository.getActivityById(activity.getId()))
                .thenThrow(new SQLException("Activity not found"));

        SQLException e = assertThrows(SQLException.class,
                () -> activityService.getActivityById(activity.getId()));

        assertThat(e.getMessage(), equalTo("Activity not found"));
        Mockito.verify(activityRepository, Mockito.times(1)).getActivityById(activity.getId());
    }


    @Test
    void getAllActivities_WhenNormalReturnActivities() throws SQLException {
        Map<Activity, List<Employee>> activitiesMap = Map.of(activity, List.of(employee, otherEmployee));
        Mockito
                .when(activityRepository.getActivitiesWithEmployee())
                .thenReturn(activitiesMap);

        List<ActivityDto> activities = activityService.getAllActivities();

        assertThat(activities.size(), equalTo(1));
        assertThat(activities.get(0).getName(), equalTo("Мафия"));
        assertThat(activities.get(0).getEmployees().size(), equalTo(2));
        Mockito.verify(activityRepository, Mockito.times(1)).getActivitiesWithEmployee();
    }
}




