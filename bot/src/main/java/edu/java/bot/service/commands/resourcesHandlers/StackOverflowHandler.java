package edu.java.bot.service.commands.resourcesHandlers;

import edu.java.bot.service.ScrapperService;

public class StackOverflowHandler extends ChainResourceHandler {
    public StackOverflowHandler(ScrapperService scrapperService) {
        super(scrapperService);
    }

    @Override
    public String getHost() {
        return "stackoverflow.com";
    }
}
