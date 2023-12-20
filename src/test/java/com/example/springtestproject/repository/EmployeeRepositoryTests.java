package com.example.springtestproject.repository;

import com.example.springtestproject.model.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EmployeeRepositoryTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    public void setup(){
        employee= Employee.builder()
                .firstName("John")
                .lastName("Cena")
                .email("cena@mail.ru")
                .build();
    }

    @DisplayName("Test for save employee operation")
    @Test
    public void givenEmployeeObject_whenSave_thenReturnSavedEmployee(){
        //when - action or behaviour that we are going to test
        Employee savedEmployee = employeeRepository.save(employee);
        //then - output
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isGreaterThan(0);
    }

    @DisplayName("Test for get all employee operation")
    @Test
    public void givenEmployeesList_whenFindAll_thenEmployeesList(){

        Employee employeeSecond = Employee.builder()
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .email("dubrovskay.7830@mail.ru")
                .build();
        //when - action or behaviour that we are going to test
        employeeRepository.save(employee);
        employeeRepository.save(employeeSecond);

        List<Employee> employeeList = employeeRepository.findAll();

        //then
        assertThat(employeeList).isNotNull();
        assertThat(employeeList.size()).isEqualTo(2);
    }

    @DisplayName("Test for get employee by id")
    @Test
    public void givenEmployeeObject_whenFindById_thenReturnEmployeeObject(){
        employeeRepository.save(employee);

        //when - action or behaviour that we are going to test
        Employee employeeDb = employeeRepository.findById(employee.getId()).get();

        //then
        assertThat(employeeDb).isNotNull();
    }

    @DisplayName("Test for update employee")
    @Test
    public void givenEmployeeObject_whenUpdateEmployee_thenReturnUpdatedEmployee(){
        employeeRepository.save(employee);

        //when - action or behaviour that we are going to test
        Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
        savedEmployee.setEmail("new@mail.ru");
        savedEmployee.setFirstName("Phil");

        Employee updateEmployee = employeeRepository.save(savedEmployee);
        //then
        assertThat(updateEmployee).isNotNull();
        assertThat(updateEmployee.getId()).isEqualTo(employee.getId());
        assertThat(updateEmployee.getEmail()).isEqualTo("new@mail.ru");
        assertThat(updateEmployee.getFirstName()).isEqualTo("Phil");
    }

    @DisplayName("Test for delete employee by id")
    @Test
    public void givenEmployeeObject_whenDelete_thenRemoveEmployee(){
        employeeRepository.save(employee);

        //when - action or behaviour that we are going to test
        employeeRepository.delete(employee);
        Optional<Employee> employeeOptional = employeeRepository.findById(employee.getId());
        //then
        assertThat(employeeOptional).isEmpty();
    }

    @DisplayName("Test for custom query using jpql")
    @Test
    public void givenFirstNameAndLastName_whenFindByJQPL_thenEmployeeObject(){
        employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Cena";

        Employee savedEmployee = employeeRepository.findByJPQL(firstName, lastName);

        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("Test for custom query using jpql named params")
    @Test
    public void givenFirstNameAndLastName_whenFindByJQPLNamedParams_thenEmployeeObject(){
        employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Cena";

        Employee savedEmployee = employeeRepository.findByJPQLNamedParams(firstName, lastName);

        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("Test for custom query using native sql")
    @Test
    public void givenFirstNameAndLastName_whenFindBySQL_thenEmployeeObject(){
        employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Cena";

        Employee savedEmployee = employeeRepository.findByNativeSQL(firstName, lastName);

        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("Test for custom query using native sql named")
    @Test
    public void givenFirstNameAndLastName_whenFindBySQLNamed_thenEmployeeObject(){
        employeeRepository.save(employee);
        String firstName = "John";
        String lastName = "Cena";

        Employee savedEmployee = employeeRepository.findByNativeSQLNamed(firstName, lastName);

        assertThat(savedEmployee).isNotNull();
    }


}
