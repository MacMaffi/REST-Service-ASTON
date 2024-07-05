package service.impl;

import model.Activity;
import model.Employee;
import model.Tasks;
import model.dto.EmployeeDto;
import model.mapper.EmployeeMapper;
import repository.ActivityRepository;
import repository.EmployeeRepository;
import repository.TasksRepository;
import repository.impl.ActivityRepositoryImp;
import repository.impl.EmployeeRepositoryImp;
import repository.impl.TasksRepositoryImp;
import service.EmployeeService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

public class EmployeeServiceImpl implements EmployeeService {

    private static final EmployeeServiceImpl INSTANCE = new EmployeeServiceImpl();
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImp.getInstance();
    private final ActivityRepository activityRepository = ActivityRepositoryImp.getInstance();
    private static final TasksRepository tasksRepository = TasksRepositoryImp.getInstance();

    public EmployeeServiceImpl() {
    }

    @Override
    public EmployeeDto addEmployee(EmployeeDto employeeDto, List<Long> activityList) throws SQLException {
        List<Activity> activities = new ArrayList<>();
        for(Long id: activityList){
            activities.add(activityRepository.getActivityById(id));
        }
        Employee employee = employeeRepository.addEmployee(EmployeeMapper.toEmployee(employeeDto, 0), activityList);
        employee.setActivities(activities);
        employee.setTasks(new ArrayList<>());

        return EmployeeMapper.toEmployeeDto(employee);
    }

    @Override
    public EmployeeDto updateEmployee(EmployeeDto employeeDto, long id) throws SQLException {
        employeeRepository.getEmployeeById(id);
        Employee updatedEmployee = employeeRepository.updateEmployee(EmployeeMapper.toEmployee(employeeDto, id));
        List<Activity> activities = employeeRepository.findActivityByEmployeeId(id);
        List<Tasks> tasks = tasksRepository.getTasksByEmployeeId(id);
        tasks.stream()
                .peek(t -> t.setEmployee(updatedEmployee))
                .collect(Collectors.toList());
        updatedEmployee.setTasks(tasks);
        updatedEmployee.setActivities(activities);

        return EmployeeMapper.toEmployeeDto(updatedEmployee);
    }

    @Override
    public void deleteEmployee(long id) throws SQLException {
        employeeRepository.getEmployeeById(id);
        employeeRepository.deleteEmployee(id);
    }

    @Override
    public EmployeeDto getEmployeeById(long id) throws SQLException {
        Employee employee = employeeRepository.getEmployeeById(id);
        List<Activity> activities = employeeRepository.findActivityByEmployeeId(id);
        List<Tasks> tasks = tasksRepository.getTasksByEmployeeId(id);
        tasks.stream()
                .peek(t -> t.setEmployee(employee))
                .collect(Collectors.toList());
        employee.setActivities(activities);
        employee.setTasks(tasks);

        return EmployeeMapper.toEmployeeDto(employee);
    }

    @Override
    public List<EmployeeDto> getEmployeesByActivityId(long id) throws SQLException {
        List<Employee> employees = employeeRepository.getEmployeesByActivityId(id);
        Activity activity = activityRepository.getActivityById(id);
        List<EmployeeDto> employeeDtos = new ArrayList<>();
        for(Employee employee: employees){
            List<Tasks> tasks = tasksRepository.getTasksByEmployeeId(employee.getId());
            employee.setTasks(tasks);
            employee.setActivities(List.of(activity));
            employeeDtos.add(EmployeeMapper.toEmployeeDto(employee));
        }
        return employeeDtos;
    }

    public static EmployeeServiceImpl getInstance() {
        return INSTANCE;
    }
}
