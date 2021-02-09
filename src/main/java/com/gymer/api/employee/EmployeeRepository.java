package com.gymer.api.employee;

import com.gymer.api.employee.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long> {
}
