package repository.impl;

import model.Activity;
import model.Employee;
import repository.ActivityRepository;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityRepositoryImp implements ActivityRepository {

    private static final ActivityRepositoryImp INSTANCE= new ActivityRepositoryImp();

    private ActivityRepositoryImp() {}

    private static final String ADD_ACTIVITY = """
            INSERT INTO activity(name) VALUES (?)
            """;

    private static final String UPDATE_ACTIVITY = """
            UPDATE activity SET name = ? WHERE id = ?
            """;

    private  static  final String DELETE_ACTIVITY = """
            DELETE FROM activity WHERE id = ?
            """;

    private static final String GET_ACTIVITY_BY_ID = """
            SELECT * FROM activity WHERE id = ?
            """;

    private static final String GET_ACTIVITIES_WITH_EMPLOYEE =  """
            SELECT  a.id AS activity_id,
                    a.name AS activity_name,
                    e.id AS employee_id,
                    e.emp_firstname AS employee_first_name,
                    e.emp_lastname AS employee_last_name,
                    e.position AS employee_position
            FROM activity AS a
            LEFT JOIN employee_activity AS ea ON a.id = ea.activity_id
            LEFT JOIN employee AS e ON ea.employee_id = e.id
            ORDER BY a.id
            """;

    @Override
    public Activity addActivity(Activity activity) throws SQLException {
        Activity newActivity = null;
        List<Employee> employees = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(ADD_ACTIVITY,
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, activity.getName());

            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("Activity not added");
            }

            ResultSet rs = ps.getGeneratedKeys();
            long activityId = 0;
            if (rs.next()) {
                activityId = rs.getLong("id");
            }

            var ps2 = connection.prepareStatement("SELECT * FROM activity WHERE id = ?");
            ps2.setLong(1, activityId);
            try (var resultSet = ps2.executeQuery()) {
                while (resultSet.next()) {
                    newActivity = buildActivity(resultSet);
                }
            }
            ps2.close();
        }
        if (newActivity != null) {
            newActivity.setEmployees(employees);
        }
        return newActivity;
    }

    @Override
    public Activity updateActivity(Activity activity) throws SQLException {
        Activity newActivity = null;
        List<Employee> employees = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(UPDATE_ACTIVITY)) {

            ps.setString(1, activity.getName());
            ps.setLong(2, activity.getId());
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Activity not updated");
            }

            var ps1 = connection.prepareStatement("SELECT * FROM activity WHERE id = ?");
            ps1.setLong(1, activity.getId());
            try (var resultSet = ps1.executeQuery()) {
                while (resultSet.next()) {
                    newActivity = buildActivity(resultSet);
                }
            }
            ps1.close();
        }
        return newActivity;
    }

    @Override
    public void deleteActivity(long activityId) throws SQLException {
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(DELETE_ACTIVITY)) {
            ps.setLong(1, activityId);
            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("Activity not deleted");
            }
        }
    }

    @Override
    public Activity getActivityById(long activityId) throws SQLException {
        Activity newActivity = null;
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(GET_ACTIVITY_BY_ID)) {

            ps.setLong(1, activityId);
            try (var resultSet = ps.executeQuery()) {
                while (resultSet.next()){
                    newActivity = buildActivity(resultSet);
                }
            }
        }
        if (newActivity == null) {
            throw new SQLException("Activity not found");
        } else {
            return newActivity;
        }
    }

    @Override
    public Map<Activity, List<Employee>> getActivitiesWithEmployee() throws SQLException {

        try (Connection connection = ConnectionManager.get();
             PreparedStatement ps = connection.prepareStatement(GET_ACTIVITIES_WITH_EMPLOYEE);
             ResultSet rs = ps.executeQuery()) {

            Map<Activity, List<Employee>> activitiesWithEmployees = new HashMap<>();

            while (rs.next()) {
                long activityId = rs.getLong("activity_id");      // получаем id активности
                String activityName = rs.getString("activity_name");

                Activity activity = new Activity(activityId, activityName);

                List<Employee> employees = activitiesWithEmployees.getOrDefault(activity, new ArrayList<>());

                long employeeId = rs.getLong("employee_id");      // получаем id сотрудника
                String employeeFirstName = rs.getString("employee_first_name");
                String employeeLastName = rs.getString("employee_last_name");
                String employeePosition = rs.getString("employee_position");

                if (employeeId != 0) {  // Проверяем, есть ли сотрудник
                    Employee employee = new Employee(employeeId, employeeFirstName, employeeLastName, employeePosition);
                    employees.add(employee);
                }

                activitiesWithEmployees.put(activity, employees);
            }

            return activitiesWithEmployees;
        }
    }

    public static ActivityRepositoryImp getInstance() {

        return INSTANCE;
    }

    public  Activity buildActivity(ResultSet rs) throws SQLException {
        return new Activity(
                rs.getLong("id"),
                rs.getString("name"));
    }

    public Employee buildEmployee(ResultSet rs) throws SQLException {
        return new Employee(
                rs.getLong("id"),
                rs.getString("emp_firstname"),
                rs.getString("emp_lastname"),
                rs.getString("position"));
    }
}
