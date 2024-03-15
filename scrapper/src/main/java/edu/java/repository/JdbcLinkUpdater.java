package edu.java.repository;

import edu.java.client.GithubClient;
import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public class JdbcLinkUpdater implements LinkUpdater {

    private LinksRepository linksRepository;
    private GithubClient githubClient;

    @Override
    public int update() {
        var staleLinks = linksRepository.findStaleLinks(Duration.ofSeconds(5L));
        staleLinks.forEach(linkModel -> {
            if(Objects.equals(URI.create(linkModel.link()).getHost(), "github.com")) {
                githubClient.checkForUpdate();
            }
        });
        return 0;
    }
}
