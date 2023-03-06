package home.library.controller;

import home.library.model.User;
import home.library.model.dto.UserRegistrationDto;
import home.library.service.UserService;
import home.library.service.UsersLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import home.library.model.UsersLibrary;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/registration")
public class UserRegistrationController {
    @Autowired
    private UserService userService;

    @Autowired
    private UsersLibraryService usersLibraryService;

//    @ModelAttribute("user")
//    public UserRegistrationDto userRegistrationDto() {
//        return new UserRegistrationDto();
//    }

//    @GetMapping
//    public String showRegistrationForm() {
//        return "registration";
//    }

    @PostMapping
    public HttpStatus registerUserAccount(@RequestBody UserRegistrationDto registrationDto) {
        User user = userService.save(registrationDto);
        UsersLibrary usersLibrary = new UsersLibrary(user);
        usersLibraryService.save(usersLibrary);
        if (user ==null){
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.ACCEPTED;
    }

}
