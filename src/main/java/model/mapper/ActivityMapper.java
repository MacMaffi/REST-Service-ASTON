package model.mapper;

import model.Activity;
import model.dto.ActivityDto;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ActivityMapper {

    public static ActivityDto toActivityDto(Activity activity){
        if(activity.getEmployees() == null){
            activity.setEmployees(new ArrayList<>());
        }

        return new ActivityDto(activity.getName(), activity.getEmployees());
    }

    public static Activity toActivity(long activityId, ActivityDto activityDto){
        return new Activity(activityId, activityDto.getName(), activityDto.getEmployees());
    }
}
