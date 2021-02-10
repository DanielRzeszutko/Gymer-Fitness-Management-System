package com.gymer.api.partner;

import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.common.service.AbstractRestApiService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.partner.entity.PartnerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

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
    @GetMapping
    public CollectionModel<PartnerDTO> getAllElementsSortable(Sort sort, @RequestParam(required = false, name = "contains") String searchBy) {
        return super.getAllElementsSortable(sort, searchBy);
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

        Link selfLink = Link.of("/partners/" + partnerDTO.getId()).withSelfRel();
        Link credentialLink = Link.of("/partners/" + partner.getId() + "/credentials/" + partner.getCredential().getId()).withRel("credentials");
        Link addressLink = Link.of("/partners/" + partner.getId() + "/addresses/" + partner.getAddress().getId()).withRel("addresses");

        Links employeeLinks = Links.of(partner.getEmployees().stream().map(
                employee -> Link.of("/partners/" + partner.getId() + "/employees/" + employee.getId()).withRel("employees")
        ).collect(Collectors.toList()));

        Links slotsLinks = Links.of(partner.getSlots().stream().map(
                slot -> Link.of("/partners/" + partner.getId() + "/slots/" + slot.getId()).withRel("slots")
        ).collect(Collectors.toList()));

        Links workingHoursLinks = Links.of(partner.getWorkingHours().stream().map(
                workingHour -> Link.of("/partners/" + partner.getId() + "/workinghours/" + workingHour.getId()).withRel("workinghours")
        ).collect(Collectors.toList()));

        partnerDTO.add(selfLink, credentialLink, addressLink);
        partnerDTO.add(employeeLinks);
        partnerDTO.add(slotsLinks);
        partnerDTO.add(workingHoursLinks);

        return partnerDTO;
    }

}
