package edu.java.bot.model;

import edu.java.bot.service.commands.resourcesHandlers.Link;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

//Заглушка, далее БД
@Getter
public class User {
    private final Long id;
    private final List<Link> links;

    public User(Long id) {
        this.id = id;
        this.links = new ArrayList<>();
    }

    public void addLink(Link link) {
        links.add(link);
    }
}
