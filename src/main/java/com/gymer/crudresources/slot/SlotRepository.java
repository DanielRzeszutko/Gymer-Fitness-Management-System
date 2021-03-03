package com.gymer.crudresources.slot;

import com.gymer.crudresources.slot.entity.Slot;
import com.gymer.crudresources.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SlotRepository extends PagingAndSortingRepository<Slot, Long> {

    Page<Slot> findAllByEmployee_FirstNameOrEmployee_LastName(String firstName, String lastName, Pageable pageable);

    List<Slot> findAllByUsersContains(User user);

}
