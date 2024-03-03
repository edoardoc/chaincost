package io.openliberty.guides.iincache;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import io.openliberty.guides.iincache.model.IINCacheData;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IINCacheManager {
    private static final Logger LOGGER = Logger.getLogger(IINCacheManager.class.getName());
    private Map<String, String> IINCache = Collections.synchronizedMap(new java.util.HashMap<>());

    @SuppressWarnings("unchecked")
    public IINCacheManager() {
        LOGGER.info("******* IINCacheManager is being loaded from the file system.");

        try (FileInputStream fis = new FileInputStream("iincache.ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            IINCache = (Map<String, String>) ois.readObject(); // Cast the result to the appropriate type
        } catch (Exception e) {
            LOGGER.info("******* No serialized file found, a brand new cache");
        }
    }

    
    public void store(IINCacheData iinCacheData) {
        // whenever the cache gets incremented by 10 new IINCaches, is serialized to the file system
        IINCache.put(iinCacheData.getIin(), iinCacheData.getAlpha2());
        if (IINCache.size() % 10 == 0) {
            saveit();
        }
    }

    public void remove(String iin) {
        if (IINCache.containsKey(iin)) {
            IINCache.remove(iin);
            saveit(); // every delete triggers a save to the file system of the cache
        }
    }

    public IINCacheData get(String iin) {
        if (IINCache.containsKey(iin)) {
            return new IINCacheData(iin, IINCache.get(iin));
        } else {
            return null;
        }
    }

    // whenever this class destructor gets called the IINCaches are serialized to the file system in the target directory
    @PreDestroy
    public void saveit() {
        LOGGER.info("******* IINCache is being serialized to the file system.");
        try (FileOutputStream fos = new FileOutputStream("iincache.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(IINCache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
