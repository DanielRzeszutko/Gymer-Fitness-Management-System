package com.gymer.slotsemployee;

import com.gymer.commonresources.employee.entity.Employee;
import com.gymer.commonresources.slot.entity.Slot;
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
class SlotsEmployeeController {

    private final SlotsEmployeeService service;

    /**
     * Controller endpoint that change selected employee in selected slot. Method provides
     * various of actions, like: If slot Id and json body slot Id are not equal, throws CONFLICT status,
     * but if are equal selected slot is updated with selected employee, or removed if field 'remove' is TRUE.
     *
     * @param details - Object holding information about employeeId which should be added to the
     *                slot, slotId which should be modified and remove field, taking boolean value
     *                which tells we want remove employee or update him instead.
     * @param slotId  - Valid slot ID, must be equal to the slot ID provided in details body.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @PostMapping("/api/slotemployee/{slotId}/employee")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerManipulatingSlot(#slotId))")
    public void updateEmployeeInSlot(@RequestBody SlotsEmployeeDetails details, @PathVariable Long slotId) {
        if (!details.getSlotId().equals(slotId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid slot id.");
        }

        if (details.isRemoveEmployee()) {
            service.clearSlot(details.getSlotId());
            throw new ResponseStatusException(HttpStatus.OK, "Successfully removed Employee from slot.");
        }

        Slot slot = service.getSlotById(slotId);
        Employee employee = service.getEmployeeById(details.getEmployeeId());
        if (service.isPartnerNotContainsThisEmployee(slot, employee)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't add Employee because he's not working for you.");
        }

        service.saveUpdatedSlotInDatabase(slot, employee);
        throw new ResponseStatusException(HttpStatus.OK, "Successfully changed Employee in Slot.");
    }

}
