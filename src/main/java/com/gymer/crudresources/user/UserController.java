package com.gymer.crudresources.user;

import com.gymer.crudresources.common.JsonRestController;
import com.gymer.crudresources.common.controller.AbstractRestApiController;
import com.gymer.crudresources.credential.CredentialController;
import com.gymer.crudresources.user.entity.User;
import com.gymer.crudresources.user.entity.UserDTO;
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
public class UserController extends AbstractRestApiController<UserDTO, User, Long> {

    @Autowired
    public UserController(UserService service) {
        super(service);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/users")
    public PagedModel<EntityModel<UserDTO>> getAllElementsSortable(Pageable pageable,
                                                                   @RequestParam(required = false, name = "contains") String searchBy,
                                                                   PagedResourcesAssembler<UserDTO> assembler) {
        return super.getAllElementsSortable(pageable, searchBy, assembler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @GetMapping("/api/users/{id}")
    public UserDTO getElementById(@PathVariable Long id) {
        return super.getElementById(id);
    }

    /**
     * Endpoint showing all users connected to slot with slotId
     */
    @GetMapping("/api/partners/{partnerId}/slots/{slotId}/users")
    public PagedModel<EntityModel<UserDTO>> getUsersBySlotId(@PathVariable Long partnerId,
                                                             @PathVariable Long slotId,
                                                             Pageable pageable,
                                                             PagedResourcesAssembler<UserDTO> assembler) {
        return super.getCollectionModel(((UserService) service).findAllUsersSubmittedToSlot(pageable, slotId), assembler);
    }

    /**
     * Endpoint responsible for update user details
     */
    @PutMapping("/api/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId))")
    public void updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        if (!userDTO.getId().equals(userId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        User newUser = convertToEntity(userDTO);
        service.updateElement(newUser);
    }

    /**
     * Endpoint responsible for changing status of user to deactivated
     */
    @DeleteMapping("/api/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @accountOwnerValidator.isOwnerLoggedIn(#userId))")
    public void deleteUser(@PathVariable Long userId) {
        User user = service.getElementById(userId);
        ((UserService) service).deleteUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User convertToEntity(UserDTO userDTO) {
        User newUser = new User(userDTO);
        User oldUser = service.getElementById(userDTO.getId());
        newUser.setCredential(oldUser.getCredential());
        newUser.setProviderId(oldUser.getProviderId());
        return newUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO(user);

        Link selfLink = linkTo(
                methodOn(UserController.class).getElementById(user.getId())).withSelfRel();
        Link credentialLink = linkTo(
                methodOn(CredentialController.class).getCredentialFromUserById(user.getId(), user.getCredential().getId())).withRel("credentials");

        userDTO.add(selfLink, credentialLink);

        return userDTO;
    }

}
