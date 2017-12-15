package ru.track.prefork.admin;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.track.prefork.Message;
import ru.track.prefork.Pool;
import ru.track.prefork.database.Database;
import ru.track.prefork.database.exceptions.InvalidAuthor;
import ru.track.prefork.exceptions.NoThreadSpecified;
import ru.track.prefork.helpparser.HelpOptions;
import ru.track.prefork.helpparser.HelpParser;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AdminConsole {
    private static Logger   logger   = LoggerFactory.getLogger("logger");
    private final  String[] COMMANDS = {"list", "drop", "history"};
    
    private Pool   pool;
    private String command;
    private HelpParser parser = new HelpParser();
    
    public AdminConsole(Pool pool, String command) {
        this.pool = pool;
        this.command = command;
    }
    
    public void execute() {
        String[] splitCommands = command.split(" ");
        
        String  mainCommand  = splitCommands[0];
        boolean commandFound = false;
        
        for (String availableCommand : COMMANDS) {
            if (mainCommand.equals(availableCommand)) {
                commandFound = true;
                
                try {
                    Method commandMethod = this.getClass().getDeclaredMethod(availableCommand, String[].class);
                    
                    String[] arguments = ArrayUtils.remove(splitCommands, 0);
                    commandMethod.invoke(this, (Object) arguments);
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    System.out.println(e.toString());
                    
                    commandFound = false;
                } catch (InvocationTargetException e) {
                    logger.error(e.toString() + ": " + e.getMessage());
                    
                    e.printStackTrace();
                    
                    commandFound = false;
                }
            }
        }
        
        if (!commandFound) {
            System.out.println("admin: command '" + mainCommand + "' not found!");
        }
    }
    
    private void list(String[] arguments) {
        CommandLine commandLine = getCommandLine("list", new Option[0], arguments);
        
        if (commandLine == null) {
            return;
        }
        
        Set<String> users = pool.getUsers();
        
        if (users.isEmpty()) {
            System.out.println("No users connected!");
        }
        
        users.forEach(System.out::println);
    }
    
    private void drop(String[] arguments) {
        Option[] options = {
                Option.builder("i").longOpt("client-id").desc("The client id to drop.").argName("CLIENT_ID").hasArg()
                      .required().build()
        };
        
        CommandLine commandLine = getCommandLine("drop", options, arguments);
        
        if (commandLine == null) {
            return;
        }
        
        if (commandLine.hasOption('i')) {
            int id;
            
            try {
                id = Integer.parseInt(commandLine.getOptionValue('i'));
            } catch (NumberFormatException e) {
                System.out.println("The argument of drop must be a number!");
                
                return;
            }
            
            try {
                if (pool.dropClient(id)) {
                    System.out.println("Dropped client #" + id);
                } else {
                    System.out.println("No client #" + id);
                }
            } catch (IOException | NoThreadSpecified e) {
                logger.error(e.getMessage());
                
                System.out.println("Could not drop client! Try one more time...");
            }
        }
    }
    
    private void history(String[] arguments) {
        Option[] options = {
                Option.builder("u").longOpt("user").desc("Show messages of concrete user.").hasArg().argName(
                        "USERNAME").build(), Option.builder("l").longOpt("limit").desc(
                "Maximum number of messages (by default 10).").hasArg().argName("LIMIT").build()
        };
        CommandLine commandLine = getCommandLine("history", options, arguments);
        
        if (commandLine == null) {
            return;
        }
        
        long limit;
        try {
            limit = commandLine.hasOption("l") ? Long.parseUnsignedLong(commandLine.getOptionValue("l")) : 10;
        } catch (NumberFormatException e) {
            System.out.println("The argument of limit must be a positive number!");
            
            return;
        }
        
        List<Message> messages;
        
        if (commandLine.hasOption("u")) {
            messages = getUserMessages(commandLine.getOptionValue("u"), limit);
        } else {
            messages = getHistory(limit);
        }
        
        if (!messages.isEmpty()) {
            for (Message message : messages) {
                System.out.println(message.getUsername() + ": " + message.getText() + " on " +
                                   new Timestamp(message.getTimestamp()).toString());
            }
        } else {
            System.out.println("No messages!");
        }
    }
    
    private List<Message> getUserMessages(String user, long limit) {
        Database      database = pool.getDatabase();
        List<Message> messages;
        
        try {
            messages = database.getByUser(user, limit);
        } catch (InvalidAuthor e) {
            System.out.println(e.getMessage());
            
            return new ArrayList<>();
        } catch (SQLException e) {
            logger.error(e.toString() + e.getMessage());
            
            System.out.println("Some error occurred! Try later...");
            
            return new ArrayList<>();
        }
        
        return messages;
    }
    
    private List<Message> getHistory(long limit) {
        Database      database = pool.getDatabase();
        List<Message> messages;
        
        try {
            messages = database.getHistory(
                    System.currentTimeMillis() - 60 * 60 * 24 * 7 * 1000, System.currentTimeMillis(), limit);
        } catch (SQLException e) {
            logger.error(e.toString() + e.getMessage());
            
            System.out.println("Some error occurred! Try later...");
            
            return new ArrayList<>();
        }
        
        return messages;
    }
    
    @Nullable
    private CommandLine getCommandLine(String cmdLineSyntax, Option[] options, String[] arguments) {
        parser.setCmdLineSyntax(cmdLineSyntax);
        CommandLine commandLine;
        
        HelpOptions helpOptions = new HelpOptions();
        
        for (Option option : options) {
            helpOptions.addOption(option);
        }
        
        try {
            commandLine = parser.parse(helpOptions, arguments);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            
            return null;
        }
        
        return commandLine;
    }
}
