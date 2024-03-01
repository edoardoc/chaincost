package io.openliberty.guides.clearingcost;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import io.openliberty.guides.clearingcost.model.ClearingcostData;
import io.openliberty.guides.clearingcost.model.ClearingcostList;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClearingcostManager {
    private static final Logger LOGGER = Logger.getLogger(ClearingcostManager.class.getName());
    private Map<String, BigDecimal> clearingcosts = Collections.synchronizedMap(new java.util.HashMap<>());

    @SuppressWarnings("unchecked")
    public ClearingcostManager() {
        LOGGER.info("******* ClearingcostManager is being loaded from the file system.");

        try (FileInputStream fis = new FileInputStream("clearingcosts.ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            clearingcosts = (Map<String, BigDecimal>) ois.readObject(); // Cast the result to the appropriate type
        } catch (Exception e) {
            LOGGER.info("******* No serialized file found, using default clearingcosts.");
            store(new ClearingcostData("US", new BigDecimal("5.0")));
            store(new ClearingcostData("GR", new BigDecimal("15.0")));
        }
    }

    
    public void store(ClearingcostData clearingcost) {
        clearingcosts.put(clearingcost.getCountry(), clearingcost.getCost().setScale(2, RoundingMode.HALF_UP));
        // whenever the cache gets incremented by 10 new clearingcosts, is serialized to the file system
        if (clearingcosts.size() % 10 == 0) {
            saveit();
        }
    }

    public void remove(String country) {
        if (clearingcosts.containsKey(country)) {
            clearingcosts.remove(country);
            saveit(); // every delete triggers a save to the file system of the cache
        }
    }

    public ClearingcostData get(String country) {
        if (clearingcosts.containsKey(country)) {
            return new ClearingcostData(country, clearingcosts.get(country));
        } else {
            return null;
        }
    }

    public ClearingcostList list() {
        final List<ClearingcostData> clearingcostslist = Collections.synchronizedList(new ArrayList<>());
        for (Map.Entry<String, BigDecimal> entry : clearingcosts.entrySet()) {
            // need to format the bigdecimal to 2 decimal places
            clearingcostslist.add(new ClearingcostData(entry.getKey(), entry.getValue()));
        }
        return new ClearingcostList(clearingcostslist);
    }

    // whenever this class destructor gets called the clearingcosts are serialized to the file system in the target directory
    @PreDestroy
    public void saveit() {
        LOGGER.info("******* ClearingcostManager is being serialized to the file system.");
        try (FileOutputStream fos = new FileOutputStream("clearingcosts.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(clearingcosts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
