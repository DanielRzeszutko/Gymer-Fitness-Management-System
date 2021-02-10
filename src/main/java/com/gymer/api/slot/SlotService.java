package com.gymer.api.slot;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SlotService extends AbstractRestApiService<Slot, Long> {

    @Autowired
    public SlotService(SlotRepository repository) {
        super(repository);
    }

    /**
     * Service method responsible for deleting slot from database completely
     */
    public void deleteSlot(Slot slot) {
        repository.delete(slot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Slot> findAllContaining(Sort sort, String searchBy) {
        return ((SlotRepository) repository).findAllByEmployee_FirstNameOrEmployee_LastName(searchBy, searchBy, sort);
    }

}
