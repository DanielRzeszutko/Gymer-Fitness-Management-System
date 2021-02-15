package com.gymer.api.employee;

import com.gymer.api.employee.entity.Employee;
import com.gymer.api.partner.entity.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {

    Page<Employee> findAllByFirstNameContainsOrLastNameContains(String firstName, String lastName, Pageable pageable);

}
