package com.gymer.api.employee;

import com.gymer.api.employee.entity.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {

    Iterable<Employee> findAllByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Sort sort);

}
