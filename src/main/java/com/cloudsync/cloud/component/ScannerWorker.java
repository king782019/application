package com.cloudsync.cloud.component;

import com.cloudsync.cloud.model.MetadataCounter;
import com.cloudsync.cloud.model.User;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class ScannerWorker extends Thread {

    private User user;
    private volatile boolean running = true;
    private static final Logger logger = LogManager.getLogger(ScannerWorker.class);

    public ScannerWorker(User user){
        this.user = user;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        while(running) {

            logger.debug("Worker executing now");

            Map<MetadataCounter, Map<String, String>> account = new HashMap<>();

            List<MetadataCounter> metadataCollection = new ArrayList<>();

            Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

            if (user.getBoxAccount() != null) {
                Map<String, String> innerAccount = new HashMap<>();
                innerAccount.put(user.getBoxAccount(), user.getBoxToken());
                KClient storage = new KClient(user.getBoxToken(), user.getBoxAccount(), null);
                MetadataCollection collection = new MetadataCollection();
                try {
                    collection = storage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, collection.objects);
                sourceList = addRootTags(sourceList);
                MetadataCounter list = null;
                try {
                    list = listLoop(storage, sourceList);
                } catch (APIException | UnsupportedEncodingException | InvalidRequestException | AuthenticationException | APIConnectionException e) {
                    e.printStackTrace();
                }
                account.put(list, innerAccount);
            }

            if (user.getGoogleAccount() != null) {

                Map<String, String> innerAccount = new HashMap<>();
                innerAccount.put(user.getGoogleAccount(), user.getGoogleToken());
                KClient storage = new KClient(user.getGoogleToken(), user.getGoogleAccount(), null);
                MetadataCollection collection = new MetadataCollection();
                try {
                    collection = storage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, collection.objects);
                sourceList = addRootTags(sourceList);
                MetadataCounter list = null;
                try {
                    list = listLoop(storage, sourceList);
                } catch (APIException | UnsupportedEncodingException | InvalidRequestException | AuthenticationException | APIConnectionException e) {
                    e.printStackTrace();
                }
                account.put(list, innerAccount);
            }

            if (user.getDropboxAccount() != null) {
                Map<String, String> innerAccount = new HashMap<>();
                innerAccount.put(user.getDropboxAccount(), user.getDropboxToken());
                KClient storage = new KClient(user.getDropboxToken(), user.getDropboxAccount(), null);
                MetadataCollection collection = new MetadataCollection();
                try {
                    collection = storage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, collection.objects);
                sourceList = addRootTags(sourceList);
                MetadataCounter list = null;
                try {
                    list = listLoop(storage, sourceList);
                } catch (APIException | UnsupportedEncodingException | InvalidRequestException | AuthenticationException | APIConnectionException e) {
                    e.printStackTrace();
                }
                account.put(list, innerAccount);
            }

            if (user.getOnedriveAccount() != null) {
                Map<String, String> innerAccount = new HashMap<>();
                innerAccount.put(user.getOnedriveAccount(), user.getOnedriveToken());
                KClient storage = new KClient(user.getOnedriveToken(), user.getOnedriveAccount(), null);
                MetadataCollection collection = new MetadataCollection();
                try {
                    collection = storage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, collection.objects);
                sourceList = addRootTags(sourceList);
                MetadataCounter list = null;
                try {
                    list = listLoop(storage, sourceList);
                } catch (APIException | UnsupportedEncodingException | InvalidRequestException | AuthenticationException | APIConnectionException e) {
                    e.printStackTrace();
                }
                account.put(list, innerAccount);
            }

            if (user.getYandexAccount() != null) {
                Map<String, String> innerAccount = new HashMap<>();
                innerAccount.put(user.getYandexAccount(), user.getYandexToken());
                KClient storage = new KClient(user.getYandexToken(), user.getYandexAccount(), null);
                MetadataCollection collection = new MetadataCollection();
                try {
                    collection = storage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, collection.objects);
                sourceList = addRootTags(sourceList);
                MetadataCounter list = null;
                try {
                    list = listLoop(storage, sourceList);
                } catch (APIException | UnsupportedEncodingException | InvalidRequestException | AuthenticationException | APIConnectionException e) {
                    e.printStackTrace();
                }
                account.put(list, innerAccount);
            }

            if (user.getHidriveAccount() != null) {
                Map<String, String> innerAccount = new HashMap<>();
                innerAccount.put(user.getHidriveAccount(), user.getHidriveToken());
                KClient storage = new KClient(user.getHidriveToken(), user.getHidriveAccount(), null);
                MetadataCollection collection = new MetadataCollection();
                try {
                    collection = storage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, collection.objects);
                sourceList = addRootTags(sourceList);
                MetadataCounter list = null;
                try {
                    list = listLoop(storage, sourceList);
                } catch (APIException | UnsupportedEncodingException | InvalidRequestException | AuthenticationException | APIConnectionException e) {
                    e.printStackTrace();
                }
                account.put(list, innerAccount);
            }

            if (user.getPcloudAccount() != null) {
                Map<String, String> innerAccount = new HashMap<>();
                innerAccount.put(user.getPcloudAccount(), user.getPcloudToken());
                KClient storage = new KClient(user.getPcloudToken(), user.getPcloudAccount(), null);
                MetadataCollection collection = new MetadataCollection();
                try {
                    collection = storage.contents(null, Folder.class, "root");
                } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                MetadataCounter sourceList = new MetadataCounter(0, collection.objects);
                sourceList = addRootTags(sourceList);
                MetadataCounter list = null;
                try {
                    list = listLoop(storage, sourceList);
                } catch (APIException | UnsupportedEncodingException | InvalidRequestException | AuthenticationException | APIConnectionException e) {
                    e.printStackTrace();
                }
                account.put(list, innerAccount);
            }

            for (Map.Entry<MetadataCounter, Map<String, String>> map : account.entrySet()) {
                for (Map.Entry<MetadataCounter, Map<String, String>> innerMap : account.entrySet()) {
                    if (map.getKey().equals(innerMap.getKey())) {
                        continue;
                    } else {
                        MetadataCounter source = map.getKey();
                        MetadataCounter destination = innerMap.getKey();
                        Map<String, String> firstMap = map.getValue();
                        Map<String, String> secondMap = innerMap.getValue();
                        String sourceAccount = null;
                        String sourceToken = null;
                        for (Map.Entry<String, String> accountMap : firstMap.entrySet()) {
                            sourceAccount = accountMap.getKey();
                            sourceToken = accountMap.getValue();
                        }
                        String destinationToken = null;
                        String destinationAccount = null;
                        for (Map.Entry<String, String> accountMap : secondMap.entrySet()) {
                            destinationAccount = accountMap.getKey();
                            destinationToken = accountMap.getValue();
                        }
                        try {
                            requestAdd(source, destination, sourceAccount, sourceToken, destinationAccount, destinationToken);
                        } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (Map.Entry<MetadataCounter, Map<String, String>> map : account.entrySet()) {
                for (Map.Entry<MetadataCounter, Map<String, String>> innerMap : account.entrySet()) {
                    if (map.getKey().equals(innerMap.getKey())) {
                        continue;
                    } else {
                        MetadataCounter source = map.getKey();
                        MetadataCounter destination = innerMap.getKey();
                        Map<String, String> firstMap = map.getValue();
                        Map<String, String> secondMap = innerMap.getValue();
                        String sourceAccount = null;
                        String sourceToken = null;
                        for (Map.Entry<String, String> accountMap : firstMap.entrySet()) {
                            sourceAccount = accountMap.getKey();
                            sourceToken = accountMap.getValue();
                        }
                        String destinationToken = null;
                        String destinationAccount = null;
                        for (Map.Entry<String, String> accountMap : secondMap.entrySet()) {
                            destinationAccount = accountMap.getKey();
                            destinationToken = accountMap.getValue();
                        }
                        try {
                            requestDelete(source, destination, sourceAccount, sourceToken, destinationAccount, destinationToken);
                        } catch (APIException | AuthenticationException | APIConnectionException | InvalidRequestException | UnsupportedEncodingException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        logger.debug("Worker exiting now");


    }

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
            if(i > 100) {
                return list;
            }
            return listLoop(client, list);
        }

        return list;
    }

    @SuppressWarnings("Duplicates")
    public MetadataCounter requestAdd(MetadataCounter sourceList, MetadataCounter destinationList, String sourceAccount, String sourceToken, String destinationAccount, String destinationToken) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException {
        logger.debug("In 'requestAdd' method with params: source: {}, destination: {}", sourceAccount, destinationAccount);

        KClient sourceStorage = new KClient(sourceToken, sourceAccount, null);
        KClient destinationStorage = new KClient(destinationToken, destinationAccount, null);
        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";



        List<Metadata> reversed = new ArrayList<>(sourceList.getMetadataList());
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
                    logger.debug("Folder {} has been created in destination storage (if) with params: {}", mData.name, fileParams);

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
                            logger.debug("Folder {} has been created in destination storage (else) with params: {}", mData.name, fileParams);
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
                    logger.debug("File {} has been copied with params: {}", mData.name, fileParams);
                    mData.parent.Id = (String)fileParams.get("parent_id");
                    MetadataCollection collection = destinationStorage.contents(null, Folder.class, "root");
                    destinationList = new MetadataCounter(0, collection.objects);
                    addRootTags(destinationList);
                    destinationList = listLoop(destinationStorage, destinationList);
                }
            }
        }

        return sourceList;
    }

    @SuppressWarnings("Duplicates")
    public MetadataCounter requestDelete(MetadataCounter sourceList, MetadataCounter destinationList, String sourceAccount, String sourceToken, String destinationAccount, String destinationToken) throws UsernameNotFoundException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException, UnsupportedEncodingException, ParseException {
        logger.debug("In 'requestDelete' method with params: source: {}, destination: {}", sourceAccount, destinationAccount);

        KClient sourceStorage = new KClient(sourceToken, sourceAccount, null);
        KClient destinationStorage = new KClient(destinationToken, destinationAccount, null);

        Kloudless.apiKey = "MFGI0NG60W7up7B43V1PoosNIs1lSLyRF9AbC4VrWiqfA4Ai";

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
                            if (!file.parent.name.equals(data.parent.name) && file.name.equals(data.name)) {
                                destinationStorage.delete(null, com.kloudless.model.File.class, data.id);
                                logger.debug("File {} has been deleted from destination storage (else)", data.name);
                                for (int i = 0; i < destinationList.getMetadataList().size(); i++) {
                                    if (data.id.equals(destinationList.getMetadataList().get(i).id)) {
                                        forRemove.add(destinationList.getMetadataList().get(i));
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

        return sourceList;
    }
}
