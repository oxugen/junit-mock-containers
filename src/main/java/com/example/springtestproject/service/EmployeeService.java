package com.example.springtestproject.service;

import com.example.springtestproject.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee saveEmployee(Employee employee);
    List<Employee> getAllEmployee();
    Optional<Employee> getEmployeeById(long id);

    Employee updateEmployee(Employee employee);

    void deleteEmployee(long id);
}
