package repository.impl;

import model.Activity;
import model.Employee;
import repository.EmployeeRepository;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepositoryImp implements EmployeeRepository {

    private static final EmployeeRepositoryImp INSTANCE= new EmployeeRepositoryImp();

    private EmployeeRepositoryImp() {}

    private static final String ADD_EMPLOYEE = """
            INSERT INTO employee(emp_firstname, emp_lastname, position) VALUES (?,?,?)
            """;

    private  static final String UPDATE_EMPLOYEE = """
             UPDATE employee SET emp_firstname = ?, emp_lastname = ?, position = ? WHERE id = ?
            """;

    private static final String DELETE_EMPLOYEE = """
            DELETE FROM employee WHERE id = ?
            """;

    private static final String GET_EMPLOYEE_BY_ID = """
            SELECT * FROM employee WHERE id = ?
            """;

    private static final String FIND_ACTIVITY_BY_EMPLOYEE_ID = """
            SELECT * FROM activity WHERE id IN 
           (SELECT ea.activity_id FROM employee_activity AS ea WHERE ea.employee_id = ?)
            """;

    private static final String GET_EMPLOYEES_BY_ACTIVITY_ID = """
            SELECT * FROM employee WHERE id IN
           (SELECT ea.employee_id FROM employee_activity AS ea WHERE ea.activity_id = ?)
            """;

    @Override
    public Employee addEmployee(Employee employee, List<Long> activityList) throws SQLException {
        Employee newEmployee = null;
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(ADD_EMPLOYEE,
             Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getPosition());

            int count = ps.executeUpdate();
            if(count == 0) {
                throw new SQLException("Employee not added");
            }

            ResultSet rs = ps.getGeneratedKeys();
            long employeeId = 0;
            while (rs.next()) {
                employeeId = rs.getLong("id");
            }

            for (Long activityId : activityList) {
                var ps1 = connection.prepareStatement("INSERT INTO employee_activity" +
                        " (employee_id, activity_id) VALUES (?,?)");
                ps1.setLong(1, employeeId);
                ps1.setLong(2, activityId);
                int countRow = ps1.executeUpdate();
                if(countRow == 0) {
                    throw new SQLException("Employee and activity not added");
                }
                ps1.close();
            }

            var ps2 = connection.prepareStatement("SELECT * FROM employee WHERE id = ? ");

            ps2.setLong(1, employeeId);
            try (var resultSet = ps2.executeQuery()) {
                while (resultSet.next()){
                    newEmployee = buildEmployee(resultSet);
                }
            }
            ps2.close();
        }
        return newEmployee;
    }

    @Override
    public Employee updateEmployee(Employee employee) throws SQLException {
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(UPDATE_EMPLOYEE)) {

            ps.setString(1, employee.getFirstName());
            ps.setString(2, employee.getLastName());
            ps.setString(3, employee.getPosition());
            ps.setLong(4, employee.getId());
            int count = ps.executeUpdate();

            if (count == 0) {
                throw new SQLException("Employee not updated");
            }

            var ps1 = connection.prepareStatement("SELECT * FROM employee WHERE id = ? ");

            ps1.setLong(1, employee.getId());
            try (var resultSet = ps1.executeQuery()) {
                while (resultSet.next()){
                    employee = buildEmployee(resultSet);
                }
            }
            ps1.close();
        }
        return employee;
    }

    @Override
    public void deleteEmployee(long employeeId) throws SQLException {
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(DELETE_EMPLOYEE)) {
            ps.setLong(1, employeeId);
            int count = ps.executeUpdate();
            if (count == 0) {
                throw new SQLException("Employee not deleted");
            }
        }
    }

    @Override
    public Employee getEmployeeById(long employeeId) throws SQLException {
        Employee findEmployee = null;
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(GET_EMPLOYEE_BY_ID)) {

            ps.setLong(1, employeeId);
            try (var resultSet = ps.executeQuery()) {
                while (resultSet.next()){
                    findEmployee = buildEmployee(resultSet);
                }
            }
        }
        if (findEmployee == null) {
            throw new SQLException("Employee not found");
        } else {
            return findEmployee;
        }
    }

    @Override
    public List<Activity> findActivityByEmployeeId(long employeeId) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(FIND_ACTIVITY_BY_EMPLOYEE_ID)) {
            ps.setLong(1, employeeId);
            try (var resultSet = ps.executeQuery()) {
                while (resultSet.next()){
                    activities.add(buildActivity(resultSet));
                }
            }
        }
        if (activities.isEmpty()) {
            throw new SQLException("Activity not found");
        } else {
            return activities;
        }
    }

    @Override
    public List<Employee> getEmployeesByActivityId(long activityId) throws SQLException {
        List<Employee> employees = new ArrayList<>();

        try (var connection = ConnectionManager.get();
             var ps = connection.prepareStatement(GET_EMPLOYEES_BY_ACTIVITY_ID)) {
            ps.setLong(1, activityId);
            try (var resultSet = ps.executeQuery()) {
                while (resultSet.next()){
                    employees.add(buildEmployee(resultSet));
                }
            }
        }
        return employees;
    }

    public static EmployeeRepositoryImp getInstance() {
        return INSTANCE;
    }

    private Employee buildEmployee(ResultSet rs) throws SQLException {
        return new Employee(
        rs.getLong("id"),
        rs.getString("emp_firstname"),
        rs.getString("emp_lastname"),
        rs.getString("position")
        );
    }

    private Activity buildActivity(ResultSet rs) throws SQLException {
        return new Activity(
                rs.getLong("id"),
                rs.getString("name")
        );
    }
}
