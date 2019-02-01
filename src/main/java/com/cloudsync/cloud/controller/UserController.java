package com.cloudsync.cloud.controller;


import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.repository.UserRepository;
import com.cloudsync.cloud.service.UserDetailsServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class UserController {

    private final JavaMailSender mailSender;
    private HashMap<String, String> mailTokens = new HashMap<>();

    final PasswordEncoder passwordEncoder;

    final UserRepository userRepository;

    final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder, UserRepository userRepository, UserDetailsServiceImpl userDetailsService, JavaMailSender mailSender) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.mailSender = mailSender;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void validateRegistrationToken(@RequestParam("token") String token) {
        String email = mailTokens.get(token);
        User user = userRepository.findByUsername(email);
        user.setEnabled(true);
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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getUsername());
        message.setSubject("Hi from Cloud synchronization and security");
        if(mailTokens.size() > 10) {
            mailTokens.clear();
        }
        String registrationToken = RandomStringUtils.random(20, true, true);
        mailTokens.put(registrationToken, user.getUsername());
        message.setText("Your registration url: http://cloudsyncro.herokuapp.com/login?token=" + registrationToken);
        mailSender.send(message);
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