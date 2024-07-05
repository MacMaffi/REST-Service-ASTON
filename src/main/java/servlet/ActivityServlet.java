package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.ActivityDto;
import service.ActivityService;
import service.impl.ActivityServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/activity/*")
public class ActivityServlet  extends HttpServlet {
    private final ActivityService activityService = ActivityServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                List<ActivityDto> activities = activityService.getAllActivities();
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getOutputStream(), activities);
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                ActivityDto activity = activityService.getActivityById(id);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getOutputStream(), activity);
            }
        } catch (IllegalArgumentException e) {
           resp.sendError(404, "Employee Not Found");
        } catch (SQLException e){
            resp.sendError(500, "Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            ActivityDto activityDto = objectMapper.readValue(req.getInputStream(), ActivityDto.class);
            ActivityDto newActivity = activityService.addActivity(activityDto);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), newActivity);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Activity Already Exists");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Missing activity id");
                return;
            }
            long id = Long.parseLong(pathInfo.substring(1));
            ActivityDto activityDto = objectMapper.readValue(req.getInputStream(), ActivityDto.class);
            ActivityDto updatedActivity = activityService.updateActivity(activityDto, id);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), updatedActivity);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Activity Not Found");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST,"Missing activity id");
                return;
            }
            long id = Long.parseLong(pathInfo.substring(1));
            activityService.deleteActivity(id);
            resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NO_CONTENT, "Activity Not Found");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }
}
