package ru.track.prefork.argparser;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class HelpOptions extends Options {
    public HelpOptions() {
        super();
        
        addOption("h", "help", false, "Help command.");
        addOption(Option.builder("h").longOpt("help").desc("help").build());
    }
}
