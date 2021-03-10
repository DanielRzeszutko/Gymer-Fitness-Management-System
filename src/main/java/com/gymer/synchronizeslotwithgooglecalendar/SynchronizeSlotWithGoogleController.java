package com.gymer.synchronizeslotwithgooglecalendar;

import com.gymer.commoncomponents.languagepack.LanguageComponent;
import com.gymer.commonresources.slot.SlotService;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
class SynchronizeSlotWithGoogleController {

    private final SynchronizeSlotWithGoogleService service;
    private final SlotService slotService;
    private final LanguageComponent language;

    @GetMapping("/api/slotuser/{slotId}/synchronize")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public void synchronizeSlots(@RequestBody SynchronizeSlotDetails details, @PathVariable Long slotId) {
        if (!details.getSlotId().equals(slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, language.invalidSlotId());
        }

        if (!service.isUserLoggedAsActiveUser(details.getUserId())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, language.signInAsValidUser());
        }

        Slot slot = slotService.getElementById(slotId);
        service.synchronizeSlotWithGoogleCalendar(slot);
        throw new ResponseStatusException(HttpStatus.OK, language.successfullySynchronized());
    }

}
