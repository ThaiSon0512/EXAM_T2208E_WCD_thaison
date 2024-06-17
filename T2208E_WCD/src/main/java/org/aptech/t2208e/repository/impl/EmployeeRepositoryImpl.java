package org.aptech.t2208e.repository.impl;

import org.aptech.t2208e.dto.EmployeeDto;
import org.aptech.t2208e.entity.Employee;
import org.aptech.t2208e.repository.EmployeeRepository;
import org.aptech.t2208e.utils.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepositoryImpl implements EmployeeRepository {
    private static final String SQL_QUERY_CREATE_EMPLOYEE = "INSERT INTO Employees (FullName, Birthday, Address, Position, Department) VALUES (?, ?, ?, ?, ?);";
    private static final String SQL_FIND_ALL_EMPLOYEES = "SELECT * FROM Employees";
    ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        try (Connection con = connectionPool.getConnection();
             PreparedStatement pt = con.prepareStatement(SQL_FIND_ALL_EMPLOYEES);
             ResultSet rs = pt.executeQuery()) {

            while (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getLong("ID"));
                employee.setFullName(rs.getString("FullName"));
                employee.setBirthday(rs.getDate("Birthday"));
                employee.setAddress(rs.getString("Address"));
                employee.setPosition(rs.getString("Position"));
                employee.setDepartment(rs.getString("Department"));
                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all employees", e);
        }
        return employees;
    }

    @Override
    public Optional<Employee> createEmployee(EmployeeDto employeeDto) {
        try (Connection con = connectionPool.getConnection();
             PreparedStatement pt = con.prepareStatement(SQL_QUERY_CREATE_EMPLOYEE, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pt.setString(1, employeeDto.getFullName());
            pt.setDate(2, new Date(employeeDto.getBirthday().getTime()));
            pt.setString(3, employeeDto.getAddress());
            pt.setString(4, employeeDto.getPosition());
            pt.setString(5, employeeDto.getDepartment());

            int rowsAffected = pt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = pt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long generatedId = rs.getLong(1);
                        Employee employee = new Employee();
                        employee.setId(generatedId);
                        employee.setFullName(employeeDto.getFullName());
                        employee.setBirthday(employeeDto.getBirthday());
                        employee.setAddress(employeeDto.getAddress());
                        employee.setPosition(employeeDto.getPosition());
                        employee.setDepartment(employeeDto.getDepartment());

                        return Optional.of(employee);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating employee", e);
        }
        return Optional.empty();
    }
}
