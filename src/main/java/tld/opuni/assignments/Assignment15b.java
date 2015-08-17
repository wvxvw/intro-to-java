package tld.opuni.assignments;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class performs some very basic and inefficient matching
 * on phrases from regular languages.
 *
 * @author Oleg Sivokon
 */
public class Assignment15b extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment15b.class);

    /**
     * This function's bizarre access modifiers are due to explicit requirement
     * found in the course silibus.  This function needs not be neither static
     * nor pblic.
     *
     * This function will return <code>true</code> iff all characters sans
     * the question mark in the <code>pattern</code> match respective characters
     * in the <code>phrase</code>.
     */
    public static boolean isMatch(final String pattern, final String phrase) {
        if (pattern.length() != phrase.length() ||
            (pattern.charAt(0) != '?' && pattern.charAt(0) != phrase.charAt(0)))
            return false;
        return isMatch(pattern.substring(1), phrase.substring(1));
    }
    
    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.format("Welcome to simple matcher program%n" +
                          "Please enter the pattern to match against%n" +
                          "Use question marks to match any character:%n");
        final Scanner scanner = new Scanner(System.in);
        final String pattern = scanner.nextLine();
        System.out.format("Please enter the phrase to match%n");
        final String phrase = scanner.nextLine();

        System.out.println(
            isMatch(pattern, phrase) ?
            "Phrase matches." : "Phrase does not match.");
        if (app.quitOrReload()) interact(app);
    }
}
