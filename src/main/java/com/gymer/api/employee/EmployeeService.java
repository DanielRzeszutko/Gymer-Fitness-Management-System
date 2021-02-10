package com.gymer.api.employee;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.employee.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService extends AbstractRestApiService<Employee, Long> {

    @Autowired
    public EmployeeService(EmployeeRepository repository) {
        super(repository);
    }

    public void deleteEmployee(Employee employee) {
        repository.delete(employee);
    }

}
