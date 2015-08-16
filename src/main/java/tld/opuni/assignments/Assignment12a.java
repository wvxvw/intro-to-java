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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Assignment12a extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment12a.class);

    private class Pair extends AbstractMap.SimpleEntry<Integer,Double> {
        
        Pair(int day, double hours) { super(day, hours); }
    }
    
    private class January implements Iterable<Pair> {
        
        private final LinkedList<Pair> workdays;

        private final double overtime;

        private final int workday;

        private final int year;

        private final int hourlyRate = 35;

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

        public int getWorkday() { return workday; }

        public double getOvertime() { return overtime; }

        public Pair max() {
            Pair result = new Pair(-1, Double.MIN_VALUE);
            for (Pair day : this)
                if (result.getValue() < day.getValue()) result = day;
            return result;
        }

        public int daysWorked() {
            int result = 0;
            for (Pair day : this) if (day.getValue() > 0) result += 1;
            return result;
        }

        public double hoursWorked() {
            double result = 0;
            for (Pair day : this) result += hoursPerDay(day);
            return result;
        }

        public double computePay(final Pair day) {
            return hoursPerDay(day) * hourlyRate;
        }

        public double paycheck() {
            double result = 0d;
            for (Pair day : this)
                if (day.getValue() > 0) result += computePay(day);
            return result;
        }

        private double hoursPerDay(final Pair day) {
            final double hours = day.getValue();
            return Math.min(hours, workday) +
                    Math.max(hours - workday, 0) * overtime;
        }
        
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
            String wday = cld.getDisplayName(
                Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
            int date = day.getKey();
            System.out.format("How much did you work on %s %d?%n", wday, date + 1);
            try {
                day.setValue(scanner.nextDouble());
            } catch (InputMismatchException e) {
                logger.debug("Was reading: " + scanner.next());
                break;
            }
        }
        final Pair max = january.max();
        cld.set(january.getYear(), 0, max.getKey());
        System.out.format("The longest day of your career so far was: %s %s%n" +
                          "On that day you earned %.2f shekels.%n" +
                          "You have worked for %d days, during which you clocked %.2f hours%n" +
                          "which earned you %.2f shekels.%n",
                          cld.getDisplayName(Calendar.DAY_OF_WEEK,
                                             Calendar.SHORT, Locale.US),
                          max.getKey() + 1, january.computePay(max),
                          january.daysWorked(), january.hoursWorked(), january.paycheck());
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
                    scanner.next();
                    january = new January(year, overtime);
                }
            } catch (InputMismatchException e) {
                scanner.next();
                january = new January(year);
            }
        } catch (InputMismatchException e) {
            scanner.next();
            january = new January();
        }
        return january;
    }
}
