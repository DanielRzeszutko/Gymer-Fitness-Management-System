package com.gymer.commonresources.employee;

import com.gymer.commonresources.employee.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {

    Page<Employee> findAllByFirstNameContainsOrLastNameContains(String firstName, String lastName, Pageable pageable);

}
