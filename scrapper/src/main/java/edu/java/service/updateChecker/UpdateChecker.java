package edu.java.service.updateChecker;

import edu.java.model.LinkModel;
import org.springframework.stereotype.Service;

@Service
public interface UpdateChecker {

    int processUrlUpdates(LinkModel linkModel, int updateCount);

    String getDomain();
}
