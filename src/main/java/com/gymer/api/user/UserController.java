package com.gymer.api.user;

import com.gymer.api.common.controller.AbstractRestApiController;
import com.gymer.api.user.entity.User;
import com.gymer.api.user.entity.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
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
    public CollectionModel<UserDTO> getAllElementsSortable(Sort sort, @RequestParam(required = false, name = "contains") String searchBy) {
        return super.getAllElementsSortable(sort, searchBy);
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
     * Endpoint responsible for update user details
     */
    @PutMapping("/api/users/{userId}")
    public void updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        if (!userDTO.getId().equals(userId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        User newUser = convertToEntity(userDTO);
        service.updateElement(newUser);
    }

    /**
     * Endpoint responsible for changing status of user to deactivated
     */
    @DeleteMapping("/api/users/{userId}")
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
        return newUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO(user);

        Link selfLink = Link.of("/users/" + user.getId()).withSelfRel();
        Link credentialLink = Link.of("/users/" + user.getId() + "/credentials/" + user.getCredential().getId()).withRel("credentials");
        userDTO.add(selfLink, credentialLink);

        return userDTO;
    }

}
