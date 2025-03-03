package homerep.homerepoauth.controllers;

import homerep.homerepoauth.dto.UserDTO;
import homerep.homerepoauth.models.User;
import homerep.homerepoauth.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/user")
public class UserController {
    private UserRepository userRepository;
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok().body(users.stream().map(UserDTO::toUserDTO).toList());
    }
}
