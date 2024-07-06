package testservlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.EmployeeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.EmployeeService;
import servlet.EmployeeServlet;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class EmployeeServletTest {
    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @InjectMocks
    private EmployeeServlet employeeServlet;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpServletRequest request;

    @Test
    void doPost_whenNormal_returnEmployeeDto() throws IOException, SQLException {
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        final String responseBody = mapper.writeValueAsString(employeeDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn(employeeDto)
                    .when(employeeService).addEmployee(employeeDto, List.of());
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            employeeServlet.doPost(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.times(1))
                .addEmployee(employeeDto, List.of());
        Mockito
                .verify(response, Mockito.times(1))
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void doPost_whenRepeatName_throwIllegalArgumentException() throws IOException, SQLException {
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        final String responseBody = mapper.writeValueAsString(employeeDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new IllegalArgumentException())
                    .when(employeeService).addEmployee(employeeDto, List.of());
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(409, "Employee Already Exists");

            employeeServlet.doPost(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.times(1))
                .addEmployee(employeeDto, List.of());
        Mockito
                .verify(response, Mockito.never())
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void doPost_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        final String responseBody = mapper.writeValueAsString(employeeDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doThrow(new SQLException())
                    .when(employeeService).addEmployee(employeeDto, List.of());
            Mockito
                    .doReturn(bf).when(request)
                    .getReader();
            Mockito
                    .doNothing().when(response)
                    .sendError(500, "Server Error");

            employeeServlet.doPost(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.times(1))
                .addEmployee(employeeDto, List.of());
        Mockito
                .verify(response, Mockito.never())
                .getWriter();
        Mockito
                .verify(request, Mockito.times(1))
                .getReader();
    }

    @Test
    void updateEmployee_whenNormal_thenReturnEmployee() throws IOException, SQLException {
        final long employeeId = 1;
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        final String responseBody = mapper.writeValueAsString(employeeDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody));
             PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito
                    .doReturn("/employee/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/employee")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(employeeDto)
                    .when(employeeService).updateEmployee(employeeDto, employeeId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doReturn(pw)
                    .when(response).getWriter();

            employeeServlet.doPut(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.times(1))
                .updateEmployee(employeeDto, employeeId);
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
    void updateEmployee_whenURIInvalid_thenErrorResponse() throws IOException, SQLException {
        final long employeeId = 1;
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        final String responseBody = mapper.writeValueAsString(employeeDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/employee")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/employee")
                    .when(request).getServletPath();
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing()
                    .when(response).sendError(406, "Employee Id Invalid");

            employeeServlet.doPut(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.never())
                .updateEmployee(employeeDto, employeeId);
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(406, "Employee Id Invalid");
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
    void updateEmployee_whenEmployeeNotFound_throwException() throws IOException, SQLException {
        final long employeeId = 1;
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        final String responseBody = mapper.writeValueAsString(employeeDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/employee/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/employee")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new IllegalArgumentException("Employee Not Found"))
                    .when(employeeService).updateEmployee(employeeDto, employeeId);
            Mockito
                    .doReturn(bf)
                    .when(request).getReader();
            Mockito
                    .doNothing().when(response)
                    .sendError(404, "Employee Not Found");

            employeeServlet.doPut(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.times(1))
                .updateEmployee(employeeDto, employeeId);
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
    void updateEmployee_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long employeeId = 1;
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        final String responseBody = mapper.writeValueAsString(employeeDto);

        try (BufferedReader bf = new BufferedReader(new StringReader(responseBody))) {
            Mockito
                    .doReturn("/employee/1")
                    .when(request).getRequestURI();
            Mockito
                    .doReturn("/employee")
                    .when(request).getServletPath();
            Mockito
                    .doThrow(new SQLException())
                    .when(employeeService).updateEmployee(employeeDto, employeeId);
            Mockito
                    .doReturn(bf).when(request)
                    .getReader();
            Mockito
                    .doNothing().when(response)
                    .sendError(500, "Server Error");

            employeeServlet.doPut(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.times(1))
                .updateEmployee(employeeDto, employeeId);
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
    void deleteEmployee_whenNormal_thenDeleteEmployee() throws SQLException, IOException {
        final long employeeId = 1;
        Mockito
                .doNothing()
                .when(employeeService).deleteEmployee(employeeId);
        Mockito
                .doReturn("/employee/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/employee")
                .when(request).getServletPath();

        employeeServlet.doDelete(request, response);

        Mockito
                .verify(employeeService, Mockito.times(1))
                .deleteEmployee(employeeId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
    }

    @Test
    void deleteEmployee_whenEmployeeNotFound_throwIllegalArgumentException() throws SQLException, IOException {
        final long employeeId = 1;
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(employeeService).deleteEmployee(employeeId);
        Mockito
                .doReturn("/employee/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/employee")
                .when(request).getServletPath();
        Mockito
                .doNothing().when(response)
                .sendError(404, "Employee Not Found");

        employeeServlet.doDelete(request, response);

        Mockito
                .verify(employeeService, Mockito.times(1))
                .deleteEmployee(employeeId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(404, "Employee Not Found");
    }

    @Test
    void deleteEmployee_whenDatabaseError_throwSQLException() throws SQLException, IOException {
        final long employeeId = 1;
        Mockito
                .doThrow(new SQLException()).when(employeeService)
                .deleteEmployee(employeeId);
        Mockito
                .doReturn("/employee/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/employee")
                .when(request).getServletPath();
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");

        employeeServlet.doDelete(request, response);

        Mockito
                .verify(employeeService, Mockito.times(1))
                .deleteEmployee(employeeId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(500, "Server Error");
    }

    @Test
    void deleteEmployee_whenURIInvalid_thenResponseError() throws IOException {
        Mockito
                .doNothing().when(response)
                .sendError(406, "Employee Id Invalid");
        Mockito
                .doReturn("/employee")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/employee")
                .when(request).getServletPath();

        employeeServlet.doDelete(request, response);

        Mockito
                .verify(request, Mockito.times(1))
                .getRequestURI();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(406, "Employee Id Invalid");
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
    }

    @Test
    void getEmployeeById_whenNormal_thenReturnEmployeeDto() throws IOException, SQLException {
        final long employeeId = 1;
        final EmployeeDto employeeDto = new EmployeeDto("Иван", "Петров", "Разработчик", List.of(), List.of());
        String responseBody = mapper.writeValueAsString(employeeDto);

        Mockito
                .doReturn("/employee/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/employee")
                .when(request).getServletPath();
        Mockito
                .doReturn(employeeDto)
                .when(employeeService).getEmployeeById(employeeId);

        try (PrintWriter pw = new PrintWriter(new StringWriter().append(responseBody))) {
            Mockito.doReturn(pw).when(response).getWriter();
            employeeServlet.doGet(request, response);
        }

        Mockito
                .verify(employeeService, Mockito.times(1))
                .getEmployeeById(employeeId);
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
    void getEmployeeById_whenEmployeeNotFound_throwIllegalArgumentException() throws IOException, SQLException {
        final long employeeId = 1;

        Mockito
                .doReturn("/employee/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/employee")
                .when(request).getServletPath();
        Mockito
                .doThrow(new IllegalArgumentException())
                .when(employeeService).getEmployeeById(employeeId);
        Mockito
                .doNothing()
                .when(response).sendError(404, "Employee Not Found");

        employeeServlet.doGet(request, response);

        Mockito
                .verify(employeeService, Mockito.times(1))
                .getEmployeeById(employeeId);
        Mockito
                .verify(request, Mockito.times(3))
                .getRequestURI();
        Mockito
                .verify(request, Mockito.times(1))
                .getServletPath();
        Mockito
                .verify(response, Mockito.times(1))
                .sendError(404, "Employee Not Found");
    }

    @Test
    void getEmployeeById_whenDatabaseError_throwSQLException() throws IOException, SQLException {
        final long employeeId = 1;

        Mockito
                .doReturn("/employee/1")
                .when(request).getRequestURI();
        Mockito
                .doReturn("/employee")
                .when(request).getServletPath();
        Mockito
                .doThrow(new SQLException())
                .when(employeeService).getEmployeeById(employeeId);
        Mockito
                .doNothing()
                .when(response).sendError(500, "Server Error");

        employeeServlet.doGet(request, response);

        Mockito
                .verify(employeeService, Mockito.times(1))
                .getEmployeeById(employeeId);
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


