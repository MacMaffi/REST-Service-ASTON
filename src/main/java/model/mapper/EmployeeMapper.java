package model.mapper;

import model.Activity;
import model.Employee;
import model.dto.EmployeeDto;

import java.util.stream.Collectors;

public class EmployeeMapper {

    public static EmployeeDto toEmployeeDto(Employee employee) {
        return new EmployeeDto(employee.getFirstName(), employee.getLastName(),
                employee.getPosition(),
                employee.getActivities().stream()
                        .map(Activity::getId)
                        .collect(Collectors.toList()),
                employee.getTasks().stream()
                        .map(TasksMapper::toTasksDto)
                        .collect(Collectors.toList()));
    }

    public static Employee toEmployee(EmployeeDto employeeDto, long employeeId) {
        return new Employee(employeeId, employeeDto.getFirstName(), employeeDto.getLastName(), employeeDto.getPosition());
    }
}
