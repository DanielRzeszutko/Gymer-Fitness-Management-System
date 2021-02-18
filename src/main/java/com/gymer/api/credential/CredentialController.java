package com.gymer.api.credential;

import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.CredentialDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class CredentialController extends AbstractRestApiController<CredentialDTO, Credential, Long> {

    private final PartnerService partnerService;
    private final UserService userService;

    @Autowired
    public CredentialController(CredentialService service, PartnerService partnerService, UserService userService) {
        super(service);
        this.partnerService = partnerService;
        this.userService = userService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/credentials")
    public PagedModel<EntityModel<CredentialDTO>> getAllElementsSortable(Pageable pageable,
                                                                         @RequestParam(required = false, name = "contains") String searchBy,
                                                                         PagedResourcesAssembler<CredentialDTO> assembler) {
        return super.getAllElementsSortable(pageable, searchBy, assembler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/credentials/{id}")
    public CredentialDTO getElementById(@PathVariable Long id) {
        return super.getElementById(id);
    }

    /**
     * Endpoint only showing one resource with selected ID under partnersID
     */
    @GetMapping("/api/partners/{partnerId}/credentials/{credentialId}")
    public CredentialDTO getCredentialFromPartnerById(@PathVariable Long partnerId, @PathVariable Long credentialId) {
        Partner partner = partnerService.getElementById(partnerId);
        if (!partner.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return convertToDTO(service.getElementById(credentialId));
    }

    /**
     * Endpoint that receives CredentialDTO body and change all details inside database
     */
    @PutMapping("/api/partners/{partnerId}/credentials/{credentialId}")
    public void updateCredentialFromPartnerById(@RequestBody CredentialDTO credentialDTO, @PathVariable Long partnerId, @PathVariable Long credentialId) {
        Partner partner = partnerService.getElementById(partnerId);
        if (!partner.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Credential credential = convertToEntity(credentialDTO);
        credential.setId(credentialId);
        service.updateElement(credential);
    }

    /**
     * Endpoint only showing one resource with selected ID under usersID
     */
    @GetMapping("/api/users/{userId}/credentials/{credentialId}")
    public CredentialDTO getCredentialFromUserById(@PathVariable Long userId, @PathVariable Long credentialId) {
        User user = userService.getElementById(userId);
        if (!user.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return convertToDTO(service.getElementById(credentialId));
    }

    /**
     * Endpoint that receives CredentialDTO body and change all details inside database
     */
    @PutMapping("/api/users/{userId}/credentials/{credentialId}")
    public void updateCredentialFromUserById(@RequestBody CredentialDTO credentialDTO, @PathVariable Long userId, @PathVariable Long credentialId) {
        User user = userService.getElementById(userId);
        if (!user.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Credential credential = convertToEntity(credentialDTO);
        credential.setId(credentialId);
        service.updateElement(credential);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Credential convertToEntity(CredentialDTO credentialDTO) {
        return new Credential(credentialDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CredentialDTO convertToDTO(Credential credential) {
        CredentialDTO credentialDTO = new CredentialDTO(credential);
        Link selfLink = linkTo(
                methodOn(CredentialController.class).getElementById(credential.getId())).withSelfRel();
        credentialDTO.add(selfLink);
        return credentialDTO;
    }

}
