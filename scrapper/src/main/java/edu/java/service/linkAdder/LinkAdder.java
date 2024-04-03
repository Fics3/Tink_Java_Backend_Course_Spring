package edu.java.service.linkAdder;

import edu.java.model.LinkModel;
import java.net.URI;

public interface LinkAdder {

    LinkModel addLink(URI url, Long tgChatId);

    String getDomain();
}
