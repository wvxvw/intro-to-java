package tld.opuni.assignments;

import java.util.stream.Stream;
import java.util.Comparator;
import java.util.Arrays;

import com.google.gson.Gson;

public class Assignment13a extends Assignment {
    
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
