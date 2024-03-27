package edu.java.service.updateChecker;

import edu.java.model.LinkModel;
import org.springframework.stereotype.Service;

@Service
public interface UpdateChecker {

    void processUrlUpdates(LinkModel linkModel, int updateCount);

    String getDomain();
}
