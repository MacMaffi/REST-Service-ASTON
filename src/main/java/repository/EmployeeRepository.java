package repository;

import model.Activity;
import model.Employee;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeRepository {

    Employee addEmployee(Employee employee, List<Long> activityList) throws SQLException;

    Employee updateEmployee(Employee employee) throws SQLException;

    void deleteEmployee(long employeeId) throws SQLException;

    Employee  getEmployeeById(long employeeId) throws SQLException;

    List<Activity> findActivityByEmployeeId(long employeeId) throws SQLException;

    List<Employee> getEmployeesByActivityId(long activityId) throws SQLException;

}
