package com.cloudsync.cloud.controller;

import com.cloudsync.cloud.component.ScannerWorker;
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

    private final UserRepository userRepository;

    private volatile List<ScannerWorker> threads = new ArrayList<>();

    @Autowired
    public CloudController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    //====================Add providers=========================

    @RequestMapping(value = "/addWebDav", method = RequestMethod.POST)
    @ResponseBody
    public String addWebDav(@RequestBody Provider provider, Authentication auth) throws APIException, APIConnectionException, AuthenticationException, InvalidRequestException, UnsupportedEncodingException {
        String tempAccountName = provider.getAccount().getAccount();
        String accountName = tempAccountName.substring(tempAccountName.indexOf("@")+1);
        if(!accountName.contains("@")) {
            switch(accountName) {
                case "webdav.yandex.ru":
                    addServiceYandex(provider, auth);
                    break;
                case "webdav.hidrive.strato.com":
                    addServiceHidrive(provider, auth);
                    break;
                case "webdav.pcloud.com":
                    addServicePcloud(provider, auth);
                    break;
            }
        } else {
            String newAcc = accountName.substring(accountName.indexOf("@")+1);
            switch(newAcc) {
                case "webdav.pcloud.com":
                    addServicePcloud(provider, auth);
                    break;
            }
        }



        return "OK";
    }

    @SuppressWarnings("Duplicates")
    private String addServicePcloud(Provider provider, Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails user = (UserDetails) auth.getPrincipal();
        User currentUser = userRepository.findByUsername(user.getUsername());
        currentUser.setPcloudAccount(provider.getAccount().getId());
        currentUser.setPcloudToken(provider.getAccessToken());
        userRepository.save(currentUser);
        SyncAccount account = new SyncAccount();
        if(currentUser.getGoogleAccount() != null) {
            account.setSource("google");
        } else if (currentUser.getDropboxAccount() != null) {
            account.setSource("dropbox");
        } else if (currentUser.getBoxAccount() != null) {
            account.setSource("box");
        } else if (currentUser.getOnedriveAccount() != null) {
            account.setSource("onedrive");
        } else if (currentUser.getYandexAccount() != null) {
            account.setSource("yandex");
        } else if (currentUser.getHidriveAccount() != null) {
            account.setSource("hidrive");
        }else {
            return null;
        }

        account.setDestination("pcloud");
        fullSyncronize(account, auth);
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    private String addServiceHidrive(Provider provider, Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails user = (UserDetails) auth.getPrincipal();
        User currentUser = userRepository.findByUsername(user.getUsername());
        currentUser.setHidriveAccount(provider.getAccount().getId());
        currentUser.setHidriveToken(provider.getAccessToken());
        userRepository.save(currentUser);
        SyncAccount account = new SyncAccount();
        if(currentUser.getGoogleAccount() != null) {
            account.setSource("google");
        } else if (currentUser.getDropboxAccount() != null) {
            account.setSource("dropbox");
        } else if (currentUser.getBoxAccount() != null) {
            account.setSource("box");
        } else if (currentUser.getOnedriveAccount() != null) {
            account.setSource("onedrive");
        } else if (currentUser.getYandexAccount() != null) {
            account.setSource("yandex");
        } else if (currentUser.getPcloudAccount() != null) {
            account.setSource("pcloud");
        }else {
            return null;
        }
        account.setDestination("hidrive");
        fullSyncronize(account, auth);
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    private String addServiceYandex(Provider provider, Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails user = (UserDetails) auth.getPrincipal();
        User currentUser = userRepository.findByUsername(user.getUsername());
        currentUser.setYandexAccount(provider.getAccount().getId());
        currentUser.setYandexToken(provider.getAccessToken());
        userRepository.save(currentUser);
        SyncAccount account = new SyncAccount();
        if(currentUser.getGoogleAccount() != null) {
            account.setSource("google");
        } else if (currentUser.getDropboxAccount() != null) {
            account.setSource("dropbox");
        } else if (currentUser.getBoxAccount() != null) {
            account.setSource("box");
        } else if (currentUser.getOnedriveAccount() != null) {
            account.setSource("onedrive");
        } else if (currentUser.getPcloudAccount() != null) {
            account.setSource("pcloud");
        } else if (currentUser.getHidriveAccount() != null) {
            account.setSource("hidrive");
        }else {
            return null;
        }
        account.setDestination("yandex");
        fullSyncronize(account, auth);
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/addServiceGoogle", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceGoogle(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException, APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setGoogleAccount(provider.getAccount().getId());
            currentUser.setGoogleToken(provider.getAccessToken());
            userRepository.save(currentUser);
            SyncAccount account = new SyncAccount();
            if(currentUser.getPcloudAccount() != null) {
                account.setSource("pcloud");
            } else if (currentUser.getDropboxAccount() != null) {
                account.setSource("dropbox");
            } else if (currentUser.getBoxAccount() != null) {
                account.setSource("box");
            } else if (currentUser.getOnedriveAccount() != null) {
                account.setSource("onedrive");
            } else if (currentUser.getYandexAccount() != null) {
                account.setSource("yandex");
            } else if (currentUser.getHidriveAccount() != null) {
                account.setSource("hidrive");
            }else {
                return null;
            }
            account.setDestination("google");
            fullSyncronize(account, auth);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "OK";
    }

    @RequestMapping(value = "/addServiceDropbox", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceDropbox(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException, APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setDropboxAccount(provider.getAccount().getId());
            currentUser.setDropboxToken(provider.getAccessToken());
            userRepository.save(currentUser);
            SyncAccount account = new SyncAccount();
            if(currentUser.getGoogleAccount() != null) {
                account.setSource("google");
            } else if (currentUser.getPcloudAccount() != null) {
                account.setSource("pcloud");
            } else if (currentUser.getBoxAccount() != null) {
                account.setSource("box");
            } else if (currentUser.getOnedriveAccount() != null) {
                account.setSource("onedrive");
            } else if (currentUser.getYandexAccount() != null) {
                account.setSource("yandex");
            } else if (currentUser.getHidriveAccount() != null) {
                account.setSource("hidrive");
            }else {
                return null;
            }
            account.setDestination("dropbox");
            fullSyncronize(account, auth);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "OK";
    }

    @RequestMapping(value = "/addServiceOnedrive", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceOnedrive(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException, APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setOnedriveAccount(provider.getAccount().getId());
            currentUser.setOnedriveToken(provider.getAccessToken());
            userRepository.save(currentUser);
            SyncAccount account = new SyncAccount();
            if(currentUser.getGoogleAccount() != null) {
                account.setSource("google");
            } else if (currentUser.getDropboxAccount() != null) {
                account.setSource("dropbox");
            } else if (currentUser.getBoxAccount() != null) {
                account.setSource("box");
            } else if (currentUser.getPcloudAccount() != null) {
                account.setSource("pcloud");
            } else if (currentUser.getYandexAccount() != null) {
                account.setSource("yandex");
            } else if (currentUser.getHidriveAccount() != null) {
                account.setSource("hidrive");
            } else {
                return null;
            }
            account.setDestination("onedrive");
            fullSyncronize(account, auth);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "OK";
    }

    @RequestMapping(value = "/addServiceBox", method = RequestMethod.POST)
    @ResponseBody
    public String addServiceBox(@RequestBody Provider provider, Authentication auth) throws UsernameNotFoundException, AuthenticationException, UnsupportedEncodingException {
        try {
            UserDetails user = (UserDetails) auth.getPrincipal();
            User currentUser = userRepository.findByUsername(user.getUsername());
            currentUser.setBoxAccount(provider.getAccount().getId());
            currentUser.setBoxToken(provider.getAccessToken());
            userRepository.save(currentUser);
            SyncAccount account = new SyncAccount();
            if(currentUser.getGoogleAccount() != null) {
                account.setSource("google");
            } else if (currentUser.getDropboxAccount() != null) {
                account.setSource("dropbox");
            } else if (currentUser.getPcloudAccount() != null) {
                account.setSource("pcloud");
            } else if (currentUser.getOnedriveAccount() != null) {
                account.setSource("onedrive");
            } else if (currentUser.getYandexAccount() != null) {
                account.setSource("yandex");
            } else if (currentUser.getHidriveAccount() != null) {
                account.setSource("hidrive");
            } else {
                return null;
            }
            account.setDestination("box");
            fullSyncronize(account, auth);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        } catch (APIConnectionException | InvalidRequestException | APIException e) {
            e.printStackTrace();
        }
        return "OK";
    }

    //====================Add providers=========================

    //=======================New sync===========================

    /** Full Synchronize
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
    private void fullSyncronize(SyncAccount syncAccount, Authentication auth) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException {
        logger.debug("In 'sycno' method");

        String sourceAccount, sourceToken, destinationAccount, destinationToken;
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
/*
        for(int i = 0; i < threads.size(); i++) {
            ScannerWorker thread = threads.get(i);
            if(thread.getName().equals(user.getUsername())) {
                thread.setRunning(false);
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        switch (syncAccount.getSource()) {
            case "google":
                sourceAccount = user.getGoogleAccount();
                sourceToken = user.getGoogleToken();
                break;
            case "dropbox":
                sourceAccount = user.getDropboxAccount();
                sourceToken = user.getDropboxToken();
                break;
            case "onedrive":
                sourceAccount = user.getOnedriveAccount();
                sourceToken = user.getOnedriveToken();
                break;
            case "box":
                sourceAccount = user.getBoxAccount();
                sourceToken = user.getBoxToken();
                break;
            case "yandex":
                sourceAccount = user.getYandexAccount();
                sourceToken = user.getYandexToken();
                break;
            case "hidrive":
                sourceAccount = user.getHidriveAccount();
                sourceToken = user.getHidriveToken();
                break;
            case "pcloud":
                sourceAccount = user.getPcloudAccount();
                sourceToken= user.getPcloudToken();
                break;
            default:
                return;
        }

        switch (syncAccount.getDestination()) {
            case "google":
                destinationAccount = user.getGoogleAccount();
                destinationToken = user.getGoogleToken();
                break;
            case "dropbox":
                destinationAccount = user.getDropboxAccount();
                destinationToken = user.getDropboxToken();
                break;
            case "onedrive":
                destinationAccount = user.getOnedriveAccount();
                destinationToken = user.getOnedriveToken();
                break;
            case "box":
                destinationAccount = user.getBoxAccount();
                destinationToken = user.getBoxToken();
                break;
            case "yandex":
                destinationAccount = user.getYandexAccount();
                destinationToken = user.getYandexToken();
                break;
            case "hidrive":
                destinationAccount = user.getHidriveAccount();
                destinationToken = user.getHidriveToken();
                break;
            case "pcloud":
                destinationAccount = user.getPcloudAccount();
                destinationToken= user.getPcloudToken();
                break;
            default:
                return;
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
                    Boolean contains = false;
                    for(Metadata file : sourceList.getMetadataList()) {
                        if(file.name.equals(data.name) && file.type.equals(data.type)) {
                            contains = true;
                        }
                    }
                    if(!contains) {
                        destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                        logger.debug(String.format("File %s has been deleted from destination storage (if)", data.name));
                        for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                            if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                                forRemove.add(destinationList.getMetadataList().get(i));
                            }
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
        }*/

        ScannerWorker worker = new ScannerWorker(user);

        int accountSum = 0;

        if(user.getGoogleAccount() != null) {
            accountSum++;
        }
        if (user.getDropboxAccount() != null) {
            accountSum++;
        }
        if (user.getBoxAccount() != null) {
            accountSum++;
        }
        if (user.getOnedriveAccount() != null) {
            accountSum++;
        }
        if (user.getYandexAccount() != null) {
            accountSum++;
        }
        if (user.getHidriveAccount() != null) {
            accountSum++;
        }
        if (user.getPcloudAccount() != null) {
            accountSum++;
        }

        if(accountSum >= 2) {
            worker.setName(user.getUsername());
            worker.start();
            threads.add(worker);
        }
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
    @SuppressWarnings("Duplicates")
    private MetadataCounter listLoop(KClient client, MetadataCounter list) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        MetadataCollection temp = new MetadataCollection();

        int i = list.getCounter();
        for (; i < list.getMetadataList().size(); i++) {
            if (list.getMetadataList().get(i).type.equals("folder")) {
                if (list.getMetadataList().get(i).name.equals(".hidrive")){
                    continue;
                }
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


}
