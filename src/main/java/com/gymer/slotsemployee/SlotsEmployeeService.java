package com.gymer.slotsemployee;

import com.gymer.common.entity.JsonResponse;
import com.gymer.common.crudresources.employee.EmployeeService;
import com.gymer.common.crudresources.employee.entity.Employee;
import com.gymer.common.crudresources.partner.PartnerService;
import com.gymer.common.crudresources.partner.entity.Partner;
import com.gymer.common.crudresources.slot.SlotService;
import com.gymer.common.crudresources.slot.entity.Slot;
import com.gymer.slotsemployee.entity.SlotsEmployeeDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class SlotsEmployeeService {

    private final SlotService slotService;
    private final EmployeeService employeeService;
    private final PartnerService partnerService;

    /**
     * Service method responsible for adding or updating employee connected with specified slot.
     * If remove field is true employee is removed from the slot, instead employee with provided
     * ID is added to slot by slotId in URL and SlotsEmployeeDetails object.
     * @param details - Object holding information about employeeId which should be added to the
     *                slot, slotId which should be modified and remove field, taking boolean value
     *                which tells we want remove employee or update him instead.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public JsonResponse updateEmployeeSigningAttribute(SlotsEmployeeDetails details) {
        if (details.isRemoveEmployee()) return clearSlot(details.getSlotId());
        return updateSlot(details.getSlotId(), details.getEmployeeId());
    }

    /**
     * Service method responsible for removing employee connected with specified slot.
     * @param slotId - Valid slot ID, must be equal to the slot ID provided in details body.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public JsonResponse clearSlot(Long slotId) {
        Slot slot = slotService.getElementById(slotId);
        return saveUpdatedSlotInDatabase(slot, null);
    }

    private JsonResponse updateSlot(Long slotId, Long employeeId) {
        Slot slot = slotService.getElementById(slotId);
        Employee employee = employeeService.getElementById(employeeId);

        if (isPartnerNotContainsThisEmployee(slot, employee)) {
            return JsonResponse.invalidMessage("Can't add Employee because he's not working for you.");
        }

        return saveUpdatedSlotInDatabase(slot, employee);
    }

    private boolean isPartnerNotContainsThisEmployee(Slot slot, Employee employee) {
        Partner partner = partnerService.findPartnerContainingSlot(slot);
        return !partner.getEmployees().contains(employee);
    }

    private JsonResponse saveUpdatedSlotInDatabase(Slot slot, Employee employee) {
        slot.setEmployee(employee);
        slotService.updateElement(slot);
        return JsonResponse.validMessage("Successfully changed Employee in Slot.");
    }

}
