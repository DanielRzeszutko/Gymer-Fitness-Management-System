package com.gymer.api.employee;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.partner.entity.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService extends AbstractRestApiService<Employee, Long> {

    @Autowired
    public EmployeeService(EmployeeRepository repository) {
        super(repository);;
    }

    /**
     * Service method responsible for removing employee completely from database
     */
    public void deleteEmployee(Employee employee) {
        repository.delete(employee);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Employee> findAllContaining(Pageable pageable, String searchBy) {
        return ((EmployeeRepository) repository).findAllByFirstNameContainsOrLastNameContains(searchBy, searchBy, pageable);
    }

    public Page<Employee> findAllEmployeesForPartner(Pageable pageable, Partner partner) {
        return new PageImpl<>(partner.getEmployees(), pageable, partner.getEmployees().size());
    }

}
