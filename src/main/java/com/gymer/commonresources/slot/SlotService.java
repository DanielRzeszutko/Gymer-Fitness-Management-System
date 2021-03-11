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

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
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

    public Iterable<Slot> findAllSlotsTodayStartingInAnHour() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        Date dateNow = new Date(time.getTime());

        LocalTime startTime = LocalTime.now();
        startTime = startTime.plusHours(1);
        LocalTime endTime = startTime.plusMinutes(1);

        return ((SlotRepository) repository).findAllByDateAndStartTimeBetween(dateNow, Time.valueOf(startTime), Time.valueOf(endTime));
    }

}
