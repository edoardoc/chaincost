package io.openliberty.guides.fetcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FetcherManager {
    private static final Logger LOGGER = Logger.getLogger(FetcherManager.class.getName());
    private Queue<String> externalBinqueue = new LinkedList<>();
    private HashSet<String> externalBinset = new HashSet<String>();  // used to prevent duplicates in the queue
    
    @SuppressWarnings("unchecked")
    public FetcherManager() {
        LOGGER.info("******* FetcherManager is being loaded from the file system.");
        try (FileInputStream fis = new FileInputStream("fetchers.ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
              externalBinqueue = (Queue<String>) ois.readObject(); // Cast the result to the appropriate type
              // populate the set with the queue elements
              for (String s : externalBinqueue) {
                  externalBinset.add(s);
              }
        } catch (Exception e) {
            LOGGER.info("******* No serialized queue file found, using empty.");
        }
    }
    
    // store the bin in the queue, if it is not already present
    public void store(String iin) {
      if (externalBinset.add(iin)) {
          externalBinqueue.offer(iin);
      }
      if (externalBinset.size() % 10 == 0) {
        saveit();
      }
    }

    public String fetch() {
      String fetchedIin = externalBinqueue.poll();
      if(fetchedIin != null) {
        externalBinset.remove(fetchedIin);
        saveit(); // every delete triggers a save to the file system of the cache
      }
      return fetchedIin;
    }

    @PreDestroy
    public void saveit() {
        LOGGER.info("******* FetcherManager queue is being serialized to the file system.");
        try (FileOutputStream fos = new FileOutputStream("fetchers.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(externalBinqueue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
