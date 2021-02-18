package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import com.gymer.api.address.entity.AddressDTO;
import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class AddressController extends AbstractRestApiController<AddressDTO, Address, Long> {

    private final PartnerService partnerService;

    @Autowired
    public AddressController(AddressService addressService, PartnerService partnerService) {
        super(addressService);
        this.partnerService = partnerService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/addresses")
    public PagedModel<EntityModel<AddressDTO>> getAllElementsSortable(Pageable pageable,
                                                                      @RequestParam(required = false, name = "contains") String searchBy,
                                                                      PagedResourcesAssembler<AddressDTO> assembler) {
        return super.getAllElementsSortable(pageable, searchBy, assembler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/addresses/{id}")
    public AddressDTO getElementById(@PathVariable Long id) {
        return super.getElementById(id);
    }

    /**
     * Endpoint only showing one resource with selected ID under partnersID
     */
    @GetMapping("/api/partners/{partnerId}/addresses/{addressId}")
    public AddressDTO getPartnerAddressById(@PathVariable Long partnerId, @PathVariable Long addressId) {
        Partner partner = partnerService.getElementById(partnerId);
        if (!partner.getAddress().getId().equals(addressId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return convertToDTO(service.getElementById(addressId));
    }

    /**
     * Endpoint that receives AddressDTO body and change all details inside database
     */
    @PutMapping("/api/partners/{partnerId}/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerLoggedIn(#partnerId))")
    public void updateAddress(@RequestBody AddressDTO addressDTO, @PathVariable Long partnerId, @PathVariable Long addressId) {
        Partner partner = partnerService.getElementById(partnerId);
        if (!addressDTO.getId().equals(addressId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (!partner.getAddress().getId().equals(addressId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Address address = convertToEntity(addressDTO);
        service.updateElement(address);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Address convertToEntity(AddressDTO addressDTO) {
        return new Address(addressDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressDTO convertToDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO(address);
        Link selfLink = linkTo(
                methodOn(AddressController.class).getElementById(address.getId())).withSelfRel();
        addressDTO.add(selfLink);
        return addressDTO;
    }

}
