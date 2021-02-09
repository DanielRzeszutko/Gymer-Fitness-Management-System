package com.gymer.api.slot;

import com.gymer.api.slot.entity.Slot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends PagingAndSortingRepository<Slot, Long> {

    Iterable<Slot> findAllByEmployee_Id(Long employeeId);

    Iterable<Slot> findAllByEmployee_FirstNameOrEmployee_LastName(String firstName, String lastName);

}
