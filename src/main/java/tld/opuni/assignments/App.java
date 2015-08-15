package tld.opuni.assignments;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.PropertyConfigurator;

class App {

    static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static Class[] assignments = {
        Assignment11.class
    };
    
    public static void main(String ...args) {
        PropertyConfigurator.configure("./etc/log4j.properties");
        CommandLineParser parser = new GnuParser();
        Options options = new Options();
        Option assignment = Option.builder("a")
            .required(false)
            .hasArg()
            .longOpt("assignment")
            .desc("Select assignment to run")
            .build();
        Option help = Option.builder("h")
            .required(false)
            .longOpt("help")
            .desc("Print this message")
            .build();
        options.addOption(assignment);
        options.addOption(help);
        try {
            CommandLine line = parser.parse(options, args);
            if (line.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar ./target/assignments-1.0-SNAPSHOT.jar",
                                    "Introduction to Java (20478). Oleg Sivokon. 2015.",
                                    options,
                                    "Send your complaints to <olegsivokon@gmail.com>", true);
            } else {
                int subscript = Integer.parseInt(line.getOptionValue("assignment", "0"));
                logger.debug("Selected assignment: " + subscript);
                try {
                    Assignment selected = (Assignment)(assignments[subscript].newInstance());
                    selected.interact();
                } catch (InstantiationException | IllegalAccessException exp) {
                    logger.error("This assignment haven't been written yet: " + subscript);
                }
            }
        } catch (ParseException exp) {
            logger.error("Parsing failed.  Reason: " + exp.getMessage());
        }
    }
}
