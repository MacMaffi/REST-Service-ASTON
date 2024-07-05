package repositorytest;


import model.Activity;
import model.Employee;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.ActivityRepository;
import repository.impl.ActivityRepositoryImp;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
class ActivityRepositoryTest {

    @Container
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("company")
            .withUsername("root")
            .withPassword("root")
            .withInitScript("schema.sql");

    ActivityRepository activityRepository;

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
    void beforeEach() {
        activityRepository = ActivityRepositoryImp.getInstance();
    }

    private static void setupProperties() {
        System.setProperty("db.driver", "org.postgresql.Driver");
        System.setProperty("db.url", postgres.getJdbcUrl());
        System.setProperty("db.username", postgres.getUsername());
        System.setProperty("db.password", postgres.getPassword());
    }

    @Test
    void addActivity() throws SQLException {
        Activity newActivity = new Activity(0L,"Плавание");
        Activity activity = activityRepository.addActivity(newActivity);
        newActivity.setId(activity.getId());
        assertThat(newActivity, equalTo(activity));
    }

    @Test
    void updateActivity() throws SQLException {
        Activity activity = new Activity(0L,"Футбол");
        Activity newActivity = activityRepository.addActivity(activity);

        newActivity.setName("Бег");
        Activity updatedActivity = activityRepository.updateActivity(newActivity);
        assertThat(updatedActivity, equalTo(newActivity));
    }

    @Test
    void deleteActivity() throws SQLException {
        Activity newActivity = new Activity(0L,"Плавание");
        Activity activity = activityRepository.addActivity(newActivity);
        activityRepository.deleteActivity(activity.getId());
        assertThrows(SQLException.class, () -> activityRepository.getActivityById(activity.getId()));
    }

    @Test
    void getActivityById() throws SQLException {
        Activity newActivity = new Activity(0L,"Бильярд");
        Activity activity = activityRepository.addActivity(newActivity);
        Activity foundActivity = activityRepository.getActivityById(activity.getId());
        assertThat(foundActivity, equalTo(activity));
    }

    @Test
    void getActivitiesWithEmployee() throws SQLException {
        Map<Activity, List<Employee>> activityWithEmployee = activityRepository.getActivitiesWithEmployee();
        assertNotNull(activityWithEmployee);
    }

    @Test
    void addActivityWhenAlreadyExists() throws SQLException {
        Activity newActivity = new Activity(0L,"Волейбол");
        activityRepository.addActivity(newActivity);
        assertThrows(SQLException.class, () -> activityRepository.addActivity(newActivity));
    }

    @Test
    void updateActivityWhenNotExists() throws SQLException {
        Activity newActivity = new Activity(99L,"Теннис");
        assertThrows(SQLException.class, () -> activityRepository.updateActivity(newActivity));
    }

    @Test
    void deleteActivityWhenNotExists() throws SQLException {
        assertThrows(SQLException.class, () -> activityRepository.deleteActivity(99L));
    }

    @Test
    void getActivityWithEmployeeWhenNotExists() throws SQLException {
        Map<Activity, List<Employee>> activityWithEmployee = activityRepository.getActivitiesWithEmployee();
        assertNotNull(activityWithEmployee);
        assertThat(activityWithEmployee.size(), equalTo(0));
    }
}
