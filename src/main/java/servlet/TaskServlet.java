package servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.TasksDto;
import service.TasksService;
import service.impl.TaskServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/task/*")
public class TaskServlet extends HttpServlet {
    private final TasksService tasksService = TaskServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if(pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing employee id");
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                List<TasksDto> tasks = tasksService.getTasksByEmployeeId(id);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getOutputStream(), tasks);
            }
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tasks Not Found");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            TasksDto tasksDto = objectMapper.readValue(req.getInputStream(), TasksDto.class);
            long id = Long.parseLong(req.getParameter("employeeId"));
            TasksDto newTasks = tasksService.addTasks(tasksDto, id);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), newTasks);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tasks Already Exists");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing task id");
                return;
            }
            long id = Long.parseLong(pathInfo.substring(1));
            TasksDto tasksDto = objectMapper.readValue(req.getInputStream(), TasksDto.class);
            TasksDto newTasks = tasksService.updateTasks(tasksDto, id);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), newTasks);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tasks Not Found");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing task id");
                return;
            }
            long id = Long.parseLong(pathInfo.substring(1));
            tasksService.deleteTasks(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Tasks Not Found");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }
}
