package model.dto;

import java.util.Objects;

public class TasksDto {

    private String firstName;
    private String lastName;
    private String title;
    private String status;

    public TasksDto() {}

    public TasksDto(String firstName, String lastName, String title, String status) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.status = status;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TasksDto tasksDto = (TasksDto) o;
        return Objects.equals(getFirstName(), tasksDto.getFirstName()) && Objects.equals(getLastName(), tasksDto.getLastName()) && Objects.equals(getTitle(), tasksDto.getTitle()) && Objects.equals(getStatus(), tasksDto.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getTitle(), getStatus());
    }

    @Override
    public String toString() {
        return "TasksDto{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", title='" + title + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}
