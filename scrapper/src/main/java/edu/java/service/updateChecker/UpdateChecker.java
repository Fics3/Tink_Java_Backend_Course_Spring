package edu.java.service.updateChecker;

import edu.java.model.LinkModel;

public interface UpdateChecker {

    int processUrlUpdates(LinkModel linkModel, int updateCount);

    String getDomain();
}
