package com.gymer.slotsemployee;

import com.gymer.common.entity.JsonResponse;
import com.gymer.resources.employee.EmployeeService;
import com.gymer.resources.employee.entity.Employee;
import com.gymer.resources.partner.PartnerService;
import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.slot.SlotService;
import com.gymer.resources.slot.entity.Slot;
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
     * @param slotId - Valid slot ID, must be equal to the slot ID provided in details body.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public JsonResponse updateEmployeeSigningAttribute(SlotsEmployeeDetails details, Long slotId) {
        if (details.isRemoveEmployee()) {
            return removeEmployeeFromSlot(details.getSlotId());
        }

        return updateEmployeeInSlot(details.getSlotId(), details.getEmployeeId());
    }

    /**
     * Service method responsible for removing employee connected with specified slot.
     * @param slotId - Valid slot ID, must be equal to the slot ID provided in details body.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public JsonResponse removeEmployeeFromSlot(Long slotId) {
        Slot slot = slotService.getElementById(slotId);
        return updateSlotWithNewEmployee(slot, null);
    }

    private JsonResponse updateEmployeeInSlot(Long slotId, Long employeeId) {
        Slot slot = slotService.getElementById(slotId);
        Partner partner = partnerService.findPartnerContainingSlot(slot);
        Employee employee = employeeService.getElementById(employeeId);

        if (partner.getEmployees().contains(employee)) {
            return JsonResponse.invalidMessage("Can't add Employee because he's not working for you.");
        }

        return updateSlotWithNewEmployee(slot, employee);
    }

    private JsonResponse updateSlotWithNewEmployee(Slot slot, Employee employee) {
        slot.setEmployee(employee);
        slotService.updateElement(slot);
        return JsonResponse.validMessage("Successfully changed Employee in Slot.");
    }

}
