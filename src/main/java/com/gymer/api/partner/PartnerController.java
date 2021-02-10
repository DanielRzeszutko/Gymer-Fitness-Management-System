package com.gymer.api.partner;

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
@RequestMapping("/api/partners")
public class PartnerController {

    private final PartnerService partnerService;

    @Autowired
    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    public CollectionModel<PartnerDTO> getAllPartners(Sort sort, @RequestParam(required = false, name = "addressContains") String details) {
        if (details != null) {
            return CollectionModel.of(((List<Partner>) partnerService.findAllContaining(details))
                    .stream().map(this::convertToPartnerDTO).collect(Collectors.toList()));
        }
        List<Partner> partners = (List<Partner>) partnerService.getAllElements(sort);
        return CollectionModel.of(partners.stream().map(this::convertToPartnerDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{partnerId}")
    public PartnerDTO getPartnerById(@PathVariable Long partnerId) {
        return convertToPartnerDTO(partnerService.getElementById(partnerId));
    }

    @PutMapping("/{partnerId}")
    public void updatePartner(@RequestBody PartnerDTO partnerDTO, @PathVariable Long partnerId) {
        if (!partnerDTO.getId().equals(partnerId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        Partner newPartner = convertToPartner(partnerDTO);
        partnerService.updateElement(newPartner);
    }

    @DeleteMapping("/{partnerId}")
    public void deletePartner(@PathVariable Long partnerId) {
        Partner partner = partnerService.getElementById(partnerId);
        partnerService.deletePartner(partner);
    }

    private Partner convertToPartner(PartnerDTO partnerDTO) {
        Partner partner = partnerService.getElementById(partnerDTO.getId());
        partner.setName(partnerDTO.getName());
        partner.setDescription(partnerDTO.getDescription());
        partner.setLogo(partnerDTO.getLogo());
        partner.setWebsite(partnerDTO.getWebsite());
        return partner;
    }

    private PartnerDTO convertToPartnerDTO(Partner partner) {
        PartnerDTO partnerDTO = new PartnerDTO(
                partner.getId(),
                partner.getName(),
                partner.getLogo(),
                partner.getDescription(),
                partner.getWebsite()
        );

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
