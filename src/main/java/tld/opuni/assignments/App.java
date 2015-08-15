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

class App {
    public static void main(String ...args) {
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
                System.out.format("Selected assignment: %s%n",
                                  line.getOptionValue("assignment", "none"));
            }
        }
        catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
    }
}
