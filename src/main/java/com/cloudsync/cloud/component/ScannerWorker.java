package com.cloudsync.cloud.component;

import com.cloudsync.cloud.model.MetadataCounter;
import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.model.WorkerAccount;
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
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class ScannerWorker extends Thread {

    private UserRepository userRepository;

    private Boolean firstStart = true;
    private User user;
    private volatile boolean running = true;
    private static final Logger logger = LogManager.getLogger(ScannerWorker.class);
    private ArrayList<WorkerAccount> accounts;

    public ScannerWorker(User user, UserRepository userRepository) {
        this.user = user;
        this.userRepository = userRepository;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public void setFirstStart(Boolean firstStart) {
        this.firstStart = firstStart;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        while (running) {

            logger.debug("Worker executing now");

            accounts = new ArrayList<WorkerAccount>();
            List<MetadataCounter> metadataCollection = new ArrayList<>();

            Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

            if (user.getBoxAccount() != null) {
                WorkerAccount provider = new WorkerAccount(user.getBoxAccount(), user.getBoxToken());
                accounts.add(provider);
            }

            if (user.getGoogleAccount() != null) {
                WorkerAccount provider = new WorkerAccount(user.getGoogleAccount(), user.getGoogleToken());
                accounts.add(provider);
            }

            if (user.getDropboxAccount() != null) {
                WorkerAccount provider = new WorkerAccount(user.getDropboxAccount(), user.getDropboxToken());
                accounts.add(provider);
            }

            if (user.getOnedriveAccount() != null) {
                WorkerAccount provider = new WorkerAccount(user.getOnedriveAccount(), user.getOnedriveToken());
                accounts.add(provider);
            }

            if (user.getYandexAccount() != null) {
                WorkerAccount provider = new WorkerAccount(user.getYandexAccount(), user.getYandexToken());
                accounts.add(provider);
            }

            if (user.getHidriveAccount() != null) {
                WorkerAccount provider = new WorkerAccount(user.getHidriveAccount(), user.getHidriveToken());
                accounts.add(provider);
            }

            if (user.getPcloudAccount() != null) {
                WorkerAccount provider = new WorkerAccount(user.getPcloudAccount(), user.getPcloudToken());
                accounts.add(provider);
            }

            if (firstStart) {
                System.out.println("in firstStart");
                ArrayList<MetadataCounter> contentsOfAccounts = new ArrayList<>();
                ArrayList<String> accountsAccs = new ArrayList<>();
                for (WorkerAccount account : accounts) {
                    System.out.println("in Accounts");
                    KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                    Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                    MetadataCollection source = null;
                    try {
                        source = sourceStorage.contents(null, Folder.class, "root");
                    } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                    sourceList = addRootTags(sourceList);
                    try {
                        sourceList = listLoop(sourceStorage, sourceList);
                    } catch (APIException | UnsupportedEncodingException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                        e.printStackTrace();
                    }
                    contentsOfAccounts.add(sourceList);
                    accountsAccs.add(account.getAccount());
                }
                ArrayList<Metadata> objectsList = new ArrayList<>();
                for (int i = 0; i < contentsOfAccounts.size(); i++) {
                    for (int j = 0; j < contentsOfAccounts.size(); j++) {
                        if (i == j) {
                            continue;
                        }
                        List<Metadata> list = new ArrayList<>(contentsOfAccounts.get(i).getMetadataList());
                        List<Metadata> targetList = new ArrayList<>(contentsOfAccounts.get(j).getMetadataList());
                        list.removeAll(targetList);
                        List<Metadata> forRemove = new ArrayList<>();
                        for (int k = 0; k < list.size(); k++) {
                            for (int m = 0; m < objectsList.size(); m++) {
                                if (objectsList.get(m).mime_type.equals(list.get(k).mime_type) && objectsList.get(m).id.equals(list.get(k).id)) {
                                    forRemove.add(list.get(k));
                                }
                            }
                        }
                        list.removeAll(forRemove);
                        objectsList.addAll(list);
                    }

                }
                HashSet<Metadata> set = new HashSet<>(objectsList);
                while (true) {
                    set = fileSendAndDeleteForSynchronization(set, objectsList, accountsAccs);
                    if (set.size() == 0) {
                        break;
                    }
                }

                for (WorkerAccount account : accounts) {
                    for (WorkerAccount innerAccount : accounts) {
                        if (!account.equals(innerAccount)) {

                            String sourceAccount = account.getAccount();
                            String sourceToken = account.getToken();
                            String destinationToken = innerAccount.getToken();
                            String destinationAccount = innerAccount.getAccount();
                            KClient sourceStorage = new KClient(sourceToken, sourceAccount, null);
                            KClient destinationStorage = new KClient(destinationToken, destinationAccount, null);

                            Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

                            MetadataCollection source = null;
                            try {
                                source = sourceStorage.contents(null, Folder.class, "root");
                            } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            MetadataCollection destination = null;
                            try {
                                destination = destinationStorage.contents(null, Folder.class, "root");
                            } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                            MetadataCounter destinationList = new MetadataCounter(0, destination.objects);
                            sourceList = addRootTags(sourceList);
                            destinationList = addRootTags(destinationList);
                            try {
                                sourceList = listLoop(sourceStorage, sourceList);
                            } catch (APIException | UnsupportedEncodingException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                                e.printStackTrace();
                            }
                            try {
                                destinationList = listLoop(destinationStorage, destinationList);
                            } catch (APIException | UnsupportedEncodingException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                                e.printStackTrace();
                            }
                            for (Metadata file : sourceList.getMetadataList()) {
                                if (file.type.equals("file")) {
                                    for (Metadata data : destinationList.getMetadataList()) {
                                        if (data.type.equals("file")) {
                                            if (file.parent.name.equals(data.parent.name) && file.mime_type.equals(data.mime_type) && !file.size.equals(data.size)) {

                                                Instant instant1 = Instant.parse(file.modified);
                                                Instant instant2 = Instant.parse(data.modified);

                                                if (instant1.isBefore(instant2)) {
                                                    try {
                                                        destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                                                    } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                                                        e.printStackTrace();
                                                    }
                                                    HashMap<String, Object> fileParams = new HashMap<>();
                                                    for (Metadata Fdata : destinationList.getMetadataList()) {
                                                        if (Fdata.type.equals("folder")) {
                                                            if (Fdata.mime_type.equals(file.parent.name)) {
                                                                fileParams.put("parent_id", Fdata.id);
                                                                break;
                                                            }

                                                        }
                                                    }

                                                    if (destinationList.isGoogle()) {
                                                        StringBuilder name = new StringBuilder(file.name);
                                                        if (file.name.contains(".")) {
                                                            int index = file.name.indexOf(".");

                                                            name.insert(index, "(" + RandomStringUtils.random(4, true, false) + ")");
                                                        } else {
                                                            name.append("(" + RandomStringUtils.random(4, true, false) + ")");

                                                        }
                                                        fileParams.put("name", name);
                                                    } else {
                                                        String name = file.name.replaceAll("\\(.*\\)", "");
                                                        fileParams.put("name", name);
                                                    }
                                                    if (fileParams.size() <= 1) {
                                                        fileParams.put("parent_id", "root");
                                                    }
                                                    fileParams.put("account", destinationAccount);
                                                    try {
                                                        com.kloudless.model.File.copy(file.id, sourceAccount, fileParams);
                                                    } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                                                        e.printStackTrace();
                                                    }
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    logger.debug(String.format("File %s has been recopied ", file.name));
                                                }


                                                for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                                    if (destinationList.getMetadataList().get(i).mime_type.equals(data.mime_type) && destinationList.getMetadataList().get(i).type.equals(data.type)) {
                                                        destinationList.getMetadataList().get(i).modified = file.modified;
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                firstStart = false;
            }

            for (WorkerAccount account : accounts) {
                for (WorkerAccount innerAccount : accounts) {

                    if (account.equals(innerAccount)) {
                        continue;
                    } else {
                        String sourceAccount = account.getAccount();
                        String sourceToken = account.getToken();
                        String destinationToken = innerAccount.getToken();
                        String destinationAccount = innerAccount.getAccount();
                        try {
                            requestAdd(sourceAccount, sourceToken, destinationAccount, destinationToken);
                        } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (WorkerAccount account : accounts) {
                for (WorkerAccount innerAccount : accounts) {

                    if (account.equals(innerAccount)) {
                        continue;
                    } else {
                        String sourceAccount = account.getAccount();
                        String sourceToken = account.getToken();
                        String destinationToken = innerAccount.getToken();
                        String destinationAccount = innerAccount.getAccount();
                        try {
                            requestDelete(sourceAccount, sourceToken, destinationAccount, destinationToken);
                        } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }

        logger.debug("Worker exiting now");


    }

    @SuppressWarnings("Duplicates")
    private HashSet<Metadata> fileSendAndDeleteForSynchronization(HashSet<Metadata> set, ArrayList<Metadata> objectsList, ArrayList<String> accountsAccs) {
        ArrayList<Metadata> removingSet = new ArrayList<>();
        for (Metadata metadata : set) {
            ArrayList<MetadataCounter> contentsOfAccounts = new ArrayList<>();
            for (WorkerAccount account : accounts) {
                System.out.println("in Accounts");
                KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                MetadataCollection source = null;
                try {
                    source = sourceStorage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                sourceList = addRootTags(sourceList);
                try {
                    sourceList = listLoop(sourceStorage, sourceList);
                } catch (APIException | UnsupportedEncodingException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                    e.printStackTrace();
                }
                contentsOfAccounts.add(sourceList);

            }
            int counter = Collections.frequency(objectsList, metadata);
            if (metadata.type.equals("file")) {
                if (counter > 1) {
                    boolean flag = false;
                    for (Metadata metadata1 : set) {
                        if (metadata1.type.equals("folder")) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        continue;
                    }
                    MetadataCounter destinationList;
                    for (int i = 0; i < contentsOfAccounts.size(); i++) {
                        destinationList = contentsOfAccounts.get(i);
                        boolean exists = destinationList.getMetadataList().stream().anyMatch(x -> x.mime_type.equals(metadata.mime_type) && x.type.equals(metadata.type) && x.parent.name.equals(metadata.parent.name));
                        if (!exists) {
                            HashMap<String, Object> fileParams = new HashMap<>();
                            for (Metadata data : destinationList.getMetadataList()) {
                                if (data.type.equals("folder")) {
                                    if (data.mime_type.equals(metadata.parent.name)) {
                                        fileParams.put("parent_id", data.id);
                                        break;
                                    }

                                }
                            }


                            if (destinationList.isGoogle()) {
                                StringBuilder name = new StringBuilder(metadata.name);
                                if (metadata.name.contains(".")) {
                                    int index = metadata.name.indexOf(".");

                                    name.insert(index, "(" + RandomStringUtils.random(4, true, false) + ")");
                                } else {
                                    name.append("(" + RandomStringUtils.random(4, true, false) + ")");

                                }
                                fileParams.put("name", name);
                            } else {
                                String name = metadata.name.replaceAll("\\(.*\\)", "");
                                fileParams.put("name", name);
                            }

                            if (fileParams.size() <= 1) {
                                if (!metadata.parent.name.equals("root")) break;
                                fileParams.put("parent_id", "root");
                            }

                            fileParams.put("account", accountsAccs.get(i));
                            for (WorkerAccount account : accounts) {
                                try {
                                    com.kloudless.model.File.copy(metadata.id, account.getAccount(), fileParams);
                                    removingSet.add(metadata);
                                    logger.debug("File {} has been copied with params: {}", metadata.name, fileParams);
                                    break;
                                } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                                    e.printStackTrace();
                                }
                            }


                        }


                    }
                } else if (counter == 1) {
                    for (WorkerAccount account : accounts) {
                        KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                        MetadataCollection source = null;
                        try {
                            source = sourceStorage.contents(null, Folder.class, "root");
                        } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                        sourceList = addRootTags(sourceList);
                        try {
                            sourceList = listLoop(sourceStorage, sourceList);
                        } catch (APIException | UnsupportedEncodingException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                            e.printStackTrace();
                        }
                        try {
                            sourceStorage.delete(null, com.kloudless.model.File.class, metadata.id);
                            removingSet.add(metadata);
                        } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            if (metadata.type.equals("folder")) {
                if (counter > 1) {
                    for (WorkerAccount account : accounts) {
                        KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                        MetadataCollection source = null;
                        try {
                            source = sourceStorage.contents(null, Folder.class, "root");
                        } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                        sourceList = addRootTags(sourceList);
                        try {
                            sourceList = listLoop(sourceStorage, sourceList);
                        } catch (APIException | UnsupportedEncodingException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                            e.printStackTrace();
                        }

                        HashMap<String, Object> fileParams = new HashMap<>();
                        for (Metadata data : sourceList.getMetadataList()) {
                            if (data.type.equals("folder")) {
                                if (data.mime_type.equals(metadata.parent.name)) {
                                    fileParams.put("parent_id", data.id);
                                    break;
                                }

                            }
                        }
                        if (sourceList.isGoogle()) {
                            StringBuilder name = new StringBuilder(metadata.name);
                            if (metadata.name.contains(".")) {
                                int index = metadata.name.indexOf(".");

                                name.insert(index, "(" + RandomStringUtils.random(4, true, false) + ")");
                            } else {
                                name.append("(" + RandomStringUtils.random(4, true, false) + ")");

                            }
                            fileParams.put("name", name);
                        } else {
                            String name = metadata.name.replaceAll("\\(.*\\)", "");
                            fileParams.put("name", name);
                        }

                        if (fileParams.size() <= 1) {
                            if (!metadata.parent.name.equals("root")) break;
                            fileParams.put("parent_id", "root");
                        }
                        try {
                            sourceStorage.create(null, Folder.class, fileParams);
                            removingSet.add(metadata);
                        } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } else if (counter == 1) {
                    for (WorkerAccount account : accounts) {
                        boolean flag = false;
                        for (Metadata metadata1 : set) {
                            if (metadata1.type.equals("file")) {
                                flag = true;
                            }
                        }
                        if (flag) {
                            continue;
                        }
                        KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                        MetadataCollection source = null;
                        try {
                            source = sourceStorage.contents(null, Folder.class, "root");
                        } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                        sourceList = addRootTags(sourceList);
                        try {
                            sourceList = listLoop(sourceStorage, sourceList);
                        } catch (APIException | UnsupportedEncodingException | AuthenticationException | InvalidRequestException | APIConnectionException e) {
                            e.printStackTrace();
                        }

                        for (Metadata data : sourceList.getMetadataList()) {
                            if (data.type.equals("folder") && data.mime_type.equals(metadata.mime_type) && data.parent.name.equals(metadata.parent.name)) {
                                try {
                                    sourceStorage.delete(null, Folder.class, data.id);
                                    removingSet.add(metadata);
                                } catch (APIException | AuthenticationException | InvalidRequestException | APIConnectionException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                }
            }

        }
        set.removeAll(removingSet);
        return set;
    }


    @SuppressWarnings("Duplicates")
    private MetadataCounter addRootTags(MetadataCounter sourceList) {

        String username = this.getName();
        User user = userRepository.findByUsername(username);
        Integer num = null;
        for (int i = 0; i < sourceList.getMetadataList().size(); i++) {
            if (user.getGoogleAccount() != null) {
                if (user.getGoogleAccount().equals(sourceList.getMetadataList().get(i).account.toString())) {
                    sourceList.setGoogle(true);
                }
            }
            sourceList.getMetadataList().get(i).parent.Id = "root";
            sourceList.getMetadataList().get(i).parent.name = "root";
            String noWhitespace = sourceList.getMetadataList().get(i).name.replaceAll("\\s", "");
            sourceList.getMetadataList().get(i).mime_type = noWhitespace.replaceAll("\\(.*\\)", "").toUpperCase();
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

        return sourceList;

    }

    @SuppressWarnings("Duplicates")
    private MetadataCounter listLoop(KClient client, MetadataCounter list) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {


        String username = this.getName();
        User user = userRepository.findByUsername(username);

        MetadataCollection temp = new MetadataCollection();

        int i = list.getCounter();
        for (; i < list.getMetadataList().size(); i++) {
            if (list.getMetadataList().get(i).type.equals("folder")) {
                temp = client.contents(null, Folder.class, list.getMetadataList().get(i).id);
                logger.debug(String.format("Contents of folder %s have gotten", list.getMetadataList().get(i).name));
                for (int j = 0; j < temp.objects.size(); j++) {
                    if (user.getGoogleAccount() != null) {
                        if (user.getGoogleAccount().equals(temp.objects.get(j).account.toString())) {
                            list.setGoogle(true);
                        }
                    }
                    temp.objects.get(j).parent.Id = list.getMetadataList().get(i).id;
                    String noWhitespaceFolder = temp.objects.get(j).parent.name.replaceAll("\\s", "");
                    temp.objects.get(j).parent.name = noWhitespaceFolder.replaceAll("\\(.*\\)", "").toUpperCase();
                    String noWhitespace = temp.objects.get(j).name.replaceAll("\\s", "");
                    temp.objects.get(j).mime_type = noWhitespace.replaceAll("\\(.*\\)", "").toUpperCase();
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

    @SuppressWarnings("Duplicates")
    private void deleteFolders(Metadata sourceFolder, String token) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        int counter = 0;
        for (WorkerAccount account : accounts) {
            if (account.getToken().equals(token)) {
                continue;
            }
            KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
            Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
            MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
            MetadataCounter sourceList = new MetadataCounter(0, source.objects);
            sourceList = addRootTags(sourceList);
            sourceList = listLoop(sourceStorage, sourceList);

            for (Metadata folder : sourceList.getMetadataList()) {
                if (folder.type.equals("folder") && folder.mime_type.equals(sourceFolder.mime_type) && folder.parent.name.equals(sourceFolder.parent.name)) {
                    counter++;
                }
            }
        }

        if (counter == 0) {

        } else {
            ArrayList<KClient> storageList = new ArrayList<>();
            ArrayList<MetadataCounter> sourcesList = new ArrayList<>();
            for (WorkerAccount account : accounts) {
                KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
                MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                sourceList = addRootTags(sourceList);
                sourceList = listLoop(sourceStorage, sourceList);
                storageList.add(sourceStorage);
                sourcesList.add(sourceList);
            }
            boolean contains = false;
            for (MetadataCounter collection : sourcesList) {
                contains = collection.getMetadataList().stream().anyMatch(x -> x.parent.name.equals(sourceFolder.mime_type) && !sourceFolder.name.equals("root"));

            }
            if (!contains) {
                for (int i = 0; i < sourcesList.size(); i++) {
                    for (Metadata data : sourcesList.get(i).getMetadataList()) {
                        if (data.type.equals("folder") && data.mime_type.equals(sourceFolder.mime_type) && data.parent.name.equals(sourceFolder.parent.name)) {
                            storageList.get(i).delete(null, Folder.class, data.id);
                        }
                    }
                }
            }

        }

    }

    @SuppressWarnings("Duplicates")
    private void sendFolders(Metadata sourceFolder, String token) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        int counter = 0;
        for (WorkerAccount account : accounts) {
            if (account.getToken().equals(token)) {
                continue;
            }
            KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
            Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
            MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
            MetadataCounter sourceList = new MetadataCounter(0, source.objects);
            sourceList = addRootTags(sourceList);
            sourceList = listLoop(sourceStorage, sourceList);

            for (Metadata folder : sourceList.getMetadataList()) {
                if (folder.type.equals("folder") && folder.mime_type.equals(sourceFolder.mime_type) && folder.parent.name.equals(sourceFolder.parent.name)) {
                    counter++;
                }
            }
        }

        if (counter == 0) {
            for (WorkerAccount account : accounts) {
                if (account.getToken().equals(token)) {
                    continue;
                }
                KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
                MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                sourceList = addRootTags(sourceList);
                sourceList = listLoop(sourceStorage, sourceList);

                HashMap<String, Object> fileParams = new HashMap<>();
                for (Metadata data : sourceList.getMetadataList()) {
                    if (data.type.equals("folder")) {
                        if (data.mime_type.equals(sourceFolder.parent.name)) {
                            fileParams.put("parent_id", data.id);
                            break;
                        }

                    }
                }

                if (sourceList.isGoogle()) {
                    StringBuilder name = new StringBuilder(sourceFolder.name);
                    if (sourceFolder.name.contains(".")) {
                        int index = sourceFolder.name.indexOf(".");

                        name.insert(index, "(" + RandomStringUtils.random(4, true, false) + ")");
                    } else {
                        name.append("(" + RandomStringUtils.random(4, true, false) + ")");

                    }
                    fileParams.put("name", name);
                } else {
                    String name = sourceFolder.name.replaceAll("\\(.*\\)", "");
                    fileParams.put("name", name);
                }
                if (fileParams.size() <= 1) {
                    if (sourceFolder.parent.name.equals("root")) {
                        fileParams.put("parent_id", "root");
                    } else {
                        continue;
                    }
                }
                sourceStorage.create(null, Folder.class, fileParams);
            }
        } else {


        }

    }


    @SuppressWarnings("Duplicates")
    public MetadataCounter requestAdd(String sourceAccount, String sourceToken, String destinationAccount, String destinationToken) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException {
        logger.debug("In 'requestAdd' method with params: source: {}, destination: {}", sourceAccount, destinationAccount);

        KClient sourceStorage = new KClient(sourceToken, sourceAccount, null);
        KClient destinationStorage = new KClient(destinationToken, destinationAccount, null);
        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

        MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
        MetadataCollection destination = destinationStorage.contents(null, Folder.class, "root");
        MetadataCounter sourceList = new MetadataCounter(0, source.objects);
        MetadataCounter destinationList = new MetadataCounter(0, destination.objects);
        sourceList = addRootTags(sourceList);
        destinationList = addRootTags(destinationList);
        sourceList = listLoop(sourceStorage, sourceList);
        destinationList = listLoop(destinationStorage, destinationList);


        List<Metadata> reversed = new ArrayList<>(sourceList.getMetadataList());
        Collections.reverse(reversed);

        for (Metadata mData : reversed) {
            if (mData.type.equals("folder")) {
                boolean contains = destinationList.getMetadataList().stream().anyMatch(x -> x.mime_type.equals(mData.mime_type) && x.parent.name.equals(mData.parent.name));
                if (!contains) {
                    sendFolders(mData, sourceToken);
                }
            }
        }


        for (Metadata mData : sourceList.getMetadataList()) {
            if (mData.type.equals("file")) {
                boolean contains = destinationList.getMetadataList().stream().anyMatch(x -> x.mime_type.equals(mData.mime_type) && x.parent.name.equals(mData.parent.name));
                if (!contains) {
                    Instant now = Instant.now();
                    Instant modified = Instant.parse(mData.modified);
                    modified = modified.plusSeconds(300);
                    if (modified.isAfter(now)) {
                        HashMap<String, Object> fileParams = new HashMap<>();
                        for (Metadata data : destinationList.getMetadataList()) {
                            if (data.type.equals("folder")) {
                                if (data.mime_type.equals(mData.parent.name)) {
                                    fileParams.put("parent_id", data.id);
                                    break;
                                }

                            }
                        }

                        if (destinationList.isGoogle()) {
                            StringBuilder name = new StringBuilder(mData.name);
                            if (mData.name.contains(".")) {
                                int index = mData.name.indexOf(".");

                                name.insert(index, "(" + RandomStringUtils.random(4, true, false) + ")");
                            } else {
                                name.append("(" + RandomStringUtils.random(4, true, false) + ")");

                            }
                            fileParams.put("name", name);
                        } else {
                            String name = mData.name.replaceAll("\\(.*\\)", "");
                            fileParams.put("name", name);
                        }

                        if (fileParams.size() <= 1) {
                            if (!mData.parent.name.equals("root")) break;
                            fileParams.put("parent_id", "root");
                        }
                        fileParams.put("account", destinationAccount);
                        com.kloudless.model.File.copy(mData.id, sourceAccount, fileParams);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        logger.debug("File {} has been copied with params: {}", mData.name, fileParams);
                    }
                }
            }
        }

        return sourceList;
    }
    //

    @SuppressWarnings("Duplicates")
    public MetadataCounter requestDelete(String sourceAccount, String sourceToken, String destinationAccount, String destinationToken) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException, ParseException {
        logger.debug("In 'requestDelete' method with params: source: {}, destination: {}", sourceAccount, destinationAccount);

        KClient sourceStorage = new KClient(sourceToken, sourceAccount, null);
        KClient destinationStorage = new KClient(destinationToken, destinationAccount, null);

        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

        MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
        MetadataCollection destination = destinationStorage.contents(null, Folder.class, "root");
        MetadataCounter sourceList = new MetadataCounter(0, source.objects);
        MetadataCounter destinationList = new MetadataCounter(0, destination.objects);
        sourceList = addRootTags(sourceList);
        destinationList = addRootTags(destinationList);
        sourceList = listLoop(sourceStorage, sourceList);
        destinationList = listLoop(destinationStorage, destinationList);

        List<Metadata> forRemove = new ArrayList<>();
        for (Metadata data : destinationList.getMetadataList()) {
            if (data.type.equals("file")) {

                boolean isNameHasDigit = data.name.matches(".*\\d+.*");
                if (isNameHasDigit) {
                    destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                    break;
                }
                long containsSame = destinationList.getMetadataList().stream().filter(x -> data.mime_type.equals(x.mime_type)).count();
                if (containsSame >= 2) {
                    destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                    break;
                }

                boolean contains = sourceList.getMetadataList().stream().anyMatch(x -> x.mime_type.equals(data.mime_type) && x.parent.name.equals(data.parent.name));
                if (!contains) {

                    Instant now = Instant.now();
                    Instant modified = Instant.parse(data.modified);
                    modified = modified.plusSeconds(300);
                    if (modified.isBefore(now)) {
                        destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                        logger.debug("File {} has been deleted from destination storage (if)", data.name);
                        for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                            if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                                forRemove.add(destinationList.getMetadataList().get(i));
                            }
                        }
                    }


                } else {
                    for (Metadata file : sourceList.getMetadataList()) {
                        if (file.type.equals(data.type)) {
                            if (!file.parent.name.equals(data.parent.name) && file.mime_type.equals(data.mime_type)) {
                                Instant now = Instant.now();
                                Instant modified = Instant.parse(data.modified);
                                modified = modified.plusSeconds(300);
                                if (modified.isBefore(now)) {
                                    destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                                    logger.debug("File {} has been deleted from destination storage (else)", data.name);
                                    for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                        if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                                            forRemove.add(destinationList.getMetadataList().get(i));
                                        }
                                    }


                                }
                            }

                            if (file.parent.name.equals(data.parent.name) && file.mime_type.equals(data.mime_type) && !file.size.equals(data.size)) {

                                Instant instant1 = Instant.parse(file.modified);
                                Instant instant2 = Instant.parse(data.modified);
                                Instant now = Instant.now();
                                now.minusSeconds(60);

                                if (instant1.isAfter(instant2) && now.isAfter(instant1)) {
                                    destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                                    HashMap<String, Object> fileParams = new HashMap<>();
                                    for (Metadata Fdata : destinationList.getMetadataList()) {
                                        if (Fdata.type.equals("folder")) {
                                            if (Fdata.mime_type.equals(file.parent.name)) {
                                                fileParams.put("parent_id", Fdata.id);
                                                break;
                                            }

                                        }
                                    }

                                    if (destinationList.isGoogle()) {
                                        StringBuilder name = new StringBuilder(file.name);
                                        if (file.name.contains(".")) {
                                            int index = file.name.indexOf(".");

                                            name.insert(index, "(" + RandomStringUtils.random(4, true, false) + ")");
                                        } else {
                                            name.append("(" + RandomStringUtils.random(4, true, false) + ")");

                                        }
                                        fileParams.put("name", name);
                                    } else {
                                        String name = file.name.replaceAll("\\(.*\\)", "");
                                        fileParams.put("name", name);
                                    }
                                    if (fileParams.size() <= 1) {
                                        fileParams.put("parent_id", "root");
                                    }
                                    fileParams.put("account", destinationAccount);
                                    com.kloudless.model.File.copy(file.id, sourceAccount, fileParams);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    logger.debug(String.format("File %s has been recopied ", file.name));
                                }


                                for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                    if (destinationList.getMetadataList().get(i).mime_type.equals(data.mime_type) && destinationList.getMetadataList().get(i).type.equals(data.type)) {
                                        destinationList.getMetadataList().get(i).modified = file.modified;
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

                boolean contains = destinationList.getMetadataList().stream().anyMatch(x -> x.parent.name.equals(data.mime_type) && !data.name.equals("root"));
                if (!contains) {
                    deleteFolders(data, destinationToken);
                }
            }

        }

        return sourceList;
    }
}
