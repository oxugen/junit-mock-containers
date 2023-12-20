package com.example.springtestproject.service;

import com.example.springtestproject.exception.ResourceNotFoundException;
import com.example.springtestproject.model.Employee;
import com.example.springtestproject.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee saveEmployee(Employee employee) {
        Optional<Employee> savedEmployee= employeeRepository.findByEmail(employee.getEmail());
        if(savedEmployee.isPresent()){
            throw new ResourceNotFoundException("Employee already exists with given email" + employee.getEmail());
        }
        return employeeRepository.save(employee);
    }

    @Override
    public List<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getEmployeeById(long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee updateEmployee(Employee updateEmployee) {
        return employeeRepository.save(updateEmployee);
    }

    @Override
    public void deleteEmployee(long id) {
        Employee employeeToDelete = getEmployeeById(id).get();
        employeeRepository.delete(employeeToDelete);
    }
}
