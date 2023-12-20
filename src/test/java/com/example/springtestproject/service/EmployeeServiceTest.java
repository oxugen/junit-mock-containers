package com.example.springtestproject.service;

import com.example.springtestproject.exception.ResourceNotFoundException;
import com.example.springtestproject.model.Employee;
import com.example.springtestproject.repository.EmployeeRepository;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;

    private Employee employee2;

    @BeforeEach
    public void setup(){
        employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Cena")
                .email("dubrovskay.7830@mail.ru")
                .build();

        employee2 = Employee.builder()
                .id(2L)
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .email("dubrovskay.7830@mail.ru")
                .build();

    }

    @DisplayName("saveEmployeeMethod")
    @Test
    public void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObject(){
        //given
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.empty());
        given(employeeRepository.save(employee)).willReturn(employee);

        //when
        Employee savedEmployee = employeeService.saveEmployee(employee);

        assertThat(savedEmployee).isNotNull();
    }

    @DisplayName("ThrowEmployeeMethod")
    @Test
    public void givenExistingEmail_whenSaveEmployee_thenThrowException(){
        //given
        given(employeeRepository.findByEmail(employee.getEmail()))
                .willReturn(Optional.of(employee));

        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.saveEmployee(employee);
        });
        //then
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @DisplayName("GetAllEmployee")
    @Test
    public void givenEmployeesList_whenGetAllEmployees_thenReturnEmployeesList(){
        //given
        given(employeeRepository.findAll())
                .willReturn(List.of(employee, employee2));

        //when
        List<Employee> employeeList = employeeService.getAllEmployee();

        assertThat(employeeList).isEqualTo(List.of(employee, employee2));
    }

    @DisplayName("GetEmptyListOfEmployee")
    @Test
    public void givenEmployeesEmptyList_whenGetAllEmployees_thenReturnEmptyList(){
        //given
        given(employeeRepository.findAll())
                .willReturn(List.of());

        //when
        List<Employee> employeeList = employeeService.getAllEmployee();

        assertThat(employeeList).isEmpty();
    }

    @DisplayName("GetEmployeeById")
    @Test
    public void givenEmployeesId_whenGetEmployeeById_thenReturnEmployee(){
        //given
        given(employeeRepository.findById(1L))
                .willReturn(Optional.of(employee));

        //when
        Employee savedEmployee = employeeService.getEmployeeById(employee.getId()).get();

        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee).isEqualTo(employee);
    }

    @DisplayName("UpdateEmployee")
    @Test
    public void givenEmployee_whenUpdateEmployee_thenReturnUpdateEmployee(){
        //given
        given(employeeRepository.save(employee))
                .willReturn(employee);
        employee.setEmail("test@mail.ru");
        employee.setFirstName("Philip");
        //when
        Employee employeeToUpdate = employeeService.updateEmployee(employee);

        assertThat(employeeToUpdate.getFirstName()).isEqualTo("Philip");
        assertThat(employeeToUpdate.getEmail()).isEqualTo("test@mail.ru");
    }


    @DisplayName("DeleteEmployee")
    @Test
    public void givenEmployee_whenDeleteEmployee_thenNothing(){
        //given
        long employeeId = 1L;
        willDoNothing().given(employeeRepository).deleteById(1L);

        //when
        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository, times(1)).deleteById(employeeId);
    }
}

