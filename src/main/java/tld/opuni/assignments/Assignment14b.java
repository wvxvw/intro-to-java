package tld.opuni.assignments;

import java.util.Scanner;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

/**
 * This class looks for a ``hole'' (a row of zeros at the same offset
 * as a column of all ones, except for the common cell).
 *
 * @author Oleg Sivokon
 */
public class Assignment14b extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment14b.class);

    private int[] sortMod4(final int[] array) {
        final List<Integer> sorted = Ints.asList(array);
        Collections.sort(sorted, new Comparator<Integer>() {
                @Override public int compare(Integer a, Integer b) {
                    return new Integer(a % 4).compareTo(new Integer(b % 4));
                }
            });
        return Ints.toArray(sorted);
    }

    private int[] readArray(final Scanner scanner) {
        return Arrays.stream(scanner.nextLine().split("\\D"))
                .mapToInt(Integer::parseInt).toArray();
    }
    
    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.format("Welcome to mod 4 sorting program%n" +
                          "Type integers separated by spaces you wish to sort.%n");
        final Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Array sorted: " +
                               Arrays.toString(sortMod4(readArray(scanner))));
        } catch (NumberFormatException exp) {
            System.out.println(
                "Your input contains something other than numbers!");
        } finally {
            if (app.quitOrReload()) interact(app);
        }
    }
}
