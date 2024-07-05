package repository;

import model.Activity;
import model.Employee;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ActivityRepository {

    Activity addActivity(Activity activity) throws SQLException;

    Activity updateActivity(Activity activity) throws SQLException;

    void deleteActivity(long activityId) throws SQLException;

    Activity getActivityById(long activityId) throws SQLException;

    Map<Activity, List<Employee>> getActivitiesWithEmployee() throws SQLException;
}
