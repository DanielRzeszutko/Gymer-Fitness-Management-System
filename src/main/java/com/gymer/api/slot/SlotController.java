package com.gymer.api.slot;

import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.employee.EmployeeController;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import com.gymer.api.slot.entity.SlotDTO;
import com.gymer.api.user.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class SlotController extends AbstractRestApiController<SlotDTO, Slot, Long> {

    private final PartnerService partnerService;

    @Autowired
    public SlotController(SlotService service, PartnerService partnerService) {
        super(service);
        this.partnerService = partnerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/slots")
    public PagedModel<EntityModel<SlotDTO>> getAllElementsSortable(Pageable pageable,
                                                                   @RequestParam(required = false, name = "contains") String searchBy,
                                                                   PagedResourcesAssembler<SlotDTO> assembler) {
        return super.getAllElementsSortable(pageable, searchBy, assembler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/slots/{id}")
    public SlotDTO getElementById(@PathVariable Long id) {
        return super.getElementById(id);
    }

    /**
     * Endpoint responsible for getting all slots from partner
     */
    @GetMapping("/api/partners/{partnerId}/slots")
    public PagedModel<EntityModel<SlotDTO>> getAllSlots(@PathVariable Long partnerId,
                                                        Pageable pageable,
                                                        PagedResourcesAssembler<SlotDTO> assembler) {
        Partner partner = partnerService.getElementById(partnerId);
        return super.getCollectionModel(((SlotService) service).findAllSlotsForPartner(pageable, partner), assembler);
    }

    /**
     * Endpoint responsible for adding new slot for partner
     */
    @PostMapping("/api/partners/{partnerId}/slots")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerLoggedIn(#partnerId))")
    public void addSlotToPartner(@RequestBody SlotDTO slotDTO, @PathVariable Long partnerId) {
        Partner partner = partnerService.getElementById(partnerId);
        partner.getSlots().add(convertToEntity(slotDTO));
        partnerService.updateElement(partner);
    }

    /**
     * Endpoint responsible for getting specific slot using slotID from partner
     */
    @GetMapping("/api/partners/{partnerId}/slots/{slotId}")
    public SlotDTO getSlotById(@PathVariable Long partnerId, @PathVariable Long slotId) {
        Partner partner = partnerService.getElementById(partnerId);
        List<Slot> slots = partner.getSlots();
        for (Slot slot : slots) {
            if (slot.getId().equals(slotId)) {
                return convertToDTO(slot);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint responsible for updating slot details
     */
    @PutMapping("/api/partners/{partnerId}/slots/{slotId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerLoggedIn(#partnerId))")
    public void updateSlotById(@RequestBody SlotDTO slotDTO, @PathVariable Long partnerId, @PathVariable Long slotId) {
        if (!slotDTO.getId().equals(slotId)) throw new ResponseStatusException(HttpStatus.CONFLICT);

        Partner partner = partnerService.getElementById(partnerId);
        List<Slot> slots = partner.getSlots();
        for (Slot slot : slots) {
            if (slot.getId().equals(slotId)) {
                service.updateElement(convertToEntity(slotDTO));
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    /**
     * Endpoint responsible for deleting slot from database completely
     */
    @DeleteMapping("/api/partners/{partnerId}/slots/{slotId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerLoggedIn(#partnerId))")
    public void deleteSlot(@PathVariable Long partnerId, @PathVariable Long slotId) {
        Partner partner = partnerService.getElementById(partnerId);
        List<Slot> slots = partner.getSlots();
        for (Slot slot : slots) {
            if (slot.getId().equals(slotId)) {
                ((SlotService) service).deleteSlot(slot);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Slot convertToEntity(SlotDTO slotDTO) {
        Slot newSlot = new Slot(slotDTO);
        if (service.isElementExistById(slotDTO.getId())) {
            Slot oldSlot = service.getElementById((slotDTO.getId()));
            newSlot.setEmployee(oldSlot.getEmployee());
            newSlot.setUsers(oldSlot.getUsers());
            return newSlot;
        }
        newSlot.setEmployee(null);
        newSlot.setUsers(Collections.emptyList());
        return newSlot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SlotDTO convertToDTO(Slot slot) {
        Partner partner = partnerService.findPartnerContainingSlot(slot);

        SlotDTO slotDTO = new SlotDTO(slot);

        Link selfLink = linkTo(
                methodOn(SlotController.class).getSlotById(partner.getId(), slot.getId())).withSelfRel();
        Link employeeLink = linkTo(
                methodOn(EmployeeController.class).getEmployeeById(partner.getId(), slot.getEmployee().getId())).withRel("employee");
        Link usersLink = linkTo(
                methodOn(UserController.class).getUsersBySlotId(partner.getId(), slot.getId(), Pageable.unpaged(), null)).withRel("users");

        slotDTO.add(selfLink, employeeLink, usersLink);

        return slotDTO;
    }

}
