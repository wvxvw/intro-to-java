package tld.opuni.assignments;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class looks for a ``hole'' (a row of zeros at the same offset
 * as a column of all ones, except for the common cell).
 *
 * @author Oleg Sivokon
 */
public class Assignment14a extends Assignment {
        
    private static final Logger logger =
            LoggerFactory.getLogger(Assignment14a.class);

    private int[][] transpose(final int[][] matrix) {
        final int[][] result = new int[matrix.length][matrix.length];
        for (int row = 0; row < matrix.length; row++)
            for (int col = 0; col < matrix.length; col++)
                result[col][row] = matrix[row][col];
        return result;
    }
    
    private int sumCol(final int[][] matrix, final int col) {
        return sumRow(transpose(matrix), col);
    }
    
    private int sumRow(final int[][] matrix, final int row) {
        return Arrays.stream(matrix[row]).reduce(0, (a, b) -> a + b);
    }
    
    protected int holeIndex(final int[][] matrix) {
        int result = -1;
        assert matrix.length == matrix[0].length;

        for (int row = 0; row < matrix.length; row++) {
            if (matrix[row][row] == 0 &&
                sumRow(matrix, row) == 0 &&
                sumCol(matrix, row) == matrix.length - 1) {
                result = row;
                break;
            }
        }
        return result;
    }
    
    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.format("Let's search for zeros and ones!%n" +
                          "Type the path to the file containing your matrix.%n" +
                          "(Matrix can only contain zeros and ones)%n");
        final Scanner scanner = new Scanner(System.in);
        final String file = scanner.nextLine();

        try {
            final int hole = holeIndex(new MatrixReader(file).getMatrix());
            System.out.println(
                hole > -1 ?
                "This matrix has a ``hole'' at ." + hole :
                "This matrix doesn't have a ``hole''.");
        } catch (FileNotFoundException exp) {
            System.out.format("Apparently, such file doesn't exist%n" +
                              "or its format doesn't match.%n");
        } finally {
            if (app.quitOrReload()) interact(app);
        }
    }
}
