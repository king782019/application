package com.cloudsync.cloud.controller;

import com.cloudsync.cloud.component.ScannerWorker;
import com.cloudsync.cloud.model.MetadataCounter;
import com.cloudsync.cloud.model.Provider;
import com.cloudsync.cloud.model.SyncAccount;
import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.repository.UserRepository;
import com.kloudless.KClient;
import com.kloudless.exception.APIConnectionException;
import com.kloudless.exception.APIException;
import com.kloudless.exception.AuthenticationException;
import com.kloudless.exception.InvalidRequestException;
import com.kloudless.model.Folder;
import com.kloudless.model.MetadataCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    private void stopWorker(Authentication auth) {

        UserDetails user = (UserDetails) auth.getPrincipal();
        for (ScannerWorker thread : threads) {
            if (thread.getName().equals(user.getUsername())) {
                thread.setRunning(false);
                try {
                    thread.join();
                    threads.remove(thread);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        logger.debug("worker stopped");

    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    private void startWorker(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        for (ScannerWorker thread : threads) {
            if (thread.getName().equals(userDetails.getUsername())) {
                return;
            }
        }
        User user = userRepository.findByUsername(userDetails.getUsername());
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

        logger.debug("worker starting");
    }

    @RequestMapping(value = "/remove", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    private void removeAccounts(Authentication auth){
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setPassword(user.getPassword());
        newUser.setUsername(user.getUsername());
        userRepository.save(newUser);
    }

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

        for(int i = 0; i < threads.size(); i++) {
            ScannerWorker thread = threads.get(i);
            if(thread.getName().equals(user.getUsername())) {
                thread.setRunning(false);
                try {
                    thread.join();
                    threads.remove(thread);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

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
            worker.setFirstStart(false);
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
