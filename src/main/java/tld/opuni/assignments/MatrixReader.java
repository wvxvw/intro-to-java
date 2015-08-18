package tld.opuni.assignments;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Utility class for reading two-dimensional matrices from files.
 *
 * @author Oleg Sivokon
 */
public class MatrixReader {
        
    private final int[][] matrix;

    /**
     * Reads the given file and parses it into an integer matrix.
     *
     * @param file The path to the text file containing the matrix.
     */
    public MatrixReader(final String file) throws FileNotFoundException {
        final Scanner scanner = new Scanner(new File(file));
        final LinkedList<String> lines = new LinkedList<String>();
            
        while (scanner.hasNextLine()) lines.add(scanner.nextLine());

        matrix = lines.stream()
                 .map(s -> Arrays.stream(s.split("\\W"))
                      .mapToInt(Integer::parseInt).toArray())
                 .toArray(int[][]::new);
    }

    /**
     * The matrix we read from file (does not create a copy).
     */ 
    public int[][] getMatrix() { return matrix; }

    /**
     * The same matrix as <code>getMatrix()</code>, return transposed (copy).
     */
    public int[][] transposed() {
        final int[][] result = new int[matrix[0].length][matrix.length];
        for (int row = 0; row < matrix.length; row++)
            for (int col = 0; col < matrix[0].length; col++)
                result[col][row] = matrix[row][col];
        return result;
    }
}
