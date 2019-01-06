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
import org.springframework.security.core.context.SecurityContextHolder;
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
        startWorkerAfter(auth);
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    private String addServiceHidrive(Provider provider, Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails user = (UserDetails) auth.getPrincipal();
        User currentUser = userRepository.findByUsername(user.getUsername());
        currentUser.setHidriveAccount(provider.getAccount().getId());
        currentUser.setHidriveToken(provider.getAccessToken());
        userRepository.save(currentUser);
        startWorkerAfter(auth);
        return "OK";
    }

    @SuppressWarnings("Duplicates")
    private String addServiceYandex(Provider provider, Authentication auth) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        UserDetails user = (UserDetails) auth.getPrincipal();
        User currentUser = userRepository.findByUsername(user.getUsername());
        currentUser.setYandexAccount(provider.getAccount().getId());
        currentUser.setYandexToken(provider.getAccessToken());
        userRepository.save(currentUser);
        startWorkerAfter(auth);
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
            startWorkerAfter(auth);
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
            startWorkerAfter(auth);
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
            startWorkerAfter(auth);
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
            startWorkerAfter(auth);
        } catch (UsernameNotFoundException err) {
            err.printStackTrace();
            return null;
        }
        return "OK";
    }

    //====================Add providers=========================

    @RequestMapping(value = "/removeGoogle", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeGoogle(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        user.setGoogleAccount(null);
        user.setGoogleToken(null);
        userRepository.save(user);
    }

    @RequestMapping(value = "/removeDropbox", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeDropbox(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        user.setDropboxAccount(null);
        user.setDropboxToken(null);
        userRepository.save(user);
    }

    @RequestMapping(value = "/removeBox", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeBox(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        user.setBoxAccount(null);
        user.setBoxToken(null);
        userRepository.save(user);
    }

    @RequestMapping(value = "/removeOnedrive", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeOnedrive(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        user.setOnedriveAccount(null);
        user.setOnedriveToken(null);
        userRepository.save(user);
    }

    @RequestMapping(value = "/removeYandex", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeYandex(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        user.setYandexAccount(null);
        user.setYandexToken(null);
        userRepository.save(user);
    }

    @RequestMapping(value = "/removeHidrive", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removeHidrive(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        user.setHidriveAccount(null);
        user.setHidriveToken(null);
        userRepository.save(user);
    }

    @RequestMapping(value = "/removePcloud", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void removePcloud(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        user.setPcloudAccount(null);
        user.setPcloudToken(null);
        userRepository.save(user);
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    @ResponseBody
    private String checkStatus(Authentication auth) {
        logger.info("User is checking status");
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        for(ScannerWorker thread : threads) {
            if(thread.getName().equals(userDetails.getUsername())) {
                return "Worker is running";
            }
        }
        return "Worker stopped";
    }

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

        logger.info("User requested to stop worker");

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
        ScannerWorker worker = new ScannerWorker(user, userRepository);

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

        logger.info("User requested to start worker");
    }

    @SuppressWarnings("Duplicates")
    private void startWorkerAfter(Authentication auth) {
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        for (ScannerWorker thread : threads) {
            if (thread.getName().equals(userDetails.getUsername())) {
                thread.setRunning(false);
                try {
                    thread.join();
                    threads.remove(thread);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        User user = userRepository.findByUsername(userDetails.getUsername());
        ScannerWorker worker = new ScannerWorker(user, userRepository);

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

        logger.info("User requested to start worker after adding new account");
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
        logger.info("User requested to remove all accounts");
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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if(principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        User user = userRepository.findByUsername(username);

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
                    if(user.getGoogleAccount().equals(temp.objects.get(i).account.toString())) {
                        list.setGoogle(true);
                    }
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
