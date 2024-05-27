package io.openliberty.guides.fetcher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FetcherManager {
    private static final Logger LOGGER = Logger.getLogger(FetcherManager.class.getName());
    private BlockingQueue<Task> externalBinqueue = new LinkedBlockingQueue<>();
    private HashSet<String> externalBinset = new HashSet<String>();  // used to prevent duplicates in the queue
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    // Task class represents a task that can invoke an endpoint and update a cache
    private static class Task {
      int invokeEndpoint() {
          // implement the logic to invoke the external endpoint
          return 0;
      }

      void updateCache() {
          // implement the logic to update the cache
      }
    }

    private void consume() {
        while (running.get()) {
            try {
                Task task = queue.take(); // fetches an element from the queue
                int responseCode = task.invokeEndpoint(); // invokes the external endpoint

                if (responseCode == 429) {
                    executor.schedule(this::consume, 1, TimeUnit.HOURS); // sleeps for an hour
                    break;
                } else if (responseCode == 200) {
                    task.updateCache(); // updates the cache
                    // the element is automatically removed from the queue by take()
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
  
    @SuppressWarnings("unchecked")
    public FetcherManager() {
        LOGGER.info("******* FetcherManager is being loaded from the file system.");
        try (FileInputStream fis = new FileInputStream("fetchers.ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
              externalBinqueue = (BlockingQueue<Task>) ois.readObject(); // Cast the result to the appropriate type
              // populate the set with the queue elements
              for (Task s : externalBinqueue) {
                  externalBinset.add(s);
              }
        } catch (Exception e) {
            LOGGER.info("******* No serialized queue file found, using empty.");
        }
        executor.submit(this::consume);
    }
    
    public void shutdown() {
      running.set(false);
      executor.shutdown(); // stops the consumer task
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
      if (fetchedIin != null) {
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
