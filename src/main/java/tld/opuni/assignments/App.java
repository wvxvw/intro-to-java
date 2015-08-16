package tld.opuni.assignments;

import java.util.Scanner;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;

/**
 * Entry point.
 */
public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);
    
    private static final App app = new App();

    private final HashMap<String, Class> assignments;
    
    private final Pattern re;
    
    private App() {
        re = Pattern.compile("(\\d+)\\W*(\\w+)");
        assignments = new HashMap<String, Class>();
        assignments.put("11a", Assignment11a.class);
        assignments.put("11b", Assignment11b.class);
    }
    
    public static void main(String ...args) {
        PropertyConfigurator.configure("./etc/log4j.properties");
        final CommandLineParser parser = new GnuParser();
        final Options options = new Options();
        final Option assignment = Option.builder("a")
            .required(false)
            .hasArg()
            .longOpt("assignment")
            .desc("Select assignment to run.  " +
                  "The assignment number is composed of a sequence of digits " +
                  "as they appear in the course silibus followed by a letter or " +
                  "a digit, with a possible space " +
                  "or other punctuation character in between.  " +
                  "For example `11a', `11.0', `1-A' are all valid and designate " +
                  "the same assignment.")
            .build();
        final Option help = Option.builder("h")
            .required(false)
            .longOpt("help")
            .desc("Print this message")
            .build();
        final Option verbosity = Option.builder("v")
            .required(false)
            .longOpt("verbosity")
            .hasArg()
            .desc("Controls how much information is printed.")
            .build();
        options.addOption(assignment);
        options.addOption(help);
        options.addOption(verbosity);
        try {
            final CommandLine line = parser.parse(options, args);
            if (line.hasOption("verbosity"))
                LogManager.getRootLogger().setLevel(Level.toLevel(
                    line.getOptionValue("verbosity", "error")));
            if (line.hasOption("help")) {
                final HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java -jar ./target/assignments-1.0-SNAPSHOT.jar",
                                    "Introduction to Java (20478). Oleg Sivokon. 2015.",
                                    options,
                                    "Send your complaints to <olegsivokon@gmail.com>", true);
            } else {
                app.loadAssignment(line.getOptionValue("assignment", "11"));
            }
        } catch (ParseException | InvalidAssignment exp) {
            logger.error("Parsing failed.  Reason: " + exp.getMessage());
        }
    }

    public void loadAssignment(final String which) throws InvalidAssignment {
        logger.debug("Selected assignment: " + which);
        try {
            final String normalized = normalize(which);
            if (assignments.containsKey(normalized)) {
                final Assignment selected =
                    (Assignment)(assignments.get(normalized).newInstance());
                selected.interact(this);
            } else throw new InvalidAssignment();
        } catch (InstantiationException |
                 IllegalAccessException |
                 ArrayIndexOutOfBoundsException exp) {
            logger.error("This assignment hasn't been written yet: " + which);
        }
    }

    public void quitOrReload() {
        System.out.println("The program is waiting for you instructions:");
        System.out.println("  Type `c' to continue.");
        System.out.println("  Type `q' to quit.");
        System.out.println("  Type the number of an assignment to switch to.");
        final Scanner scanner = new Scanner(System.in);
        final String in = scanner.next();
        if ("c".equals(in)) return;
        else if ("q".equals(in)) System.exit(-1);
        else {
            try { loadAssignment(in); }
            catch (InvalidAssignment exp) { quitOrReload(); }
        }
    }

    private class InvalidAssignment extends Exception { }
    
    private String normalize(final String denorm) throws InvalidAssignment {
        final Matcher mtc = re.matcher(denorm);
        logger.debug("normalizing: " + denorm + " matched: " + mtc.matches());
        if (mtc.matches()) {
            int num = Integer.parseInt(mtc.group(1));
            if (num < 11) num += 10;
            if (num == 10) num = 11;
            String section;
            try {
                char sub = (char)('a' + Integer.parseInt(mtc.group(2)));
                char[] chars = { sub };
                section = new String(chars);
            } catch (NumberFormatException exp) {
                section = mtc.group(2);
            }
            logger.debug("assignment normalized: " + num + section.toLowerCase());
            return num + section.toLowerCase();
        }
        throw new InvalidAssignment();
    }
}
