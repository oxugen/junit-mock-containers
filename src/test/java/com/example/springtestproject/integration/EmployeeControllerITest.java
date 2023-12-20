package com.example.springtestproject.integration;

import com.example.springtestproject.model.Employee;
import com.example.springtestproject.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerITest extends AbstractionBaseTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        employeeRepository.deleteAll();
    }

    @DisplayName("SaveEmployeeControllerTest")
    @Test
    public void givenEmployeeObject_whenCreateEmployee_ThenReturnSavedEmployee() throws Exception {
        System.out.println(String.format("Container name $s, $s, $s",
                MY_SQL_CONTAINER.getDatabaseName(),
                MY_SQL_CONTAINER.getUsername(),
                MY_SQL_CONTAINER.getPassword()));
        //given
        Employee employee = Employee.builder()
                .id(1L)
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();
        //when
        ResultActions response = mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employee)));
        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName",
                        is(employee.getFirstName())))
                .andExpect(jsonPath("$.lastName",
                        is(employee.getLastName())))
                .andExpect(jsonPath("$.email",
                        is(employee.getEmail())));
    }

    @DisplayName("GetAllEmployees")
    @Test
    public void givenListOfEmployees_whenGetAllEmployees_ThenReturnEmployeesList() throws Exception {
        //given
        Employee employee = Employee.builder()
                .id(1L)
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();
        Employee employee2 = Employee.builder()
                .id(2L)
                .email("test@mail.ru")
                .firstName("John")
                .lastName("Cena")
                .build();

        employeeRepository.saveAll(List.of(employee, employee2));
        //when
        ResultActions response = mockMvc.perform(get("/api/employees"));
        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        is(List.of(employee, employee2).size())));
    }

    @DisplayName("GetEmployeeByIdPositiveScenario")
    @Test
    public void givenEmployeeId_whenGetEmployeeById_thenEmployeeObject() throws Exception {
        Employee employee = Employee.builder()
                .id(1L)
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();
        employeeRepository.save(employee);
        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employee.getId()));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
    }

    @DisplayName("GetEmployeeByIdNegativeScenario")
    @Test
    public void givenNotExistEmployeeId_whenGetEmployeeByNotExistId_thenReturnNotFound() throws Exception {
        long employeeId = 2L;

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }


    @DisplayName("UpdateEmployee")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdateEmployeeObject() throws Exception {
        Employee savedEmployee = Employee.builder()
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();
        Employee updatedEmployee = Employee.builder()
                .email("test@mail.ru")
                .firstName("John")
                .lastName("Cena")
                .build();
        employeeRepository.save(savedEmployee);
        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
                .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
    }

    @DisplayName("UpdateEmployeeNegativeScenario")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnNotFound() throws Exception {
        Employee savedEmployee = Employee.builder()
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();
        Employee updatedEmployee = Employee.builder()
                .email("test@mail.ru")
                .firstName("John")
                .lastName("Cena")
                .build();

        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", savedEmployee.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("DeleteEmployeeById")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnOk() throws Exception {
        Employee savedEmployee = Employee.builder()
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();

        employeeRepository.save(savedEmployee);

        //when
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId()));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

}
