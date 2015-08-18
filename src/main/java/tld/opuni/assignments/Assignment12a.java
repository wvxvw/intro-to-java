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

/**
 * Blue collar job simulator.  You work overtime making nuts and
 * bolts.  Earn money as you clock more hours.  <i>Note that your days
 * may have as many as (2-2<sup>-52</sup>)·2<sup>1023</sup> hours,
 * <b>what a racket!</b></i>
 *
 * @author Oleg Sivokon
 */
public class Assignment12a extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment12a.class);

    protected class Pair extends AbstractMap.SimpleEntry<Integer,Double> {

        /**
         * Because Java has no deftype.
         *
         * @param day The day of the month (which is also the key).
         *
         * @param hours The number of hours you have worked that day.
         */
        Pair(int day, double hours) { super(day, hours); }
    }
    
    protected class January implements Iterable<Pair> {
        
        private final LinkedList<Pair> workdays;

        private final double overtime;

        private final int workday;

        private final int year;

        private final int hourlyRate = 35;

        /**
         * Default constructor, creates new <code>January</code> of the
         * current year Anno Domini!
         */
        public January() {
            this(Calendar.getInstance().get(Calendar.YEAR), 2.0d, 8);
        }

        /**
         * Creates new <code>January</code> of the year of your choosing.
         *
         * @param year The year of yhour choosing.
         */
        public January(final int year) { this(year, 2.0d, 8); }

        /**
         * Creates a special <code>January</code>: you choose the year and
         * the ratio of hours you are paid when you stay overtime.
         *
         * @param year Four digits year in Gregorian calendar.
         *
         * @param overtime The ratio of hours counted per hour you stay overtime.
         */
        public January(final int year, final double overtime) {
            this(year, overtime, 8);
        }

        /**
         * Creates a very special <code>January</code>: you control the year,
         * the ratio of overtime hours and the length of the working day (i.e.
         * when you start working overtime).
         *
         * @param year Year of our Lord the Savior, heathens!
         *
         * @param overtime How many hours you clock when you work overtime each hour.
         *
         * @param workday Unions mandated working day.
         */
        public January(final int year, final double overtime, final int workday) {
            this.year = year;
            this.overtime = overtime;
            this.workday = workday;
            this.workdays = calculateWorkdays();
        }

        /**
         * Implementation of <code>Iterable</code>.
         */
        public Iterator<Pair> iterator() { return workdays.iterator(); }

        /**
         * The year this January is of.
         */
        public int getYear() { return year; }

        /**
         * Union mandated working day (in hours).
         */
        public int getWorkday() { return workday; }

        /**
         * The ratio of hours you clock when you work overtime to your
         * regular Union-mandated hours.
         */
        public double getOvertime() { return overtime; }

        /**
         * The day the Earth stood still, but you busted your nut all the same!
         */
        public Pair max() {
            Pair result = new Pair(-1, Double.MIN_VALUE);
            for (Pair day : this)
                if (result.getValue() < day.getValue()) result = day;
            return result;
        }

        /**
         * Of them Union-mandated working day, you only worked this many.
         */
        public int daysWorked() {
            int result = 0;
            for (Pair day : this) if (day.getValue() > 0) result += 1;
            return result;
        }

        /**
         * Slacker or worker bee--one way to find out!
         */
        public double hoursWorked() {
            double result = 0;
            for (Pair day : this) result += hoursPerDay(day);
            return result;
        }

        /**
         * This is how much you put on your plate, daily.
         */
        public double computePay(final Pair day) {
            return hoursPerDay(day) * hourlyRate;
        }

        /**
         * End of the month, here you come!
         */
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

    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
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
        System.out.print(formatReport(january, cld));
        if (app.quitOrReload()) interact(app);
    }

    protected String formatReport(final January january, final Calendar cld) {
        final Pair max = january.max();
        cld.set(january.getYear(), 0, max.getKey());
        return String
                .format("The longest day of your career so far was: %s %s%n" +
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
