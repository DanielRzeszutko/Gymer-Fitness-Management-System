package com.gymer.api.user;

import com.gymer.api.user.entity.User;
import com.gymer.api.user.entity.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public UserDTO getUserById(@PathVariable Long userId) {
        return convertToUserDTO(userService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    public void updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
        if (!userDTO.getId().equals(userId)) throw new ResponseStatusException(HttpStatus.CONFLICT);
        User newUser = convertToUser(userDTO);
        userService.updateUser(newUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        userService.deleteUser(user);
    }

    private User convertToUser(UserDTO userDTO) {
        User user = userService.getUserById(userDTO.getId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        return user;
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName()
        );

        Link selfLink = Link.of("/users/" + user.getId()).withSelfRel();

        Link credentialLink = Link.of("/users/" + user.getId() + "/credentials/" + user.getCredential().getId()).withRel("credentials");

        userDTO.add(selfLink, credentialLink);

        return userDTO;
    }

}
