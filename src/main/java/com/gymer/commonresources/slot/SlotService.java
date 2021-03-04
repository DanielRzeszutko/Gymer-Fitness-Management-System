package com.gymer.commonresources.slot;

import com.gymer.commonresources.common.service.AbstractRestApiService;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlotService extends AbstractRestApiService<Slot, Long> {

    private final SlotMailService slotMailService;

    @Autowired
    public SlotService(SlotRepository repository, SlotMailService slotMailService) {
        super(repository);
        this.slotMailService = slotMailService;
    }

    /**
     * Service method responsible for deleting slot from database completely
     */
    public void deleteSlot(Partner partner, Slot slot) {
        slotMailService.sendEmail(partner, slot);
        repository.delete(slot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Slot> findAllContaining(Pageable pageable, String searchBy) {
        return ((SlotRepository) repository).findAllByEmployee_FirstNameOrEmployee_LastName(searchBy, searchBy, pageable);
    }

    /**
     * Service method that returns all slots for specific partner with pageable sorting properties
     */
    public Page<Slot> findAllSlotsForPartner(Pageable pageable, Partner partner) {
        if (!partner.getCredential().isActivated()) return Page.empty();
        return new PageImpl<>(partner.getSlots(), pageable, partner.getSlots().size());
    }

    public Page<Slot> findAllSlotsForUser(Pageable pageable, User user) {
        if (!user.getCredential().isActivated()) return Page.empty();
        List<Slot> slots = ((SlotRepository) repository).findAllByUsersContains(user);
        return new PageImpl<>(slots, pageable, slots.size());
    }

}