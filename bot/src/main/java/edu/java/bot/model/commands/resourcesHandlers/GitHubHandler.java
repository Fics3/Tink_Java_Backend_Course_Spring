package edu.java.bot.model.commands.resourcesHandlers;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class GitHubHandler extends ChainResourceHandler {

    @Override
    public String getHost() {
        return "github.com";
    }
}
