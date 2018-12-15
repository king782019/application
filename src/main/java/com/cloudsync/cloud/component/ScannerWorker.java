package com.cloudsync.cloud.component;

import com.cloudsync.cloud.model.MetadataCounter;
import com.cloudsync.cloud.model.User;
import com.cloudsync.cloud.model.WorkerAccount;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.time.Instant;
import java.util.*;

public class ScannerWorker extends Thread {

    private User user;
    private volatile boolean running = true;
    private static final Logger logger = LogManager.getLogger(ScannerWorker.class);
    private ArrayList<WorkerAccount> accounts;

    public ScannerWorker(User user){
        this.user = user;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        while(running) {

            logger.debug("Worker executing now");

            accounts = new ArrayList<>();
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
    private MetadataCounter addRootTags(MetadataCounter sourceList) {
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

        return sourceList;

    }

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
    private void sendOrDeleteFolders(Metadata sourceFolder, String token) throws APIException, UnsupportedEncodingException, AuthenticationException, InvalidRequestException, APIConnectionException {
        int counter = 0;
        for(WorkerAccount account : accounts) {
            if(account.getToken().equals(token)){
                continue;
            }
            KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
            Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
            MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
            MetadataCounter sourceList = new MetadataCounter(0, source.objects);
            sourceList = addRootTags(sourceList);
            sourceList = listLoop(sourceStorage, sourceList);

            for(Metadata folder : sourceList.getMetadataList()) {
                if(folder.type.equals("folder") && folder.name.equals(sourceFolder.name)) {
                    counter++;
                }
            }
        }

        if(counter == 0) {
            for(WorkerAccount account : accounts) {
                if(account.getToken().equals(token)){
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
                        if (data.name.equals(sourceFolder.parent.name)) {
                            fileParams.put("parent_id", data.id);
                            break;
                        }

                    }
                }

                fileParams.put("name", sourceFolder.name);
                if (fileParams.size() <= 1) {
                    fileParams.put("parent_id", "root");
                }
                sourceStorage.create(null, Folder.class, fileParams);
            }
        } else {
            for(WorkerAccount account : accounts) {
                KClient sourceStorage = new KClient(account.getToken(), account.getAccount(), null);
                Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";
                MetadataCollection source = sourceStorage.contents(null, Folder.class, "root");
                MetadataCounter sourceList = new MetadataCounter(0, source.objects);
                sourceList = addRootTags(sourceList);
                sourceList = listLoop(sourceStorage, sourceList);

                for (Metadata data : sourceList.getMetadataList()) {
                    if(data.type.equals("folder") && data.name.equals(sourceFolder.name) && data.parent.name.equals(sourceFolder.parent.name)){
                        sourceStorage.delete(null, Folder.class, data.id);
                    }
                }
            }

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
                if (!destinationList.getMetadataList().contains(mData)) {
                        sendOrDeleteFolders(mData, sourceToken);
                    }
                }
            }



        for (Metadata mData : sourceList.getMetadataList()) {
            if (mData.type.equals("file")) {
                if (!destinationList.getMetadataList().contains(mData)) {
                    Instant now = Instant.now();
                    Instant modified = Instant.parse(mData.modified);
                    modified = modified.plusSeconds(300);
                    if(modified.isAfter(now)) {
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
                        logger.debug("File {} has been copied with params: {}", mData.name, fileParams);
                    }
                }
            }
        }

        return sourceList;
    }

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
                if (!sourceList.getMetadataList().contains(data)) {
                    Boolean contains = false;
                    for(Metadata file : sourceList.getMetadataList()) {
                        if(file.name.equals(data.name) && file.type.equals(data.type)) {
                            contains = true;
                        }
                    }
                    if(!contains) {
                        Instant now = Instant.now();
                        Instant modified = Instant.parse(data.modified);
                        modified = modified.plusSeconds(300);
                        if(modified.isBefore(now)) {
                            destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                            logger.debug("File {} has been deleted from destination storage (if)", data.name);
                            for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                                    forRemove.add(destinationList.getMetadataList().get(i));
                                }
                            }
                        }
                    }

                } else {
                    for (Metadata file : sourceList.getMetadataList()) {
                        if (file.type.equals(data.type)) {
                            if (!file.parent.name.equals(data.parent.name) && file.name.equals(data.name)) {
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

                            if(file.parent.name.equals(data.parent.name) && file.name.equals(data.name) && !file.size.equals(data.size)) {

                                Instant instant1 = Instant.parse(file.modified);
                                Instant instant2 = Instant.parse(data.modified);

                                if(instant1.isAfter(instant2)) {
                                    destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                                    HashMap<String, Object> fileParams = new HashMap<>();
                                    for (Metadata Fdata : destinationList.getMetadataList()) {
                                        if (Fdata.type.equals("folder")) {
                                            if (Fdata.name.equals(file.parent.name)) {
                                                fileParams.put("parent_id", Fdata.id);
                                                break;
                                            }

                                        }
                                    }


                                    fileParams.put("name", file.name);
                                    if (fileParams.size() <= 1) {
                                        fileParams.put("parent_id", "root");

                                    }
                                    fileParams.put("account", destinationAccount);
                                    com.kloudless.model.File.copy(file.id, sourceAccount, fileParams);
                                    logger.debug(String.format("File %s has been recopied ", file.name));
                                }

                                for(int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                    if(destinationList.getMetadataList().get(i).name.equals(data.name) && destinationList.getMetadataList().get(i).type.equals(data.type)) {
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
                if (!sourceList.getMetadataList().contains(data)) {

                    sendOrDeleteFolders(data, destinationToken);

                }
            }

        }

        return sourceList;
    }
}
