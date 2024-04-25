package edu.java.bot.service.commands.resourcesHandlers;

import edu.java.bot.service.ScrapperService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GitHubHandler extends ChainResourceHandler {

    public GitHubHandler(ScrapperService scrapperService) {
        super(scrapperService);
    }

    @Override
    public String getHost() {
        return "github.com";
    }
}
