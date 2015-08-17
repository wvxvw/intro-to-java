package tld.opuni.assignments;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class verifies that the given matrix has only even numbers
 * in the even rows and only perfect multiples of three in the
 * odd rows.
 *
 * @author Oleg Sivokon
 */
public class Assignment15a extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment15a.class);

    private static boolean allEven(final int[] row) {
        return Arrays.stream(row).allMatch(n -> (n & 1) == 0);
    }

    private static boolean allMod3(final int[] row) {
        return Arrays.stream(row).allMatch(n -> (n % 3) == 0);
    }

    private static int[][] butFirst(final int[][] matrix) {
        return Arrays.copyOfRange(matrix, 1, matrix.length);
    }
    
    /**
     * This function owes its bizarre name and access modifiers to the
     * explicit directions found in the assignment.  This function has
     * no reason to be neither public nor static.
     *
     * This function will return <code>true</code> iff the matrix
     * passed to it has only even numbers in even rows and perfect
     * multiples of three in odd rows.
     */
    public static boolean isEvenRowsEvenOddRowsMod3(final int[][] matrix) {
        if (matrix.length > 0) {
            if (((matrix.length & 1) == 0) ? 
                allEven(matrix[0]) : allMod3(matrix[0]))
                return isEvenRowsEvenOddRowsMod3(butFirst(matrix));
            else return false;
        }
        return true;
    }
    
    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.format("Welcome to mod 2 and 3 discovery program%n" +
                          "Type the path to the file containing your matrix.%n" +
                          "(Matrix can only contain zeros and ones)%n");
        final Scanner scanner = new Scanner(System.in);
        final String file = scanner.nextLine();
        
        try {
            System.out.println(
                isEvenRowsEvenOddRowsMod3(new MatrixReader(file).getMatrix()) ?
                "This matrix fits the description." :
                "This matrix doesn't fit the description.");
        } catch (FileNotFoundException exp) {
            System.out.println("Couldnot load matrix file: " + file);
        } finally {
            if (app.quitOrReload()) interact(app);
        }
    }
}
