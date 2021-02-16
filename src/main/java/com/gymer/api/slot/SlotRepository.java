package com.gymer.api.slot;

import com.gymer.api.slot.entity.Slot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends PagingAndSortingRepository<Slot, Long> {

    Page<Slot> findAllByEmployee_FirstNameOrEmployee_LastName(String firstName, String lastName, Pageable pageable);

}
