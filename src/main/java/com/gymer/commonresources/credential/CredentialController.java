package com.gymer.commonresources.credential;

import com.gymer.commonresources.common.JsonRestController;
import com.gymer.commonresources.common.controller.AbstractRestApiController;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.CredentialDTO;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
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

@JsonRestController
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
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARTNER') and @accountOwnerValidator.isOwnerLoggedIn(#partnerId))")
    public void updateCredentialFromPartnerById(@RequestBody CredentialDTO credentialDTO, @PathVariable Long partnerId, @PathVariable Long credentialId) {
        Partner partner = partnerService.getElementById(partnerId);
        if (!credentialDTO.getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (!partner.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        updateCredentialInsideDatabase(credentialDTO);
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
    @PreAuthorize("hasRole('ADMIN') or ((hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId)))")
    public void updateCredentialFromUserById(@RequestBody CredentialDTO credentialDTO, @PathVariable Long userId, @PathVariable Long credentialId) {
        User user = userService.getElementById(userId);
        if (!credentialDTO.getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (!user.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        updateCredentialInsideDatabase(credentialDTO);
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

    private void updateCredentialInsideDatabase(CredentialDTO credentialDTO) {
        Credential credential = service.getElementById(credentialDTO.getId());
        credential.setPhoneNumber(credentialDTO.getPhoneNumber());
        service.updateElement(credential);
    }

}
