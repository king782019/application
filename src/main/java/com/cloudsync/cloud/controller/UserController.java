package com.cloudsync.cloud.controller;


import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.repository.UserRepository;
import com.cloudsync.cloud.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class UserController {

    final PasswordEncoder passwordEncoder;

    final UserRepository userRepository;

    final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupPage() {
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String registration(@ModelAttribute @Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            System.out.println(result);
            System.out.println(user);
            return "redirect:/signup?error";
        }
        User temp = userRepository.findByUsername(user.getUsername());
        if (temp != null) {
            return "redirect:/signup?errorExist";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }

    @RequestMapping(value = "/")
    public String index(Model model, Authentication user) {
        UserDetails userDetails = (UserDetails) user.getPrincipal();
        User currentUser = userRepository.findByUsername(userDetails.getUsername());
        Boolean google = currentUser.getGoogleAccount() != null;
        Boolean dropbox = currentUser.getDropboxAccount() != null;
        Boolean onedrive = currentUser.getOnedriveAccount() != null;
        Boolean box = currentUser.getBoxAccount() != null;
        Boolean yandex = currentUser.getYandexAccount() != null;
        Boolean hidrive = currentUser.getHidriveAccount() != null;
        Boolean pcloud = currentUser.getPcloudAccount() != null;
        model.addAttribute("googleExists", google);
        model.addAttribute("dropboxExists", dropbox);
        model.addAttribute("onedriveExists", onedrive);
        model.addAttribute("boxExists", box);
        model.addAttribute("yandexExists", yandex);
        model.addAttribute("hidriveExists", hidrive);
        model.addAttribute("pcloudExists", pcloud);
        return "index";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest req, HttpServletResponse res) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(req, res, auth);
        }
        System.out.println("logout");
        return "redirect:/login?logout";
    }


}