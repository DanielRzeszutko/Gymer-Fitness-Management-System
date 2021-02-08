package com.gymer.api.credential;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.CredentialDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CredentialController {

    private final CredentialService credentialService;
    private final PartnerService partnerService;
    private final UserService userService;

    @Autowired
    public CredentialController(CredentialService credentialService, PartnerService partnerService, UserService userService) {
        this.credentialService = credentialService;
        this.partnerService = partnerService;
        this.userService = userService;
    }

    @GetMapping("/api/partners/{partnerId}/credentials/{credentialId}")
    public CredentialDTO getCredentialFromPartnerById(@PathVariable Long partnerId, @PathVariable Long credentialId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        if (!partner.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return convertToCredentialDTO(credentialService.getCredentialById(credentialId));
    }

    @PutMapping("/api/partners/{partnerId}/credentials/{credentialId}")
    public void updateCredentialFromPartnerById(@RequestBody CredentialDTO credentialDTO, @PathVariable Long partnerId, @PathVariable Long credentialId) {
        Partner partner = partnerService.getPartnerById(partnerId);
        if (!partner.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Credential credential = convertToCredential(credentialDTO);
        credential.setId(credentialId);
        credentialService.updateCredentials(credential);
    }

    @GetMapping("/api/users/{userId}/credentials/{credentialId}")
    public CredentialDTO getCredentialFromUserById(@PathVariable Long userId, @PathVariable Long credentialId) {
        User user = userService.getUserById(userId);
        if (!user.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return convertToCredentialDTO(credentialService.getCredentialById(credentialId));
    }

    @PutMapping("/api/users/{userId}/credentials/{credentialId}")
    public void updateCredentialFromUserById(@RequestBody CredentialDTO credentialDTO, @PathVariable Long userId, @PathVariable Long credentialId) {
        User user = userService.getUserById(userId);
        if (!user.getCredential().getId().equals(credentialId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Credential credential = convertToCredential(credentialDTO);
        credential.setId(credentialId);
        credentialService.updateCredentials(credential);
    }

    private Credential convertToCredential(CredentialDTO credentialDTO) {
        return new Credential(
                credentialDTO.getId(),
                credentialDTO.getEmail(),
                credentialDTO.getPassword(),
                credentialDTO.getPhoneNumber(),
                credentialDTO.isActive()
        );
    }

    private CredentialDTO convertToCredentialDTO(Credential credential) {
        return new CredentialDTO(
                credential.getId(),
                credential.getEmail(),
                credential.getPassword(),
                credential.getPhoneNumber(),
                credential.isActive()
        );
    }

}
