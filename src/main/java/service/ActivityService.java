package service;

import model.dto.ActivityDto;

import java.sql.SQLException;
import java.util.List;

public interface ActivityService {

    ActivityDto addActivity(ActivityDto activityDto) throws SQLException;

    ActivityDto updateActivity(ActivityDto activityDto,long id) throws SQLException;

    void deleteActivity(long id) throws SQLException;

    ActivityDto getActivityById(long id) throws SQLException;

    List<ActivityDto> getAllActivities() throws SQLException;
}
