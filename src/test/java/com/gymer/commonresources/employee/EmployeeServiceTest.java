package com.gymer.commonresources.employee;

import com.gymer.commonresources.employee.entity.Employee;
import com.gymer.commonresources.partner.entity.Partner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeServiceTest {

    @Mock
    private final Pageable pageable = PageRequest.of(0, 10);

    @Mock
    private final Page<Employee> page = getTestPageData();

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    public void should_returnPageOfEmployees_when_findAllContainingWithValidData() {
        given(employeeRepository.findAllByFirstNameContainsOrLastNameContains("", "", pageable)).willReturn(page);

        Page<Employee> obtainedPage = employeeService.findAllContaining(pageable, "");

        assertArrayEquals(page.getContent().toArray(new Employee[0]), obtainedPage.getContent().toArray(new Employee[0]));
    }

    @Test
    public void should_returnPageOfEmployees_when_findAllEmployeesForPartnerWithValidData() {
        Partner partner = new Partner("", "", "", "", "", null, null,
                getTestEmployees(), Collections.emptyList(), Collections.emptyList());
        partner.setId(1L);

        Page<Employee> obtainedPage = employeeService.findAllEmployeesForPartner(pageable, partner);

        assertArrayEquals(getTestEmployees().toArray(), obtainedPage.getContent().toArray(new Employee[0]));
    }

    @Test
    public void should_returnPageWithEmployees_when_getAllElementsWithValidData() {
        given(employeeRepository.findAll(pageable)).willReturn(page);

        Page<Employee> obtainedPage = employeeService.getAllElements(pageable);

        assertArrayEquals(page.getContent().toArray(new Employee[0]), obtainedPage.getContent().toArray(new Employee[0]));
    }

    @Test
    public void should_returnValidEmployee_when_getElementByIdWithValidId() {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(1L);
        given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

        Employee obtainedEmployee = employeeService.getElementById(1L);

        assertEquals(employee, obtainedEmployee);
    }

    @Test
    public void should_returnNotFoundError_when_getElementByIdWithNonExistingId() {
        given(employeeRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> employeeService.getElementById(1L));
    }

    @Test
    public void should_returnPageWithoutRemovedElement_when_deleteEmployeeWithOldEmployee() {
        Employee employee = new Employee("", "", "", "", Collections.emptyList());
        employee.setId(2L);
        List<Employee> expectedEmployees = getTestEmployees();
        expectedEmployees.remove(1);
        given(employeeRepository.findAll(pageable)).willReturn(new PageImpl<>(expectedEmployees));

        employeeService.deleteEmployee(employee);
        List<Employee> obtainedEmployees = employeeService.getAllElements(pageable).getContent();

        assertArrayEquals(expectedEmployees.toArray(), obtainedEmployees.toArray());
    }

    @Test
    public void should_returnTrue_when_isElementExistByIdWithValidData() {
        given(employeeRepository.existsById(1L)).willReturn(true);

        boolean obtainedStatus = employeeService.isElementExistById(1L);

        assertTrue(obtainedStatus);
    }

    @Test
    public void should_returnFalse_when_isElementExistByIdWithNonExistingAddress() {
        given(employeeRepository.existsById(1L)).willReturn(false);

        boolean obtainedStatus = employeeService.isElementExistById(1L);

        assertFalse(obtainedStatus);
    }

    private List<Employee> getTestEmployees() {
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
        return employees;
    }

    private Page<Employee> getTestPageData() {
        return new PageImpl<>(getTestEmployees());
    }

}
