package model.dto;


import model.Employee;

import java.util.List;
import java.util.Objects;

public class ActivityDto {

    private String name;
    private List<Employee> employees;

    public ActivityDto(){
    }

    public ActivityDto(String name, List<Employee> employees) {
        this.name = name;
        this.employees = employees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityDto that = (ActivityDto) o;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getEmployees(), that.getEmployees());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getEmployees());
    }

    @Override
    public String toString() {
        return "ActivityDto{" +
               "name='" + name + '\'' +
               ", employees=" + employees +
               '}';
    }
}
