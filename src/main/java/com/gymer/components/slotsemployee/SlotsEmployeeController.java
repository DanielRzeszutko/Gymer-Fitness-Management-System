package com.gymer.components.slotsemployee;

import com.gymer.components.common.entity.JsonResponse;
import com.gymer.components.slotsemployee.entity.SlotsEmployeeDetails;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class SlotsEmployeeController {

    private final SlotsEmployeeService service;

    @PostMapping("/api/slotemployee/{slotId}/employee")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerManipulatingSlot(#slotId))")
    public JsonResponse updateEmployeeInSlot(@RequestBody SlotsEmployeeDetails details, @PathVariable Long slotId) {
        if (!details.getSlotId().equals(slotId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        if (details.isRemoveEmployee()) return service.removeEmployeeFromSlot(details.getSlotId());
        return service.updateEmployeeInSlot(details.getSlotId(), details.getEmployeeId());
    }

}
