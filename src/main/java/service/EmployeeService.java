package service;

import model.dto.EmployeeDto;

import java.sql.SQLException;
import java.util.List;

public interface EmployeeService {

    EmployeeDto addEmployee(EmployeeDto employeeDto, List<Long> activityList) throws SQLException;

    EmployeeDto updateEmployee(EmployeeDto employeeDto, long id) throws SQLException;

    void deleteEmployee(long id) throws SQLException;

    EmployeeDto getEmployeeById(long id) throws SQLException;

    List<EmployeeDto> getEmployeesByActivityId(long id) throws SQLException;
}
