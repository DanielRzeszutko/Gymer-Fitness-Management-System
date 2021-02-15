package com.gymer.api.employee;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.employee.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService extends AbstractRestApiService<Employee, Long> {

    @Autowired
    public EmployeeService(EmployeeRepository repository) {
        super(repository);
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
    public Iterable<Employee> findAllContaining(Sort sort, String searchBy) {
        return ((EmployeeRepository) repository).findAllByFirstNameContainsOrLastNameContains(searchBy, searchBy, sort);
    }

}
