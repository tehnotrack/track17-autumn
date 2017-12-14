package ru.track.prefork.argparser;

import org.apache.commons.cli.*;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

public class HelpParser extends DefaultParser {
    private HelpFormatter helpFormatter = new HelpFormatter();
    private String        cmdLineSyntax = "";
    
    private boolean printHelp(Options options, String[] arguments) throws ParseException {
        CommandLine commandLine = new DefaultParser().parse(new HelpOptions(), arguments, true);
        
        if (commandLine.hasOption('h')) {
            helpFormatter.printHelp(cmdLineSyntax, options, true);
            
            return true;
        }
        
        return false;
    }
    
    @Override
    @Nullable
    public CommandLine parse(Options options, String[] arguments, Properties properties, boolean stopAtNonOption)
            throws ParseException {
        if (!printHelp(options, arguments)) {
            return super.parse(options, arguments, properties, stopAtNonOption);
        } else {
            return null;
        }
    }
    
    public void setCmdLineSyntax(String cmdLineSyntax) {
        this.cmdLineSyntax = cmdLineSyntax;
    }
}
