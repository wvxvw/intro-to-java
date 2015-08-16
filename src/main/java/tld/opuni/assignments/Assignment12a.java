package tld.opuni.assignments;

import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Date;
import java.lang.Iterable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Locale;
import java.util.InputMismatchException;

public class Assignment12a extends Assignment {

    private class Pair extends AbstractMap.SimpleEntry<Integer,Double> {
        
        Pair(int day, double hours) { super(day, hours); }
    }
    
    private class January implements Iterable<Pair> {
        
        private final LinkedList<Pair> workdays;

        private final double overtime;

        private final int workday;

        private final int year;

        public January() {
            this(Calendar.getInstance().get(Calendar.YEAR), 2.0d, 8);
        }
        
        public January(final int year) {
            this(year, 2.0d, 8);
        }

        public January(final int year, final double overtime) {
            this(year, overtime, 8);
        }

        public January(final int year, final double overtime, final int workday) {
            this.year = year;
            this.overtime = overtime;
            this.workday = workday;
            this.workdays = calculateWorkdays();
        }

        public Iterator<Pair> iterator() {
            return workdays.iterator();
        }

        public int getYear() { return year; }
        
        private LinkedList<Pair> calculateWorkdays() {
            final LinkedList<Pair> result = new LinkedList<Pair>();
            final Calendar cld = Calendar.getInstance();
            int day;
            cld.set(this.year, 0, 0);
            for (int i = 0; i < 31; i++) {
                day = cld.get(Calendar.DAY_OF_WEEK);
                if (day != Calendar.FRIDAY && day != Calendar.SATURDAY)
                    result.add(new Pair(i, 0d));
                cld.set(this.year, 0, i);
            }
            return result;
        }
    }
    
    @Override public void interact(final App app) {
        System.out.format("Welcome to nuts and bolts accounting program!%n" +
                          "This program will ask you to produce nuts and bolts%n" +
                          "every working day of Janury.%n" +
                          "No worries though!  You may opt-out when you feel you%n" +
                          "had made enough bolts! (To opt-out type `q')%n" +
                          "First, let's select the year:%n");
        
        final Scanner scanner = new Scanner(System.in);
        final Calendar cld = Calendar.getInstance();
        final January january = readJanuary(scanner);
        
        for (Pair day : january) {
            cld.set(january.getYear(), 0, day.getKey());
            String wday = cld.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
            int date = day.getKey();
            System.out.format("How much did you work on %s %d?%n", wday, date + 1);
            try {
                double hours = scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Was reading: " + scanner.next());
                break;
            }
        }
    }

    private January readJanuary(final Scanner scanner) {
        January january;
        try {
            int year = scanner.nextInt();
            try {
                System.out.println("How much do you want to be paid overtime?");
                double overtime = scanner.nextDouble();
                try {
                    System.out.println("How long is your working day?");
                    january = new January(year, overtime, scanner.nextInt());
                } catch (InputMismatchException e) {
                    january = new January(year, overtime);
                }
            } catch (InputMismatchException e) {
                january = new January(year);
            }
        } catch (InputMismatchException e) {
            january = new January();
        }
        return january;
    }
}
