package com.gymer.api.partner;

import com.gymer.api.address.AddressController;
import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.credential.CredentialController;
import com.gymer.api.employee.EmployeeController;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.partner.entity.PartnerDTO;
import com.gymer.api.slot.SlotController;
import com.gymer.api.workinghours.WorkingHourController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class PartnerController extends AbstractRestApiController<PartnerDTO, Partner, Long> {

    @Autowired
    public PartnerController(PartnerService service) {
        super(service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/partners")
    public CollectionModel<PartnerDTO> getAllElementsSortable(Sort sort, @RequestParam(required = false, name = "contains") String searchBy) {
        CollectionModel<PartnerDTO> model = super.getAllElementsSortable(sort, searchBy);
        model.add(linkTo(methodOn(PartnerController.class).getAllElementsSortable(sort, searchBy)).withSelfRel().expand());
        return model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/partners/{partnerId}")
    public PartnerDTO getElementById(@PathVariable Long partnerId) {
        return super.getElementById(partnerId);
    }

    /**
     * Endpoint responsible for updating partner details
     */
    @PutMapping("/api/partners/{partnerId}")
    public void updatePartner(@RequestBody PartnerDTO partnerDTO, @PathVariable Long partnerId) {
        if (!partnerDTO.getId().equals(partnerId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        Partner newPartner = convertToEntity(partnerDTO);
        service.updateElement(newPartner);
    }

    /**
     * Endpoint responsible for deleting partner from application by changing status to deactivated
     */
    @DeleteMapping("/api/partners/{partnerId}")
    public void deletePartner(@PathVariable Long partnerId) {
        Partner partner = service.getElementById(partnerId);
        ((PartnerService) service).deletePartner(partner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Partner convertToEntity(PartnerDTO partnerDTO) {
        Partner oldPartner = service.getElementById(partnerDTO.getId());
        Partner newPartner = new Partner(partnerDTO);
        newPartner.setAddress(oldPartner.getAddress());
        newPartner.setEmployees(oldPartner.getEmployees());
        newPartner.setSlots(oldPartner.getSlots());
        newPartner.setWorkingHours(oldPartner.getWorkingHours());
        return newPartner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PartnerDTO convertToDTO(Partner partner) {
        PartnerDTO partnerDTO = new PartnerDTO(partner);

        Link selfLink = linkTo(methodOn(PartnerController.class).getElementById(partner.getId())).withSelfRel();
        Link credentialLink = linkTo(
                methodOn(CredentialController.class).getCredentialFromPartnerById(partner.getId(), partner.getCredential().getId())).withRel("credential");
        Link employeeLink = linkTo(
                methodOn(EmployeeController.class).getAllEmployeesByPartnerId(partner.getId())).withRel("employees");
        Link addressLink = linkTo(
                methodOn(AddressController.class).getPartnerAddressById(partner.getId(), partner.getAddress().getId())).withRel("address");
        Link slotsLink = linkTo(
                methodOn(SlotController.class).getAllSlots(partner.getId())).withRel("slots");
        Link workingHoursLink = linkTo(
                methodOn(WorkingHourController.class).getPartnerWorkingHoursById(partner.getId())).withRel("workinghours");

        partnerDTO.add(selfLink, credentialLink, employeeLink, addressLink, slotsLink, workingHoursLink);

        return partnerDTO;
    }

}
