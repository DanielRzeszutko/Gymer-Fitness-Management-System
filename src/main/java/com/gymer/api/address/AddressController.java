package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import com.gymer.api.address.entity.AddressDTO;
import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.credential.CredentialController;
import com.gymer.api.credential.entity.CredentialDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
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
    public CollectionModel<AddressDTO> getAllElementsSortable(Sort sort, @RequestParam(required = false, name = "contains") String searchBy) {
        CollectionModel<AddressDTO> model = super.getAllElementsSortable(sort, searchBy);
        model.add(linkTo(methodOn(AddressController.class).getAllElementsSortable(sort, searchBy)).withSelfRel().expand());
        return model;
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return convertToDTO(service.getElementById(addressId));
    }

    /**
     * Endpoint that receives AddressDTO body and change all details inside database
     */
    @PutMapping("/api/partners/{partnerId}/addresses/{addressId}")
    public void updateAddress(@RequestBody AddressDTO addressDTO, @PathVariable Long partnerId, @PathVariable Long addressId) {
        Partner partner = partnerService.getElementById(partnerId);
        if (!partner.getAddress().getId().equals(addressId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
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
