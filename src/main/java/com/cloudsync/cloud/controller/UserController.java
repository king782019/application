package com.cloudsync.cloud.controller;


import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.model.UserEmail;
import com.cloudsync.cloud.model.UserPasswordToken;
import com.cloudsync.cloud.repository.UserRepository;
import com.cloudsync.cloud.service.UserDetailsServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.*;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
    private HashMap<String, String> resetTokens = new HashMap<>();

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

    @GetMapping("/reset")
    public String reset() {
        return "reset";
    }

    @PostMapping("/reset")
    public String resetPassword(@ModelAttribute @Valid UserEmail user) {
        String token = RandomStringUtils.random(20, true, true);
        if(resetTokens.size() > 10) {
            resetTokens.clear();
        }
        resetTokens.put(token, user.getMail());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gazeromo@gmail.com");
        message.setTo("<" + user.getMail() + ">");
        message.setSubject("Hi from Cloud synchronization and security");

        message.setText("Your reset token: " + token);
        mailSender.send(message);
        return "newPassword";
    }

    @GetMapping("/newPassword")
    public String newPasswordForm() {
        return "newPassword";
    }

    @PostMapping("/newPassword")
    public String newPassword(@ModelAttribute @Valid UserPasswordToken user) {
        if(resetTokens.containsKey(user.getToken())) {
            String email = resetTokens.get(user.getToken());
            User tempUser = userRepository.findByUsername(email);
            tempUser.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(tempUser);
        }
        return "login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String token) {
        if(token != null) {
            if(mailTokens.containsKey(token)) {
                String email = mailTokens.get(token);
                User user = userRepository.findByUsername(email);
                if(user != null) {
                    user.setEnabled(true);
                    userRepository.save(user);
                }
            }
        }
        return "login";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signupPage() {
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String registration(@ModelAttribute @Valid User user, BindingResult result) throws AddressException {
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
        message.setFrom("gazeromo@gmail.com");
        message.setTo("<" + user.getUsername() + ">");
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