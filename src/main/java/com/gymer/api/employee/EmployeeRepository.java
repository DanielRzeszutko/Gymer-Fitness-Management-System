package com.gymer.api.employee;

import com.gymer.api.employee.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface EmployeeRepository extends CrudRepository<Employee, Long> {
}
