package com.gymer.commonresources.slot;

import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Repository
interface SlotRepository extends PagingAndSortingRepository<Slot, Long> {

    Page<Slot> findAllByEmployee_FirstNameOrEmployee_LastName(String firstName, String lastName, Pageable pageable);

    Iterable<Slot> findAllByDateAndStartTimeBetween(Date date, Time startTime, Time endTime);

    List<Slot> findAllByUsersContains(User user);

}
