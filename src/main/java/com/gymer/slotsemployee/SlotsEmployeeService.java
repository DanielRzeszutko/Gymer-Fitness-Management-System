package com.gymer.slotsemployee;

import com.gymer.crudresources.employee.EmployeeService;
import com.gymer.crudresources.employee.entity.Employee;
import com.gymer.crudresources.partner.PartnerService;
import com.gymer.crudresources.partner.entity.Partner;
import com.gymer.crudresources.slot.SlotService;
import com.gymer.crudresources.slot.entity.Slot;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class SlotsEmployeeService {

    private final SlotService slotService;
    private final EmployeeService employeeService;
    private final PartnerService partnerService;

    /**
     * Service method responsible for removing employee connected with specified slot.
     *
     * @param slotId - Valid slot ID, must be equal to the slot ID provided in details body.
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    public void clearSlot(Long slotId) {
        Slot slot = slotService.getElementById(slotId);
        saveUpdatedSlotInDatabase(slot, null);
    }

    public boolean isPartnerNotContainsThisEmployee(Slot slot, Employee employee) {
        Partner partner = partnerService.findPartnerContainingSlot(slot);
        return !partner.getEmployees().contains(employee);
    }

    public Slot getSlotById(Long slotId) {
        return slotService.getElementById(slotId);
    }

    public Employee getEmployeeById(Long employeeId) {
        return employeeService.getElementById(employeeId);
    }

    public void saveUpdatedSlotInDatabase(Slot slot, Employee employee) {
        slot.setEmployee(employee);
        slotService.updateElement(slot);
    }

}
