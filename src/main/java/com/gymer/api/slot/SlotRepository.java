package com.gymer.api.slot;


import com.gymer.api.slot.entity.Slot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends CrudRepository<Slot, Long> {
}
