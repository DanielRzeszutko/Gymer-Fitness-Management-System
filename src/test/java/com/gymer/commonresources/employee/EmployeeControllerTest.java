package com.gymer.commonresources.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.commonresources.employee.entity.Employee;
import com.gymer.commonresources.employee.entity.EmployeeDTO;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.partner.entity.Partner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private PartnerService partnerService;

    @InjectMocks
    private EmployeeController employeeController;

    private final Pageable pageable = PageRequest.of(0, 20);
    private final Page<Employee> page = getTestPageData();

    @Test
    public void contextLoads() {
        assertThat(employeeController).isNotNull();
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetPageWithRecords() throws Exception {
        Partner partner = new Partner("", "", "", "", "", null, null,
                page.getContent(), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);

        for (Employee employee : page.getContent()) {
            given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        }
        given(employeeService.findAllContaining(pageable, "")).willReturn(page);

        mockMvc.perform(get("/api/employees")
                .header("Origin", "*")
                .param("contains", "")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetPageWithRecordsWithoutSearchBy() throws Exception {
        Partner partner = new Partner("", "", "", "", "", null, null,
                page.getContent(), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);

        for (Employee employee : page.getContent()) {
            given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        }
        given(employeeService.getAllElements(pageable)).willReturn(page);

        mockMvc.perform(get("/api/employees")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnOKStatus_when_tryingToGetSpecificRecordWithId() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);

        given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        given(employeeService.getElementById(1L)).willReturn(employee);

        mockMvc.perform(get("/api/employees/1")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnOKStatus_when_tryingToGetAllEmployeesForSpecifiedPartner() throws Exception {
        Partner partner = new Partner("", "", "", "", "", null, null,
                page.getContent(), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);

        given(partnerService.getElementById(1L)).willReturn(partner);
        for (Employee employee : page.getContent()) {
            given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        }
        given(employeeService.findAllEmployeesForPartner(pageable, partner)).willReturn(page);

        mockMvc.perform(get("/api/partners/1/employees")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnBadRequest_when_tryingToGetNotValidEmployeeFromPartner() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(5L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                page.getContent(), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);
        Partner partner2 = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee), Collections.emptyList(), Collections.emptyList());
        partner.setId(2L);

        for (Employee emp : page.getContent()) {
            given(partnerService.findPartnerContainingEmployee(emp)).willReturn(partner);
        }
        given(partnerService.getElementById(1L)).willReturn(partner);
        given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner2);
        given(employeeService.findAllEmployeesForPartner(pageable, partner)).willReturn(page);

        mockMvc.perform(get("/api/partners/1/employees/5")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Ignore
    public void should_returnCreatedStatus_when_tryingToAddNewEmployeeWithValidDTO() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        EmployeeDTO employeeDTO = new EmployeeDTO(employee);

        given(partnerService.getElementById(1L)).willReturn(partner);
        doNothing().when(partnerService).updateElement(isA(Partner.class));

        mockMvc.perform(post("/api/partners/1/employees")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .accept("application/json")
                .content(objectMapper.writeValueAsString(employeeDTO))
                .header("Origin", "*"))
                .andExpect(status().isCreated());
    }

    @Test
    public void should_returnNoContentStatus_when_tryingToDeleteEmployeeWithValidId() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee), Collections.emptyList(), Collections.emptyList());

        given(partnerService.getElementById(1L)).willReturn(partner);
        given(employeeService.getElementById(1L)).willReturn(employee);

        mockMvc.perform(delete("/api/partners/1/employees/1")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .accept("application/json")
                .header("Origin", "*"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void should_returnBadRequestStatus_when_tryingToDeleteEmployeeWithInvalidId() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee), Collections.emptyList(), Collections.emptyList());

        given(partnerService.getElementById(1L)).willReturn(partner);
        given(employeeService.getElementById(1L)).willReturn(employee);

        mockMvc.perform(delete("/api/partners/1/employees/2")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .accept("application/json")
                .header("Origin", "*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnNotFoundStatus_when_tryingToGetSpecificRecordWithNonExistingId() throws Exception {
        given(employeeService.getElementById(-1L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/employees/-1")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetSpecificRecordForPartner() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);
        given(employeeService.getElementById(1L)).willReturn(employee);
        given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        given(partnerService.getElementById(1L)).willReturn(partner);

        mockMvc.perform(get("/api/partners/1/employees/1")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNoContentStatus_when_tryingToUpdateExistingEmployeeForPartner() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);
        given(employeeService.getElementById(1L)).willReturn(employee);
        given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        given(employeeService.isElementExistById(1L)).willReturn(true);
        given(partnerService.getElementById(1L)).willReturn(partner);
        EmployeeDTO employeeDTO = new EmployeeDTO(employee);

        mockMvc.perform(put("/api/partners/1/employees/1")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(employeeDTO))
                .header("Origin", "*"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void should_returnConflictStatus_when_tryingToUpdateEmployeeWhenUrlIsNotValidForPartner() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee), Collections.emptyList(), Collections.emptyList());
        given(employeeService.getElementById(1L)).willReturn(employee);
        given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        given(partnerService.getElementById(1L)).willReturn(partner);
        EmployeeDTO employeeDTO = new EmployeeDTO(employee);

        mockMvc.perform(put("/api/partners/1/employees/2")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(employeeDTO))
                .header("Origin", "*"))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_returnBadRequestStatus_when_tryingToUpdateEmployeeWhenObjectIdNotEqualToUrlIdForPartner() throws Exception {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        Employee employee2 = new Employee("", "", "", "", Collections.emptyList());
        employee2.setId(2L);
        Partner partner = new Partner("", "", "", "", "", null, null,
                Collections.singletonList(employee2), Collections.emptyList(), Collections.emptyList());
        given(employeeService.getElementById(1L)).willReturn(employee);
        given(partnerService.findPartnerContainingEmployee(employee)).willReturn(partner);
        given(partnerService.getElementById(1L)).willReturn(partner);
        EmployeeDTO employeeDTO = new EmployeeDTO(employee);

        mockMvc.perform(put("/api/partners/1/employees/1")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(employeeDTO))
                .header("Origin", "*"))
                .andExpect(status().isBadRequest());
    }

    private Page<Employee> getTestPageData() {
        List<Employee> employees = new LinkedList<>();
        Employee employee1 = new Employee("", "", "", "", Collections.emptyList());
        employee1.setId(1L);
        employees.add(employee1);
        Employee employee2 = new Employee("", "", "", "", Collections.emptyList());
        employee2.setId(2L);
        employees.add(employee2);
        Employee employee3 = new Employee("", "", "", "", Collections.emptyList());
        employee3.setId(3L);
        employees.add(employee3);
        return new PageImpl<>(employees);
    }

}
