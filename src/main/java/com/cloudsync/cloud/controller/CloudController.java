package com.cloudsync.cloud.controller;

import com.cloudsync.cloud.model.Provider;
import com.cloudsync.cloud.model.SyncAccount;
import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.repository.UserRepository;
import com.kloudless.KClient;
import com.kloudless.Kloudless;
import com.kloudless.exception.APIConnectionException;
import com.kloudless.exception.APIException;
import com.kloudless.exception.AuthenticationException;
import com.kloudless.exception.InvalidRequestException;
import com.kloudless.model.Folder;
import com.kloudless.model.Metadata;
import com.kloudless.model.MetadataCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

@Controller
public class CloudController {

    @Autowired
    UserRepository userRepository;


    //====================Add providers=========================

    @RequestMapping(value = "/addServiceGoogle", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceGoogle(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setGoogleAccount(provider.getAccount().getId());
            currentUser.setGoogleToken(provider.getAccessToken());
            userRepository.save(currentUser);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "added";
    }

    @RequestMapping(value = "/addServiceDropbox", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceDropbox(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setDropboxAccount(provider.getAccount().getId());
            currentUser.setDropboxToken(provider.getAccessToken());
            userRepository.save(currentUser);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "added";
    }

    @RequestMapping(value = "/addServiceOnedrive", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceOnedrive(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setOnedriveAccount(provider.getAccount().getId());
            currentUser.setOnedriveToken(provider.getAccessToken());
            userRepository.save(currentUser);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "added";
    }

    @RequestMapping(value = "/addServiceBox", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceBox(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setBoxAccount(provider.getAccount().getId());
            currentUser.setBoxToken(provider.getAccessToken());
            userRepository.save(currentUser);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "added";
    }

    @RequestMapping(value = "/getFilesGoogle", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesGoogle(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        KClient storage = new KClient(user.getGoogleToken(), user.getGoogleAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");


        return metadataCollection;
    }

    @RequestMapping(value = "/getFilesDropbox", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesDropbox(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        KClient storage = new KClient(user.getDropboxToken(), user.getDropboxAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");


        return metadataCollection;
    }

    @RequestMapping(value = "/getFilesOnedrive", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesOnedrive(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        KClient storage = new KClient(user.getOnedriveToken(), user.getOnedriveAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");


        return metadataCollection;
    }

    @RequestMapping(value = "/getFilesBox", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesBox(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        KClient storage = new KClient(user.getBoxToken(), user.getBoxAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");


        return metadataCollection;
    }


    @RequestMapping(value = "/sync", method = RequestMethod.POST)
    @ResponseBody
    public Authentication sync(@RequestBody SyncAccount syncAccount, Authentication auth) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException {
        String sourceAccount, sourceToken, destinationAccount, destinationToken;
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        switch (syncAccount.getSource()) {
            case 1:
                sourceAccount = user.getGoogleAccount();
                sourceToken = user.getGoogleToken();
                break;
            case 2:
                sourceAccount = user.getDropboxAccount();
                sourceToken = user.getDropboxToken();
                break;
            case 3:
                sourceAccount = user.getOnedriveAccount();
                sourceToken = user.getOnedriveToken();
                break;
            case 4:
                sourceAccount = user.getBoxAccount();
                sourceToken = user.getBoxToken();
                break;
            default:
                return null;
        }

        switch (syncAccount.getDestination()) {
            case 1:
                destinationAccount = user.getGoogleAccount();
                destinationToken = user.getGoogleToken();
                break;
            case 2:
                destinationAccount = user.getDropboxAccount();
                destinationToken = user.getDropboxToken();
                break;
            case 3:
                destinationAccount = user.getOnedriveAccount();
                destinationToken = user.getOnedriveToken();
                break;
            case 4:
                destinationAccount = user.getBoxAccount();
                destinationToken = user.getBoxToken();
                break;
            default:
                return null;
        }

        KClient sourceStorage = new KClient(sourceToken, sourceAccount, null);
        KClient destinationStorage = new KClient(destinationToken, destinationAccount, null);
        MetadataCollection sCollection = sourceStorage.contents(null, Folder.class, "root");
        MetadataCollection dCollection = destinationStorage.contents(null, Folder.class, "root");
        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

        for (Metadata file : dCollection.objects) {
            if (file.type.equals("file")) {
                if (!sCollection.objects.contains(file)) {
                    destinationStorage.delete(null, com.kloudless.model.File.class, file.id);
                }
            }
        }

        for (Metadata file : sCollection.objects) {
            if (file.type.equals("file")) {
                if (!dCollection.objects.contains(file)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    fileParams.put("parent_id", "root");
                    fileParams.put("name", file.name);
                    fileParams.put("account", destinationAccount);
                    com.kloudless.model.File.copy(file.id, sourceAccount, fileParams);
                }
            }
        }

        return auth;
    }

    @RequestMapping(value = "/twowaysync", method = RequestMethod.POST)
    @ResponseBody
    public Authentication twoWaySync(@RequestBody SyncAccount syncAccount, Authentication auth) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException {
        String sourceAccount, sourceToken, destinationAccount, destinationToken;
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        switch (syncAccount.getSource()) {
            case 1:
                sourceAccount = user.getGoogleAccount();
                sourceToken = user.getGoogleToken();
                break;
            case 2:
                sourceAccount = user.getDropboxAccount();
                sourceToken = user.getDropboxToken();
                break;
            case 3:
                sourceAccount = user.getOnedriveAccount();
                sourceToken = user.getOnedriveToken();
                break;
            case 4:
                sourceAccount = user.getBoxAccount();
                sourceToken = user.getBoxToken();
                break;
            default:
                return null;
        }

        switch (syncAccount.getDestination()) {
            case 1:
                destinationAccount = user.getGoogleAccount();
                destinationToken = user.getGoogleToken();
                break;
            case 2:
                destinationAccount = user.getDropboxAccount();
                destinationToken = user.getDropboxToken();
                break;
            case 3:
                destinationAccount = user.getOnedriveAccount();
                destinationToken = user.getOnedriveToken();
                break;
            case 4:
                destinationAccount = user.getBoxAccount();
                destinationToken = user.getBoxToken();
                break;
            default:
                return null;
        }

        KClient sourceStorage = new KClient(sourceToken, sourceAccount, null);
        KClient destinationStorage = new KClient(destinationToken, destinationAccount, null);
        MetadataCollection sCollection = sourceStorage.contents(null, Folder.class, "root");
        MetadataCollection dCollection = destinationStorage.contents(null, Folder.class, "root");
        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

        for (Metadata file : dCollection.objects) {
            if (file.type.equals("file")) {
                if (!sCollection.objects.contains(file)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    fileParams.put("parent_id", "root");
                    fileParams.put("name", file.name);
                    fileParams.put("account", sourceAccount);
                    com.kloudless.model.File.copy(file.id, destinationAccount, fileParams);
                }
            }
        }

        for (Metadata file : sCollection.objects) {
            if (file.type.equals("file")) {
                if (!dCollection.objects.contains(file)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    fileParams.put("parent_id", "root");
                    fileParams.put("name", file.name);
                    fileParams.put("account", destinationAccount);
                    com.kloudless.model.File.copy(file.id, sourceAccount, fileParams);
                }
            }
        }

        return auth;
    }

}
