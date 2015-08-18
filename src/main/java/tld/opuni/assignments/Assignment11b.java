package tld.opuni.assignments;

import java.util.Scanner;
import java.util.InputMismatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This assignment calculates the profits and the time you
 * spend when folding paper cups.
 *
 * @author Oleg Sivokon
 */
public class Assignment11b extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment11b.class);
    
    /**
     * This class folds cups.
     */
    protected class CupsFolder {

        /**
         * Produces a message describing the earnings for the number
         * of cups folded.
         *
         * @param cups The number of cups folded.
         */
        public String fold(final int cups) {
            double shekels = cups * 2.5;
            int time = cups * 5;
            int hours = time / 60;
            int minutes = time % 60;
            String hMessage;
            String mMessage;
            String connector;
            if (hours > 0) {
                if (hours % 10 == 1) hMessage = "%d hour";
                else hMessage = "%d hours";
                hMessage = String.format(hMessage, hours);
            } else hMessage = "";
            if (minutes > 0) {
                if (minutes % 10 == 1) mMessage = "%d minute";
                else mMessage = "%d minutes";
                mMessage = String.format(mMessage, minutes);
            } else mMessage = "";
            if (mMessage.length() > 0 && hMessage.length() > 0)
                connector = " and ";
            else connector = "";
            return String.format("You earned %.2f shekels in just %s%s%s!%n",
                                 shekels, hMessage, connector, mMessage);
        }
    }
    
    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.println("Welcome to paper cup folding program!");
        final Scanner scanner = new Scanner(System.in);
        final CupsFolder folder = new CupsFolder();
        
        while (true) {
            System.out.println("How many cups do you want to fold?");
            try {
                System.out.print(folder.fold(scanner.nextInt()));
            } catch (InputMismatchException xpt) {
                scanner.next();
                app.quitOrReload();
                continue;
            }
        }
    }
}
