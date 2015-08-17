package tld.opuni.assignments;

import java.util.stream.Stream;
import java.util.Comparator;
import java.util.Arrays;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class creates a library program.  You can borrow books,
 * but never return them.  This library is very particular about
 * the number of pages each book has.
 *
 * <h1>Modifications to Silibus Requirements</h1>
 * <i>I deliberately altered some names and requirements found in the silibus.
 * Below is the justification for my changes.</i>
 * 
 * <ul>
 *   <li><code>bookName</code> is renamed into <code>title</code>.
 *   Books don't usually have names.  Books may, however, earn a name,
 *   as they would earn fame.  For example, the book Compilers: Principles,
 *   Techniques, and Tools by Alfred Aho, Jeffrey Ullman, Monica S. Lam,
 *   and Ravi Sethi has eraned the name "Dragon Book".  However, I believe
 *   the silibus' author was aiming for a more prosaic nomenclature.</li>
 *   
 *   <li><code>bookAuthor</code> was renamed to <code>author</code>.
 *   This is because using the name of the class in the name of its
 *   method or property is a known anti-pattern of naming.  Same rationale
 *   applies to several other names.</li>
 *   
 *   <li>I removed the filed <code>available</code>.  This is so because
 *   having an instance field which serves only to save the state of
 *   another instance field is the pinacle of bad design.</li>
 *   
 *   <li><code>friendName</code> is renamed to <code>patron</code>.
 *   Books don't have friends.  The person who borrows a book from a library
 *   is a patron.  Eventually, it may be your or someone else's friend,
 *   but it is not what the author of the silibus most likely had in mind.</li>
 *   
 *   <li>I removed all underscores in field names.  Such naming goes against
 *   the established practices in the field.</li>
 *   
 *   <li>Finally, it is not clear what the author of the silibus had in
 *   mind when she wrote tha the number of books in the library should be
 *   constant.  Some programming languages have a concept of constants,
 *   either variables or class fields etc.  Java has literal constants, i.e.
 *   the literal values which appear in code, but this cannot be the intended
 *   meaning.  I put my best effort into ensuring that the value is difficult
 *   to modify, which seems to me the intended meaning of the paragraph.</li>
 * </ul>
 *
 * @author Oleg Sivokon
 */
public class Assignment13a extends Assignment {
    
    private static final Logger logger =
            LoggerFactory.getLogger(Assignment13a.class);

    private class Book {
        
        private String title;
        
        private String author;
        
        private int numPages;
        
        private String patron;

        public String getTitle() { return title; }

        public String getAuthor() { return author; }

        public int getNumPages() { return numPages; }

        public boolean isAvailable() { return patron != null; }

        public Book(final String author, final String title, final int numPages) {
            this.author = author;
            this.title = title;
            this.numPages = numPages;
        }

        public Book(final Book copy) {
            this(copy.getAuthor(), copy.getTitle(), copy.getNumPages());
        }

        public boolean borrow(String patron) {
            boolean result = isAvailable();
            if (result) this.patron = patron;
            return result;
        }

        public String toString() { return new Gson().toJson(this); }
    }

    private class BookComparator implements Comparator<Book> {

        @Override public int compare(final Book x, final Book y) {
            if (!y.isAvailable() && x.isAvailable()) return 0;
            if (!x.isAvailable()) return 1;
            if (!y.isAvailable()) return -1;
            return Integer.compare(x.getNumPages(), y.getNumPages());
        }
    }
    
    private class Library {

        private final Book[] store;

        public Library() { this(150); }
        
        public Library(final int capacity) { this.store = new Book[capacity]; }

        public long tally() { return present().count(); }

        public long borrowed() {
            return present().filter(b -> b.isAvailable()).count();
        }

        public boolean addBook(
            final String author, final String title, final int numPages) {
            int i;
            for (i = 0; i < store.length; i++) {
                if (store[i] == null) {
                    store[i] = new Book(author, title, numPages);
                    break;
                }
            }
            return i < store.length;
        }

        public Book longestBookAvailable() {
            return present().max(new BookComparator()).get();
        }

        private Stream<Book> present() {
            return Arrays.stream(store).filter(b -> b != null);
        }
    }
    
    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        System.out.println("Welcome to library!");
        Book sicp = new Book("Structure and Interpretation of Computer Programs",
                             "Gerald Jay Sussman and Hal Abelson", 855);
        System.out.println("Example book: " + sicp);
    }
}
