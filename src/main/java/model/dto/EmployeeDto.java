package model.dto;

import java.util.List;
import java.util.Objects;

public class EmployeeDto {
    private String firstName;
    private String lastName;
    private String position;
    private List<Long> activity;
    private List<TasksDto> tasks;

    public EmployeeDto(){
    }

    public EmployeeDto(String firstName, String lastName, String position, List<Long> activity, List<TasksDto> tasks) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.activity = activity;
        this.tasks = tasks;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public List<Long> getActivity() {
        return activity;
    }

    public void setActivity(List<Long> activity) {
        this.activity = activity;
    }

    public List<TasksDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TasksDto> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeDto that = (EmployeeDto) o;
        return Objects.equals(getFirstName(), that.getFirstName()) && Objects.equals(getLastName(), that.getLastName()) && Objects.equals(getPosition(), that.getPosition()) && Objects.equals(getActivity(), that.getActivity()) && Objects.equals(getTasks(), that.getTasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getPosition(), getActivity(), getTasks());
    }

    @Override
    public String toString() {
        return "EmployeeDto{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", position='" + position + '\'' +
               ", activity=" + activity +
               ", tasks=" + tasks +
               '}';
    }
}
