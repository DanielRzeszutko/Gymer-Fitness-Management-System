package com.gymer.components.slotsemployee;

import com.gymer.resources.employee.EmployeeService;
import com.gymer.resources.employee.entity.Employee;
import com.gymer.resources.partner.PartnerService;
import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.slot.SlotService;
import com.gymer.resources.slot.entity.Slot;
import com.gymer.components.common.entity.JsonResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class SlotsEmployeeService {

    private final SlotService slotService;
    private final EmployeeService employeeService;
    private final PartnerService partnerService;

    /**
     * Service method responsible for adding or updating employee connected with specified slot
     */
    public JsonResponse updateEmployeeInSlot(Long slotId, Long employeeId) {
        Slot slot = slotService.getElementById(slotId);
        Partner partner = partnerService.findPartnerContainingSlot(slot);
        Employee employee = employeeService.getElementById(employeeId);
        if (partner.getEmployees().contains(employee)) return updateSlotWithNewEmployee(slot, employee);
        return new JsonResponse("Can't add Employee because he's not working for you.", true);
    }

    /**
     * Service method responsible for removing employee connected with specified slot
     */
    public JsonResponse removeEmployeeFromSlot(Long slotId) {
        Slot slot = slotService.getElementById(slotId);
        return updateSlotWithNewEmployee(slot, null);
    }

    private JsonResponse updateSlotWithNewEmployee(Slot slot, Employee employee) {
        slot.setEmployee(employee);
        slotService.updateElement(slot);
        return new JsonResponse("Successfully changed Employee in Slot.", false);
    }

}
