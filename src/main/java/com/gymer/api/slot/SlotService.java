package com.gymer.api.slot;

import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlotService extends AbstractRestApiService<Slot, Long> {

    @Autowired
    public SlotService(SlotRepository repository) {
        super(repository);
    }

    public void deleteSlot(Slot slot) {
        repository.delete(slot);
    }

    public Iterable<Slot> getSlotsByEmployeeId(Long employeeId) {
        return ((SlotRepository) repository).findAllByEmployee_Id(employeeId);
    }

    public Iterable<Slot> getSlotsByEmployeeNameOrSurname(String nameOrSurname) {
        return ((SlotRepository) repository).findAllByEmployee_FirstNameOrEmployee_LastName(nameOrSurname, nameOrSurname);
    }

}
