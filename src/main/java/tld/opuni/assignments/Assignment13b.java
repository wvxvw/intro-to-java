package tld.opuni.assignments;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tries to establish that the upper-right triangle of
 * a given square matrix contains only odd factors, while its lower-left
 * triangle contains only even factors.
 *
 * @author Oleg Sivokon
 */
public class Assignment13b extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment13b.class);

    /**
     * There is no reason for this function to be either public or static,
     * but the silibus requirement was very specific about it.
     * This function will look into the upper-right triangle of the matrix for
     * even factors and lower-left triangle for the odd factors.  If none are
     * found, it will return <code>true</code>.
     *
     * @param matrix An integer matrix.
     */
    public static boolean isUpperOddsLowerEvens(final int[][] matrix) {
        assert matrix.length == matrix[0].length;
        
        LinkedList<Integer> topRight = new LinkedList<Integer>();
        LinkedList<Integer> bottomLeft = new LinkedList<Integer>();
        
        for (int row = 0; row < matrix.length; row++) {
            for (int col = row + 1; col < matrix.length; col++) {
                topRight.add(matrix[row][col]);
                bottomLeft.add(matrix[col][row]);
            }
        }
        return topRight.stream().anyMatch(n -> (n & 1) == 0) &&
                bottomLeft.stream().allMatch(n -> (n & 1) == 1);
    }
    
    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.format("Welcome to odds counting program!%n" +
                          "Type the path to the file containing your matrix.%n" +
                          "(Matrix can only contain decimal integers)%n");
        final Scanner scanner = new Scanner(System.in);
        final String file = scanner.nextLine();
        try {
            System.out.println(
                isUpperOddsLowerEvens(new MatrixReader(file).getMatrix()) ?
                "This matrix satisfies the requirements." :
                "This matrix does not satisfy the requirements.");
        } catch (FileNotFoundException exp) {
            System.out.format("Apparently, such file doesn't exist%n" +
                              "or its format doesn't match.");
        } finally {
            if (app.quitOrReload()) interact(app);
        }
    }
}
