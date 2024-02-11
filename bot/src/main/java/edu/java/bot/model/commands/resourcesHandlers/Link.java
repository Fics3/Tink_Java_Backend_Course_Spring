package edu.java.bot.model.commands.resourcesHandlers;

import java.net.URI;
import java.net.URISyntaxException;

public record Link(URI uri) {
    public static Link parse(String url) throws URISyntaxException {
        return new Link(new URI(url));
    }

}
