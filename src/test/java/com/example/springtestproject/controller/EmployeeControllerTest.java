package com.example.springtestproject.controller;

import com.example.springtestproject.model.Employee;
import com.example.springtestproject.service.EmployeeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.CoreMatchers.is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("SaveEmployeeControllerTest")
    @Test
    public void givenEmployeeObject_whenCreateEmployee_ThenReturnSavedEmployee() throws Exception {
        //given
        Employee employee = Employee.builder()
                .id(1L)
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();
        given(employeeService.saveEmployee(any(Employee.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
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

       given(employeeService.getAllEmployee())
                .willReturn(List.of(employee, employee2));
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
        long employeeId = 1L;
        Employee employee = Employee.builder()
                .id(1L)
                .email("dubrovskay.7830@mail.ru")
                .firstName("Philip")
                .lastName("Dubrovskiy")
                .build();
        given(employeeService.getEmployeeById(1L)).willReturn(Optional.of(employee));

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
    }

    @DisplayName("GetEmployeeByIdNegativeScenario")
    @Test
    public void givenNotExistEmployeeId_whenGetEmployeeByNotExistId_thenReturnNotFound() throws Exception {
        long employeeId = 2L;
        given(employeeService.getEmployeeById(2L)).willReturn(Optional.empty());

        ResultActions response = mockMvc.perform(get("/api/employees/{id}", employeeId));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("UpdateEmployee")
    @Test
    public void givenUpdatedEmployee_whenUpdateEmployee_thenReturnUpdateEmployeeObject() throws Exception {
        long employeeId = 1L;
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
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.of(savedEmployee));
        given(employeeService.updateEmployee(any(Employee.class))).willAnswer((invocation) -> invocation.getArgument(0));
        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
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
        long employeeId = 1L;
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
        given(employeeService.getEmployeeById(employeeId)).willReturn(Optional.empty());
        given(employeeService.updateEmployee(any(Employee.class))).willAnswer((invocation) -> invocation.getArgument(0));
        //when
        ResultActions response = mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)));

        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @DisplayName("DeleteEmployeeById")
    @Test
    public void givenEmployeeId_whenDeleteEmployee_thenReturnOk() throws Exception {
        long employeeId = 1L;
        willDoNothing().given(employeeService).deleteEmployee(employeeId);

        //when
        ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeId));

        //then
        response.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
