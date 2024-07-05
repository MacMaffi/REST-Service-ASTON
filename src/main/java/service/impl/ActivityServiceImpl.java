package service.impl;

import model.Activity;
import model.Employee;
import model.dto.ActivityDto;
import model.mapper.ActivityMapper;
import repository.ActivityRepository;
import repository.EmployeeRepository;
import repository.impl.ActivityRepositoryImp;
import repository.impl.EmployeeRepositoryImp;
import service.ActivityService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActivityServiceImpl implements ActivityService {

    private static final ActivityServiceImpl INSTANCE = new ActivityServiceImpl();

    private final ActivityRepository repository = ActivityRepositoryImp.getInstance();
    private final EmployeeRepository employeeRepository = EmployeeRepositoryImp.getInstance();

    private ActivityServiceImpl() {
    }

    @Override
    public ActivityDto addActivity(ActivityDto activityDto) throws SQLException {
        Activity activity = ActivityMapper.toActivity(0, activityDto);

        return ActivityMapper.toActivityDto(repository.addActivity(activity));
    }

    @Override
    public ActivityDto updateActivity(ActivityDto activityDto, long id) throws SQLException {
        repository.getActivityById(id);
        Activity activity = repository.updateActivity(ActivityMapper.toActivity(id, activityDto));

        return ActivityMapper.toActivityDto(activity);
    }

    @Override
    public void deleteActivity(long id) throws SQLException {
        repository.getActivityById(id);
        repository.deleteActivity(id);
    }

    @Override
    public ActivityDto getActivityById(long id) throws SQLException {
        Activity activity = repository.getActivityById(id);
        return ActivityMapper.toActivityDto(activity);
    }

    @Override
    public List<ActivityDto> getAllActivities() throws SQLException {
        Map<Activity, List<Employee>> map = repository.getActivitiesWithEmployee();
        return map.entrySet().stream()
                .peek(e -> e.getKey().setEmployees(e.getValue()))
                .map(e -> ActivityMapper.toActivityDto(e.getKey()))
                .collect(Collectors.toList());
    }

    public static ActivityServiceImpl getInstance() {
        return INSTANCE;
    }
}
