package tld.opuni.assignments;

import java.io.PrintWriter;
import java.util.Scanner;

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

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static Class[] assignments = {
        Assignment11.class
    };

    private static App app;
    
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
                app = new App();
                app.loadAssignment(line.getOptionValue("assignment", "11"));
            }
        } catch (ParseException exp) {
            logger.error("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    public void loadAssignment(String which) {
        int subscript = Integer.parseInt(which) - 11;
        logger.debug("Selected assignment: " + subscript);
        try {
            Assignment selected = (Assignment)(assignments[subscript].newInstance());
            selected.interact(this);
        } catch (InstantiationException |
                 IllegalAccessException |
                 ArrayIndexOutOfBoundsException exp) {
            logger.error("This assignment hasn't been written yet: " +
                         (subscript + 11));
        }
    }

    public void quitOrReload() {
        System.out.println("The program is waiting for you instructions:");
        System.out.println("  Type `c' to continue.");
        System.out.println("  Type `q' to quit.");
        System.out.println("  Type the number of an assignment to switch to.");
        Scanner scanner = new Scanner(System.in);
        String in = scanner.next();
        if ("c".equals(in)) { return; }
        else if ("q".equals(in)) { System.exit(-1); }
        else {
            try { loadAssignment(in); }
            catch (NumberFormatException exp) { quitOrReload(); }
        }
    }
}
