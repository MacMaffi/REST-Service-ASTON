package testservlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.TasksDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.TasksService;
import servlet.TaskServlet;

import java.io.*;
import java.sql.SQLException;

@ExtendWith(MockitoExtension.class)
public class TasksServletTest {

    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private TaskServlet tasksServlet;

    @Mock
    private TasksService taskService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Test
    void doPost_whenNormal_returnTaskDto() throws IOException, SQLException {
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        final String responseBody = mapper.writeValueAsString(tasksDto);
        final long employeeId = 1L;

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(tasksDto)
                    .when(taskService).addTasks(tasksDto, employeeId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            tasksServlet.doPost(request, response);
        }

        Mockito
                .verify(taskService, Mockito.times(1))
                .addTasks(tasksDto, employeeId);
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void doPost_whenTaskAlreadyExists_throwIllegalArgumentException() throws IOException, SQLException {
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        final String responseBody = mapper.writeValueAsString(tasksDto);
        final long employeeId = 1L;

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new IllegalArgumentException())
                    .when(taskService).addTasks(tasksDto, employeeId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(409, "Task Already Exists");

            tasksServlet.doPost(request, response);
        }

        Mockito
                .verify(taskService, Mockito.times(1))
                .addTasks(tasksDto, employeeId);
        Mockito
                .verify(response, Mockito.never())
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void doPost_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        final String responseBody = mapper.writeValueAsString(tasksDto);
        final long employeeId = 1L;

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new SQLException())
                    .when(taskService).addTasks(tasksDto, employeeId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");

            tasksServlet.doPost(request, response);
        }

        Mockito
                .verify(taskService, Mockito.times(1))
                .addTasks(tasksDto, employeeId);
        Mockito
                .verify(response, Mockito.never())
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void updateTask_whenNormal_thenReturnTask() throws IOException, SQLException {
        final long taskId = 1;
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        final String responseBody = mapper.writeValueAsString(tasksDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn("/task/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/task")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(tasksDto)
                    .when(taskService).updateTasks(tasksDto, taskId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            tasksServlet.doPut(request, response);
        }

        Mockito
                .verify(taskService, Mockito.times(1))
                .updateTasks(tasksDto, taskId);
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
    }

    @Test
    void updateTask_whenURIInvalid_thenErrorResponse() throws IOException, SQLException {
        final long taskId = 1;
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        final String responseBody = mapper.writeValueAsString(tasksDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/task")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/task")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(bf).when(request)
                    .getReader();
            Mockito
                    .doNothing().when(response)
                    .sendError(406, "Task Id Invalid");

            tasksServlet.doPut(request, response);
        }

        Mockito
                .verify(taskService, Mockito.never())
                .updateTasks(tasksDto, taskId);
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(406, "Task Id Invalid");
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(request, Mockito.times(1))
                .getRequestURI();
    }

    @Test
    void updateTask_whenTaskNotFound_throwException() throws IOException, SQLException {
        final long taskId = 1;
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        final String responseBody = mapper.writeValueAsString(tasksDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/task/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/task")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new IllegalArgumentException("Task Not Found"))
                    .when(taskService).updateTasks(tasksDto, taskId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(404, "Task Not Found");

            tasksServlet.doPut(request, response);
        }

        Mockito
                .verify(taskService, Mockito.times(1))
                .updateTasks(tasksDto, taskId);
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
    }

    @Test
    void updateTask_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long taskId = 1;
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        final String responseBody = mapper.writeValueAsString(tasksDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/task/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/task")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new SQLException())
                    .when(taskService).updateTasks(tasksDto, taskId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");

            tasksServlet.doPut(request, response);
        }

        Mockito
                .verify(taskService, Mockito.times(1))
                .updateTasks(tasksDto, taskId);
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
    }

    @Test
    void deleteTask_whenNormal_thenDeleteTask() throws SQLException, IOException {
        final long taskId = 1;
        Mockito
                .doNothing()
                .when(taskService).deleteTasks(taskId);
        Mockito
                .doReturn("/task/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/task")
                .when(request).getServletPath();

        tasksServlet.doDelete(request, response);

        Mockito
                .verify(taskService, Mockito.times(1))
                .deleteTasks(taskId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
    }

    @Test
    void deleteTask_whenTaskNotFound_throwIllegalArgumentException() throws SQLException, IOException {
        final long taskId = 1;
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(taskService).deleteTasks(taskId);
        Mockito
                .doReturn("/task/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/task")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(404, "Task Not Found");

        tasksServlet.doDelete(request, response);

        Mockito
                .verify(taskService, Mockito.times(1))
                .deleteTasks(taskId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(404, "Task Not Found");
    }

    @Test
    void deleteTask_whenDatabaseError_throwSQLException() throws SQLException, IOException {
        final long taskId = 1;
        Mockito
                .doThrow(new SQLException())
                .when(taskService).deleteTasks(taskId);
        Mockito
                .doReturn("/task/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/task")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");

        tasksServlet.doDelete(request, response);

        Mockito
                .verify(taskService, Mockito.times(1))
                .deleteTasks(taskId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(500, "Server Error");
    }

    @Test
    void deleteTask_whenURIInvalid_thenResponseError() throws IOException {
        Mockito
                .doNothing()
                .when(response).sendError(406, "Task Id Invalid");
        Mockito
                .doReturn("/task")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/task")
                .when(request).getServletPath();

        tasksServlet.doDelete(request, response);

        Mockito
                .verify(request, Mockito.times(1))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(406, "Task Id Invalid");
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
    }

    @Test
    void getTaskById_whenNormal_thenReturnTaskDto() throws IOException, SQLException {
        final long employeeId = 1;
        final TasksDto tasksDto = new TasksDto("Иван", "Иванов", "Задача", "На рассмотрении");
        String responseBody = mapper.writeValueAsString(tasksDto);

        Mockito
                .doReturn("/task/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/task")
                .when(request).getServletPath();
        Mockito
                .doReturn(tasksDto)
                .when(taskService).getTasksByEmployeeId(employeeId);

        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();
            tasksServlet.doGet(request, response);
        }

        Mockito
                .verify(taskService, Mockito.times(1))
                .getTasksByEmployeeId(employeeId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
    }


    @Test
    void getTaskById_whenTaskNotFound_throwIllegalArgumentException() throws IOException, SQLException {
        final long employeeId = 1;

        Mockito
                .doReturn("/task/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/task")
                .when(request).getServletPath();
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(taskService).getTasksByEmployeeId(employeeId);
        Mockito
                .doNothing()
                .when(response).sendError(404, "Task Not Found");

        tasksServlet.doGet(request, response);

        Mockito
                .verify(taskService, Mockito.times(1))
                .getTasksByEmployeeId(employeeId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(404, "Task Not Found");
    }

    @Test
    void getTaskById_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long employeeId = 1;

        Mockito
                .doReturn("/task/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/task")
                .when(request).getServletPath();
        Mockito
                .doThrow(new SQLException())
                .when(taskService).getTasksByEmployeeId(employeeId);
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");

        tasksServlet.doGet(request, response);

        Mockito
                .verify(taskService, Mockito.times(1))
                .getTasksByEmployeeId(employeeId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(500, "Server Error");
    }
}


