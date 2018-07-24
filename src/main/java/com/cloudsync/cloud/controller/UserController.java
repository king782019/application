package com.cloudsync.cloud.controller;



import com.cloudsync.cloud.WebSecurityConfig;
import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.repository.UserRepository;
import com.cloudsync.cloud.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login()
    {
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupPage() {
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String registration(@ModelAttribute @Valid User user, BindingResult result) {
        if(result.hasErrors()) {
            System.out.println(result);
            System.out.println(user);
            return "redirect:/signup?error";
        }
        User temp = userRepository.findByUsername(user.getUsername());
        if(temp != null) {
            return "redirect:/signup?errorExist";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @RequestMapping(value = "/")
    public String index(Model model, Authentication user) {
        UserDetails userDetails = (UserDetails)user.getPrincipal();
        model.addAttribute("user", userDetails.getUsername());
        model.addAttribute("password", userDetails.getPassword());
        return "index";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest req, HttpServletResponse res) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null) {
            new SecurityContextLogoutHandler().logout(req, res, auth);
        }
        return "redirect:/login?logout";
    }


}