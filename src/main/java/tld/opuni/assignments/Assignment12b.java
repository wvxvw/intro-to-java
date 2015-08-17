package tld.opuni.assignments;

import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class prepends a digit to a given number.  The rules
 * for prepending are as follows: if both the first and the
 * last digits are the same, then 9 is prepended, otherwise
 * the digit prepended is the maximal of the two.
 *
 * @author Oleg Sivokon
 */
public class Assignment12b extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment12b.class);

    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.println("Welcome to digit appending program!");
        final Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter a number 3 to 5 digits long: ");
            try {
                System.out.println("Here is your number, converted: " +
                                 convert(digitsOf(scanner.nextInt())));
            } catch (InputMismatchException e) {
                logger.debug("failed to parse: " + scanner.next());
                if (!app.quitOrReload()) break;
            }
        }
    }

    private int[] digitsOf(int number) {
        LinkedList<Integer> result = new LinkedList<Integer>();
        if (number == 0) result.add(0);
        else {
            number = Math.abs(number);
            while (number > 0) {
                result.add(number % 10);
                number /= 10;
            }
        }
        Collections.reverse(result);
        logger.debug("digits: " + Arrays.toString(
            result.toArray(new Integer[result.size()])));
        return result.stream().mapToInt(i -> i).toArray();
    }

    private int toInt(final int[] digits) {
        return Arrays.stream(digits).reduce(0, (acc, n) -> acc * 10 + n);
    }
    
    private int convert(final int[] digits) {
        int added;
        final int left = digits[0];
        final int right = digits[digits.length - 1];
        if (left == right) added = 9;
        else added = Math.max(left, right);
        logger.debug("added: " + added);
        return added * (int)Math.pow(10, digits.length) + toInt(digits);
    }
}
