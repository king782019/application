package com.cloudsync.cloud.controller;

import com.cloudsync.cloud.model.MetadataCounter;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Controller
public class CloudController {

    private static final Logger logger = LogManager.getLogger(CloudController.class);

    final UserRepository userRepository;

    @Autowired
    public CloudController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


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
        return "OK";
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
        return "OK";
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
        return "OK";
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
        return "OK";
    }

    //====================Add providers=========================

    //=====================Check availability===================



    //=====================Check availability===================
    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/getStatusGoogle", method = RequestMethod.GET)
    @ResponseBody
    public String getStatusGoogle(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        if(user.getGoogleAccount() == null) {
            return "Not found";
        }
        KClient storage = new KClient(user.getGoogleToken(), user.getGoogleAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/getStatusDropbox", method = RequestMethod.GET)
    @ResponseBody
    public String getStatusDropbox(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        if(user.getDropboxAccount() == null) {
            return "Not found";
        }
        KClient storage = new KClient(user.getDropboxToken(), user.getDropboxAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/getStatusOnedrive", method = RequestMethod.GET)
    @ResponseBody
    public String getStatusOnedrive(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        if(user.getOnedriveAccount() == null) {
            return "Not found";
        }
        KClient storage = new KClient(user.getOnedriveToken(), user.getOnedriveAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/getStatusBox", method = RequestMethod.GET)
    @ResponseBody
    public String getStatusBox(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        if(user.getBoxAccount() == null) {
            return "Not found";
        }
        KClient storage = new KClient(user.getBoxToken(), user.getBoxAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");
        return "OK";
    }

    //====================Get files============================

    @RequestMapping(value = "/getFilesGoogle", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesGoogle(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());

        KClient storage = new KClient(user.getGoogleToken(), user.getGoogleAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");

        Integer num = null;
        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {
                num = i;
            }
        }
        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }
        num = null;

        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {

                num = i;
            }
        }


        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }


        return metadataCollection;
    }

    @RequestMapping(value = "/getFilesDropbox", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesDropbox(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        KClient storage = new KClient(user.getDropboxToken(), user.getDropboxAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");

        Integer num = null;
        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {
                num = i;
            }
        }
        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }
        num = null;

        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {

                num = i;
            }
        }


        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }


        return metadataCollection;
    }

    @RequestMapping(value = "/getFilesOnedrive", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesOnedrive(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        KClient storage = new KClient(user.getOnedriveToken(), user.getOnedriveAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");

        Integer num = null;
        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {
                num = i;
            }
        }
        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }
        num = null;

        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {

                num = i;
            }
        }


        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }


        return metadataCollection;
    }

    @RequestMapping(value = "/getFilesBox", method = RequestMethod.GET)
    @ResponseBody
    public MetadataCollection getFilesBox(Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        KClient storage = new KClient(user.getBoxToken(), user.getBoxAccount(), null);
        MetadataCollection metadataCollection = storage.contents(null, Folder.class, "root");

        Integer num = null;
        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {
                num = i;
            }
        }
        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }
        num = null;

        for (int i = 0; i < metadataCollection.objects.size(); i++) {

            metadataCollection.objects.get(i).parent.Id = "root";
            metadataCollection.objects.get(i).parent.name = "root";

            if (metadataCollection.objects.get(i).name.equals("Shared with me") || metadataCollection.objects.get(i).raw_id.equals("shared_items")) {

                num = i;
            }
        }


        if (num != null) {
            int ind = num;
            metadataCollection.objects.remove(ind);
        }


        return metadataCollection;
    }

    //====================Get files============================

    //=======================New sync===========================


    /**
     * One-way synchronization
     *
     * @param syncAccount
     * @param auth
     * @return
     * @throws UsernameNotFoundException
     * @throws APIException
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/synco", method = RequestMethod.POST)
    @ResponseBody
    public List<Metadata> synco(@RequestBody SyncAccount syncAccount, Authentication auth) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException {
        logger.debug("In 'sycno' method");
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


        MetadataCounter sourceList = new MetadataCounter(0, sCollection.objects);
        MetadataCounter destinationList = new MetadataCounter(0, dCollection.objects);


        Integer num = null;
        for (int i = 0; i < sourceList.getMetadataList().size(); i++) {

            sourceList.getMetadataList().get(i).parent.Id = "root";
            sourceList.getMetadataList().get(i).parent.name = "root";

            if (sourceList.getMetadataList().get(i).name.equals("Shared with me") || sourceList.getMetadataList().get(i).raw_id.equals("shared_items")) {
                num = i;
            }
        }
        if (num != null) {
            int ind = num;
            sourceList.getMetadataList().remove(ind);
            logger.debug("Shared with me folder deleted from source list");
        }
        num = null;

        for (int i = 0; i < destinationList.getMetadataList().size(); i++) {

            destinationList.getMetadataList().get(i).parent.Id = "root";
            destinationList.getMetadataList().get(i).parent.name = "root";

            if (destinationList.getMetadataList().get(i).name.equals("Shared with me") || destinationList.getMetadataList().get(i).raw_id.equals("shared_items")) {

                num = i;
            }
        }


        if (num != null) {
            int ind = num;
            destinationList.getMetadataList().remove(ind);
            logger.debug("Shared with me folder deleted from destination list");
        }


        sourceList = listLoop(sourceStorage, sourceList);
        destinationList = listLoop(destinationStorage, destinationList);

        List<Metadata> forRemove = new ArrayList<>();
        for (Metadata data : destinationList.getMetadataList()) {
            if (data.type.equals("file")) {
                if (!sourceList.getMetadataList().contains(data)) {
                    destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                    logger.debug(String.format("File %s has been deleted from destination storage (if)", data.name));
                    for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                        if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                            forRemove.add(destinationList.getMetadataList().get(i));
                        }
                    }

                } else {
                    for (Metadata file : sourceList.getMetadataList()) {
                        if (file.type.equals(data.type)) {
                            if (!file.parent.name.equals(data.parent.name) && file.name.equals(data.name)) {
                                destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                                logger.debug(String.format("File %s has been deleted from destination storage (else)", data.name));
                                for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                    if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                                        forRemove.add(destinationList.getMetadataList().get(i));
                                    }
                                }

                            }
                        }
                    }
                }
            }

        }

        destinationList.getMetadataList().removeAll(forRemove);
        forRemove.removeAll(forRemove);

        List<Metadata> reversed = new ArrayList<>(destinationList.getMetadataList());
        Collections.reverse(reversed);

        for (Metadata data : reversed) {
            if (data.type.equals("folder")) {
                if (!sourceList.getMetadataList().contains(data)) {
                    destinationStorage.delete(null, Folder.class, data.id);
                    logger.debug(String.format("Folder %s has been deleted from destination storage (if)", data.name));
                    for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                        if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                            forRemove.add(destinationList.getMetadataList().get(i));
                        }
                    }

                } else {
                    for (Metadata file : sourceList.getMetadataList()) {
                        if (!file.parent.name.equals(data.parent.name) && file.name.equals(data.name)) {
                            destinationStorage.delete(null, com.kloudless.model.Folder.class, data.id);
                            logger.debug(String.format("Folder %s has been deleted from destination storage (else)", data.name));
                            for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                                    forRemove.add(destinationList.getMetadataList().get(i));
                                }
                            }

                        }
                    }
                }
            }

        }

        destinationList.getMetadataList().removeAll(forRemove);
        forRemove.removeAll(forRemove);

        reversed = new ArrayList<>(sourceList.getMetadataList());
        Collections.reverse(reversed);

        for (Metadata mData : reversed) {
            if (mData.type.equals("folder")) {
                if (!destinationList.getMetadataList().contains(mData)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    for (Metadata data : destinationList.getMetadataList()) {
                        if (data.type.equals("folder")) {
                            if (data.name.equals(mData.parent.name)) {
                                fileParams.put("parent_id", data.id);
                                break;
                            }

                        }
                    }

                    fileParams.put("name", mData.name);
                    if (fileParams.size() <= 1) {
                        fileParams.put("parent_id", "root");
                    }
                    Metadata metadata = destinationStorage.create(null, Folder.class, fileParams);
                    destinationList.getMetadataList().add(metadata);
                    logger.debug(String.format("Folder %s has been created in destination storage (if)", mData.name));

                } else {
                    for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                        if (!mData.parent.name.equals(destinationList.getMetadataList().get(i).parent.name) && mData.name.equals(destinationList.getMetadataList().get(i).name)) {
                            HashMap<String, Object> fileParams = new HashMap<>();
                            for (Metadata data : destinationList.getMetadataList()) {
                                if (data.type.equals("folder")) {
                                    if (data.name.equals(mData.parent.name)) {
                                        fileParams.put("parent_id", data.id);
                                        break;
                                    }

                                }
                            }


                            fileParams.put("name", mData.name);
                            if (fileParams.size() <= 1) {
                                fileParams.put("parent_id", "root");
                            }
                            Metadata metadata = destinationStorage.create(null, Folder.class, fileParams);
                            destinationList.getMetadataList().add(metadata);
                            logger.debug(String.format("Folder %s has been created in destination storage (else)", mData.name));
                        }
                    }
                }
            }
        }


        for (Metadata mData : sourceList.getMetadataList()) {
            if (mData.type.equals("file")) {
                if (!destinationList.getMetadataList().contains(mData)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    for (Metadata data : destinationList.getMetadataList()) {
                        if (data.type.equals("folder")) {
                            if (data.name.equals(mData.parent.name)) {
                                fileParams.put("parent_id", data.id);
                                break;
                            }

                        }
                    }


                    fileParams.put("name", mData.name);
                    if (fileParams.size() <= 1) {
                        fileParams.put("parent_id", "root");
                    }
                    fileParams.put("account", destinationAccount);
                    com.kloudless.model.File.copy(mData.id, sourceAccount, fileParams);
                    logger.debug(String.format("File %s has been copied ", mData.name));
                }
            }
        }

        return sourceList.getMetadataList();
    }


    /**
     * Two-way synchronization
     *
     * @param syncAccount
     * @param auth
     * @return
     * @throws UsernameNotFoundException
     * @throws APIException
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     * @throws UnsupportedEncodingException
     */
    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/twowaysynco", method = RequestMethod.POST)
    @ResponseBody
    public List<Metadata> twowaysynco(@RequestBody SyncAccount syncAccount, Authentication auth) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException {
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


        MetadataCounter sourceList = new MetadataCounter(0, sCollection.objects);
        MetadataCounter destinationList = new MetadataCounter(0, dCollection.objects);


        Integer num = null;
        for (int i = 0; i < sourceList.getMetadataList().size(); i++) {

            sourceList.getMetadataList().get(i).parent.Id = "root";
            sourceList.getMetadataList().get(i).parent.name = "root";

            if (sourceList.getMetadataList().get(i).name.equals("Shared with me") || sourceList.getMetadataList().get(i).raw_id.equals("shared_items")) {
                num = i;
            }
        }
        if (num != null) {
            int ind = num;
            sourceList.getMetadataList().remove(ind);
        }
        num = null;

        for (int i = 0; i < destinationList.getMetadataList().size(); i++) {

            destinationList.getMetadataList().get(i).parent.Id = "root";
            destinationList.getMetadataList().get(i).parent.name = "root";

            if (destinationList.getMetadataList().get(i).name.equals("Shared with me") || destinationList.getMetadataList().get(i).raw_id.equals("shared_items")) {

                num = i;
            }
        }


        if (num != null) {
            int ind = num;
            destinationList.getMetadataList().remove(ind);
        }


        sourceList = listLoop(sourceStorage, sourceList);
        destinationList = listLoop(destinationStorage, destinationList);


        for (Metadata mData : sourceList.getMetadataList()) {
            if (mData.type.equals("folder")) {
                if (!destinationList.getMetadataList().contains(mData)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    for (Metadata data : destinationList.getMetadataList()) {
                        if (data.type.equals("folder")) {
                            if (data.name.equals(mData.parent.name)) {
                                fileParams.put("parent_id", data.id);
                                break;
                            }

                        }
                    }

                    fileParams.put("name", mData.name);
                    if (fileParams.size() <= 1) {
                        fileParams.put("parent_id", "root");
                    }
                    Metadata metadata = destinationStorage.create(null, Folder.class, fileParams);
                    logger.debug(String.format("Folder %s created in destination storage from first try", metadata.name));
                    if (fileParams.get("parent_id").equals("root")) {
                        metadata.parent.name = "root";
                    } else {
                        metadata.parent.name = mData.parent.name;
                    }
                    destinationList.getMetadataList().add(metadata);

                } else {
                    for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                        if (!mData.parent.name.equals(destinationList.getMetadataList().get(i).parent.name) && mData.name.equals(destinationList.getMetadataList().get(i).name)) {
                            HashMap<String, Object> fileParams = new HashMap<>();
                            for (Metadata data : destinationList.getMetadataList()) {
                                if (data.type.equals("folder")) {
                                    if (data.name.equals(mData.parent.name)) {
                                        fileParams.put("parent_id", data.id);
                                        break;
                                    }

                                }
                            }


                            fileParams.put("name", mData.name);
                            if (fileParams.size() <= 1) {
                                fileParams.put("parent_id", "root");
                            }
                            Metadata metadata = destinationStorage.create(null, Folder.class, fileParams);
                            logger.debug(String.format("Folder %s created in destination storage from second try", metadata.name));
                            if (fileParams.get("parent_id").equals("root")) {
                                metadata.parent.name = "root";
                            } else {
                                metadata.parent.name = mData.parent.name;
                            }
                            destinationList.getMetadataList().add(metadata);
                            break;
                        }
                    }
                }
            }
        }


        for (Metadata mData : destinationList.getMetadataList()) {
            if (mData.type.equals("folder")) {
                if (!sourceList.getMetadataList().contains(mData)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    for (Metadata data : sourceList.getMetadataList()) {
                        if (data.type.equals("folder")) {
                            if (data.name.equals(mData.parent.name)) {
                                fileParams.put("parent_id", data.id);
                                break;
                            }

                        }
                    }

                    fileParams.put("name", mData.name);
                    if (fileParams.size() <= 1) {
                        fileParams.put("parent_id", "root");
                    }
                    Metadata metadata = sourceStorage.create(null, Folder.class, fileParams);
                    logger.debug(String.format("Folder %s created in source storage from first try", metadata.name));
                    if (fileParams.get("parent_id").equals("root")) {
                        metadata.parent.name = "root";
                    } else {
                        metadata.parent.name = fileParams.get("name").toString();
                    }
                    sourceList.getMetadataList().add(metadata);

                } else {
                    for (int i = 0; i < sourceList.getMetadataList().size(); i++) {

                        if (!mData.parent.name.equals(sourceList.getMetadataList().get(i).parent.name) && mData.name.equals(sourceList.getMetadataList().get(i).name)) {
                            HashMap<String, Object> fileParams = new HashMap<>();
                            for (Metadata data : sourceList.getMetadataList()) {
                                if (data.type.equals("folder")) {
                                    if (data.name.equals(mData.parent.name)) {
                                        fileParams.put("parent_id", data.id);
                                        break;
                                    }

                                }
                            }


                            fileParams.put("name", mData.name);
                            if (fileParams.size() <= 1) {
                                fileParams.put("parent_id", "root");
                            }
                            Metadata metadata = sourceStorage.create(null, Folder.class, fileParams);
                            logger.debug(String.format("Folder %s created in source storage from second try", metadata.name));
                            if (fileParams.get("parent_id").equals("root")) {
                                metadata.parent.name = "root";
                            } else {
                                metadata.parent.name = fileParams.get("name").toString();
                            }
                            sourceList.getMetadataList().add(metadata);
                            break;
                        }

                    }
                }
            }
        }


        for (Metadata mData : sourceList.getMetadataList()) {
            if (mData.type.equals("file")) {
                if (!destinationList.getMetadataList().contains(mData)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    for (Metadata data : destinationList.getMetadataList()) {
                        if (data.type.equals("folder")) {
                            if (data.name.equals(mData.parent.name)) {
                                fileParams.put("parent_id", data.id);
                                break;
                            }

                        }
                    }


                    fileParams.put("name", mData.name);
                    if (fileParams.size() <= 1) {
                        fileParams.put("parent_id", "root");
                    }
                    fileParams.put("account", destinationAccount);
                    com.kloudless.model.File.copy(mData.id, sourceAccount, fileParams);
                    logger.debug(String.format("File %s created in destination storage", mData.name));
                }
            }
        }

        for (Metadata mData : destinationList.getMetadataList()) {
            if (mData.type.equals("file")) {
                if (!sourceList.getMetadataList().contains(mData)) {
                    HashMap<String, Object> fileParams = new HashMap<>();
                    for (Metadata data : sourceList.getMetadataList()) {
                        if (data.type.equals("folder")) {
                            if (data.name.equals(mData.parent.name)) {
                                fileParams.put("parent_id", data.id);
                                break;
                            }

                        }
                    }


                    fileParams.put("name", mData.name);
                    if (fileParams.size() <= 1) {
                        fileParams.put("parent_id", "root");
                    }
                    fileParams.put("account", sourceAccount);
                    com.kloudless.model.File.copy(mData.id, destinationAccount, fileParams);
                    logger.debug(String.format("File %s created in source storage", mData.name));
                }
            }
        }

        return sourceList.getMetadataList();
    }


    /**
     * Get files tree
     *
     * @param client
     * @param list
     * @return
     * @throws APIException
     * @throws UnsupportedEncodingException
     * @throws AuthenticationException
     * @throws InvalidRequestException
     * @throws APIConnectionException
     */
    private MetadataCounter listLoop(KClient client, MetadataCounter list) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        MetadataCollection temp = new MetadataCollection();

        int i = list.getCounter();
        for (; i < list.getMetadataList().size(); i++) {
            if (list.getMetadataList().get(i).type.equals("folder")) {
                temp = client.contents(null, Folder.class, list.getMetadataList().get(i).id);
                logger.debug(String.format("Contents of folder %s have gotten", list.getMetadataList().get(i).name));
                for (int j = 0; j < temp.objects.size(); j++) {
                    temp.objects.get(j).parent.Id = list.getMetadataList().get(i).id;
                    temp.objects.get(j).parent.name = list.getMetadataList().get(i).name;

                }
                break;
            }

        }
        if (temp.objects != null) {
            list.getMetadataList().addAll(temp.objects);
        }

        list.setCounter(i + 1);
        if (list.getMetadataList().size() != i) {
            return listLoop(client, list);
        }

        return list;
    }


    //=============================Old sync================================

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
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
