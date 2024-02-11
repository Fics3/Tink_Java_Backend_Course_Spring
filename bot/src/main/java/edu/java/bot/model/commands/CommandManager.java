package edu.java.bot.model.commands;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class CommandManager {

    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandManager() {
        commandMap.put("/start", new StartCommand());
        commandMap.put("/help", new HelpCommand());
        commandMap.put("/track", new TrackCommand());
        commandMap.put("/list", new ListCommand());
        commandMap.put("/untrack", new UntrackCommand());
    }
}
