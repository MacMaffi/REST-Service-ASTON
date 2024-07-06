package testservlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.ActivityDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.ActivityService;
import servlet.ActivityServlet;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ActivityServletTest {
    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private ActivityServlet activityServlet;

    @Mock
    private ActivityService activityService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Test
    void doPost_whenNormal_returnActivityDto() throws IOException, SQLException {
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final String responseBody = mapper.writeValueAsString(activityDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(activityDto)
                    .when(activityService).addActivity(activityDto);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            activityServlet.doPost(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .addActivity(activityDto);
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void doPost_whenActivityAlreadyExists_throwIllegalArgumentException() throws IOException, SQLException {
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final String responseBody = mapper.writeValueAsString(activityDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new IllegalArgumentException())
                    .when(activityService).addActivity(activityDto);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(409, "Activity Already Exists");

            activityServlet.doPost(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .addActivity(activityDto);
        Mockito
                .verify(response, Mockito.never())
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void doPost_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final String responseBody = mapper.writeValueAsString(activityDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new SQLException())
                    .when(activityService).addActivity(activityDto);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(500, "Server Error");

            activityServlet.doPost(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .addActivity(activityDto);
        Mockito
                .verify(response, Mockito.never())
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void updateActivity_whenNormal_thenReturnActivity() throws IOException, SQLException {
        final long activityId = 1;
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final String responseBody = mapper.writeValueAsString(activityDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn("/activity/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/activity")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(activityDto)
                    .when(activityService)
                    .updateActivity(activityDto, activityId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            activityServlet.doPut(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .updateActivity(activityDto, activityId);
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
    void updateActivity_whenURIInvalid_thenErrorResponse() throws IOException, SQLException {
        final long activityId = 1;
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final String responseBody = mapper.writeValueAsString(activityDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/activity")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/activity")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(bf).when(request)
                    .getReader();
            Mockito
                    .doNothing().when(response)
                    .sendError(406, "Activity Id Invalid");

            activityServlet.doPut(request, response);
        }

        Mockito
                .verify(activityService, Mockito.never())
                .updateActivity(activityDto, activityId);
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(406, "Activity Id Invalid");
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
    void updateActivity_whenActivityNotFound_throwException() throws IOException, SQLException {
        final long activityId = 1;
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final String responseBody = mapper.writeValueAsString(activityDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/activity/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/activity")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new IllegalArgumentException("Activity Not Found"))
                    .when(activityService).updateActivity(activityDto, activityId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(404, "Activity Not Found");

            activityServlet.doPut(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .updateActivity(activityDto, activityId);
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
    void updateActivity_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long activityId = 1;
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final String responseBody = mapper.writeValueAsString(activityDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/activity/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/activity")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new SQLException())
                    .when(activityService).updateActivity(activityDto, activityId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing().when(response)
                    .sendError(500, "Server Error");

            activityServlet.doPut(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .updateActivity(activityDto, activityId);
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
    void deleteActivity_whenNormal_thenDeleteActivity() throws SQLException, IOException {
        final long activityId = 1;
        Mockito
                .doNothing()
                .when(activityService).deleteActivity(activityId);
        Mockito
                .doReturn("/activity/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();

        activityServlet.doDelete(request, response);

        Mockito
                .verify(activityService, Mockito.times(1))
                .deleteActivity(activityId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
    }

    @Test
    void deleteActivity_whenActivityNotFound_throwIllegalArgumentException() throws SQLException, IOException {
        final long activityId = 1;
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(activityService).deleteActivity(activityId);
        Mockito
                .doReturn("/activity/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(404, "Activity Not Found");

        activityServlet.doDelete(request, response);

        Mockito
                .verify(activityService, Mockito.times(1))
                .deleteActivity(activityId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(404, "Activity Not Found");
    }

    @Test
    void deleteActivity_whenDatabaseError_throwSQLException() throws SQLException, IOException {
        final long activityId = 1;
        Mockito
                .doThrow(new SQLException())
                .when(activityService).deleteActivity(activityId);
        Mockito
                .doReturn("/activity1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");

        activityServlet.doDelete(request, response);

        Mockito
                .verify(activityService, Mockito.times(1))
                .deleteActivity(activityId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(500, "Server Error");
    }

    @Test
    void deleteActivity_whenURIInvalid_thenResponseError() throws IOException {
        Mockito
                .doNothing().when(response)
                .sendError(406, "Activity Id Invalid");
        Mockito
                .doReturn("/activity")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();

        activityServlet.doDelete(request, response);

        Mockito
                .verify(request, Mockito.times(1))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(406, "Activity Id Invalid");
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
    }

    @Test
    void getActivityById_whenNormal_thenReturnActivityDto() throws IOException, SQLException {
        final long activityId = 1;
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        String responseBody = mapper.writeValueAsString(activityDto);

        Mockito
                .doReturn("/activity/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();
        Mockito
                .doReturn(activityDto)
                .when(activityService).getActivityById(activityId);

        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito.doReturn(pw).when(response).getWriter();
            activityServlet.doGet(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .getActivityById(activityId);
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
    void getActivities_whenNormal_thenReturnActivityDto() throws IOException, SQLException {
        final ActivityDto activityDto = new ActivityDto("Мафия", List.of());
        final ActivityDto activityDto1 = new ActivityDto("Футбол", List.of());
        String responseBody = mapper.writeValueAsString(List.of(activityDto, activityDto1));

        Mockito
                .doReturn("/activity")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();
        Mockito
                .doReturn(List.of(activityDto, activityDto1))
                .when(activityService).getAllActivities();

        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();
            activityServlet.doGet(request, response);
        }

        Mockito
                .verify(activityService, Mockito.times(1))
                .getAllActivities();
        Mockito
                .verify(request, Mockito.times(1))
                .getRequestURI();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
    }

    @Test
    void getActivityById_whenActivityNotFound_throwIllegalArgumentException() throws IOException, SQLException {
        final long activityId = 1;

        Mockito
                .doReturn("/activity/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(activityService).getActivityById(activityId);
        Mockito
                .doNothing().when(response)
                .sendError(404, "Activity Not Found");

        activityServlet.doGet(request, response);

        Mockito
                .verify(activityService, Mockito.times(1))
                .getActivityById(activityId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(404, "Activity Not Found");
    }

    @Test
    void getActivityById_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long activityId = 1;

        Mockito
                .doReturn("/activity/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/activity")
                .when(request).getServletPath();
        Mockito
                .doThrow(new SQLException())
                .when(activityService).getActivityById(activityId);
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");

        activityServlet.doGet(request, response);

        Mockito
                .verify(activityService, Mockito.times(1))
                .getActivityById(activityId);
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


