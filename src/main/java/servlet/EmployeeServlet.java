package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dto.EmployeeDto;
import service.EmployeeService;
import service.impl.EmployeeServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@WebServlet("/employee/*")
public class EmployeeServlet  extends HttpServlet {
    private final EmployeeService employeeService = EmployeeServiceImpl.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing employee id");
            } else {
                long id = Long.parseLong(pathInfo.substring(1));
                EmployeeDto employee = employeeService.getEmployeeById(id);
                resp.setContentType("application/json");
                objectMapper.writeValue(resp.getOutputStream(), employee);
            }
        } catch (IllegalArgumentException e) {
           resp.sendError(404, "Employee Not Found");
        } catch (SQLException e){
            resp.sendError(500, "Server Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            EmployeeDto employeeDto = objectMapper.readValue(req.getInputStream(), EmployeeDto.class);
            List<Long> activityList = objectMapper.readValue(req.getParameter("activities"), List.class);
            EmployeeDto newEmployee = employeeService.addEmployee(employeeDto, activityList);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), newEmployee);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee Already Exists");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }

    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing employee id");
                return;
            }
            long id = Long.parseLong(pathInfo.substring(1));
            EmployeeDto employeeDto = objectMapper.readValue(req.getInputStream(), EmployeeDto.class);
            EmployeeDto updatedEmployee = employeeService.updateEmployee(employeeDto, id);
            resp.setContentType("application/json");
            objectMapper.writeValue(resp.getOutputStream(), updatedEmployee);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee Not Found");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing employee id");
                return;
            }
            long id = Long.parseLong(pathInfo.substring(1));
            employeeService.deleteEmployee(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee Not Found");
        } catch (SQLException e){
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server Error");
        }
    }
}
