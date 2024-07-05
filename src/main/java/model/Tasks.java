package model;


import java.util.Objects;

public class Tasks {

    private Long  id;
    private String title;
    private String status;
    private Employee employee;

    public Tasks() {}

    public Tasks(Long id, String title, String status) {
        this.id = id;
        this.title = title;
        this.status = status;
    }

    public Tasks(Long id, String title, String status, Employee employee) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tasks tasks = (Tasks) o;
        return Objects.equals(getId(), tasks.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Tasks{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", employee=" + employee +
                '}';
    }
}
