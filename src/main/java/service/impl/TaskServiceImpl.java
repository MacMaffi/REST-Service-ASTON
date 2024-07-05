package service.impl;

import model.Employee;
import model.Tasks;
import model.dto.TasksDto;
import model.mapper.TasksMapper;
import repository.EmployeeRepository;
import repository.TasksRepository;
import repository.impl.EmployeeRepositoryImp;
import repository.impl.TasksRepositoryImp;
import service.TasksService;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class TaskServiceImpl implements TasksService {

    private static final TaskServiceImpl INSTANCE = new TaskServiceImpl();

    private final TasksRepository tasksRepository = TasksRepositoryImp.getInstance();
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImp.getInstance();

    private TaskServiceImpl() {
    }

    @Override
    public TasksDto addTasks(TasksDto tasksDto, long id) throws SQLException {
        Employee employee = employeeRepository.getEmployeeById(id);
        Tasks tasks = TasksMapper.toTasks(tasksDto,0);
        Tasks savedTasks = tasksRepository.addTask(tasks,id);
        savedTasks.setEmployee(employee);

        return TasksMapper.toTasksDto(savedTasks);
    }

    @Override
    public TasksDto updateTasks(TasksDto tasksDto, long id) throws SQLException {
        Tasks oldTasks = tasksRepository.getTaskById(id);
        Tasks tasks = TasksMapper.toTasks(tasksDto,id);
        tasks.setEmployee(oldTasks.getEmployee());

        return TasksMapper.toTasksDto(tasksRepository.updateTask(tasks,id));
    }

    @Override
    public void deleteTasks(long id) throws SQLException {
        tasksRepository.getTasksByEmployeeId(id);
        tasksRepository.deleteTask(id);
    }

    @Override
    public List<TasksDto> getTasksByEmployeeId(long id) throws SQLException {
        Employee employee = employeeRepository.getEmployeeById(id);
        List<Tasks> tasks = tasksRepository.getTasksByEmployeeId(id);

        return tasks.stream()
                .peek(task -> task.setEmployee(employee))
                .map(task-> TasksMapper.toTasksDto(task))
                .collect(Collectors.toList());
    }

    public static TaskServiceImpl getInstance() {
        return INSTANCE;
    }
}
