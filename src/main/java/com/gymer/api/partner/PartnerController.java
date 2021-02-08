package com.gymer.api.partner;

import com.gymer.api.partner.entity.Partner;
import com.gymer.api.partner.entity.PartnerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;

    @Autowired
    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    public Iterable<PartnerDTO> getAllPartners() {
        List<Partner> partners = (List<Partner>) partnerService.getAllPartners();
        return partners.stream().map(this::convertToPartnerDTO).collect(Collectors.toList());
    }

    @GetMapping("/{partnerId}")
    public PartnerDTO getPartnerById(@PathVariable Long partnerId) {
        return convertToPartnerDTO(partnerService.getPartnerById(partnerId));
    }

    @PutMapping("/{partnerId}")
    public void updatePartner(@RequestBody PartnerDTO partnerDTO, @PathVariable Long partnerId) {
        if (!partnerDTO.getId().equals(partnerId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        Partner newPartner = convertToPartner(partnerDTO);
        partnerService.updatePartner(newPartner);
    }

    @DeleteMapping("/{partnerId}")
    public void deletePartner(@PathVariable Long partnerId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        partnerService.deletePartner(partner);
    }

    private Partner convertToPartner(PartnerDTO partnerDTO) {
        Partner partner = partnerService.getPartnerById(partnerDTO.getId());
        partner.setName(partnerDTO.getName());
        partner.setDescription(partnerDTO.getDescription());
        partner.setLogo(partnerDTO.getLogo());
        partner.setWebsite(partnerDTO.getWebsite());
        return partner;
    }

    private PartnerDTO convertToPartnerDTO(Partner partner) {
        Link credentialLink = Link.of("/partners/" + partner.getId() + "/credentials/" + partner.getCredential().getId());
        Link addressLink = Link.of("/partners/" + partner.getId() + "/addresses/" + partner.getAddress().getId());
        List<Link> employeeLinks = partner.getEmployees().stream().map(
                employee -> Link.of("/partners/" + partner.getId() + "/employees/" + employee.getId())
        ).collect(Collectors.toList());
        List<Link> slotsLinks = partner.getSlots().stream().map(
                slot -> Link.of("/partners/" + partner.getId() + "/slots/" + slot.getId())
        ).collect(Collectors.toList());
        List<Link> slotsLinks = Collections.emptyList();
        List<Link> workingHoursLinks = partner.getWorkingHours().stream().map(
                workingHour -> Link.of("/partners/" + partner.getId() + "/workinghours/" + workingHour.getId())
        ).collect(Collectors.toList());
        return new PartnerDTO(
                partner.getId(),
                partner.getName(),
                partner.getLogo(),
                partner.getDescription(),
                partner.getWebsite(),
                credentialLink,
                addressLink,
                employeeLinks,
                slotsLinks,
                workingHoursLinks
        );
    }

}
