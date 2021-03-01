package com.gymer.slotsemployee;

import com.gymer.common.entity.JsonResponse;
import com.gymer.slotsemployee.entity.SlotsEmployeeDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class SlotsEmployeeController {

    private final SlotsEmployeeService service;

    @PostMapping("/api/slotemployee/{slotId}/employee")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerManipulatingSlot(#slotId))")
    public JsonResponse updateEmployeeInSlot(@RequestBody SlotsEmployeeDetails details, @PathVariable Long slotId) {
        if (!details.getSlotId().equals(slotId))
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return service.updateEmployeeSigningAttribute(details, slotId);
    }

}
