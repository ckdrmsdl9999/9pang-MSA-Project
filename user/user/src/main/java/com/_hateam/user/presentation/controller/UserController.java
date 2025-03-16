package com._hateam.user.presentation.controller;


import com._hateam.user.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/")
@AllArgsConstructor
public class UserController {



    @PostMapping("signup")
    public void addUser(@RequestBody User user) {
        System.out.println();
    //    UserRoles.
      //  UserRoles
        //return user;
    }






}
