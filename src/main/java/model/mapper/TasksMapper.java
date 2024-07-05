package model.mapper;

import model.Employee;
import model.Tasks;
import model.dto.TasksDto;

public class TasksMapper {

    public static TasksDto toTasksDto(Tasks tasks) {
        Employee employee = tasks.getEmployee();
        return new TasksDto(employee.getFirstName(), employee.getLastName(), tasks.getTitle(), tasks.getStatus());
    }

    public static Tasks toTasks(TasksDto tasksDto, long taskId) {
        return new Tasks(taskId, tasksDto.getTitle(), tasksDto.getStatus());
    }
}
