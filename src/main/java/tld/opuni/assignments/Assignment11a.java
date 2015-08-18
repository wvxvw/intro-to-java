package tld.opuni.assignments;

import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This assignment offers an elaborate, but very restricted way to
 * convert between metric and imperial unit system.
 */
public class Assignment11a extends Assignment {

    private static final Logger logger =
            LoggerFactory.getLogger(Assignment11a.class);
    
    protected abstract class Unit {

        /**
         * The internal value of the unit.  It always has the same units as
         * the container class would imply.
         */
        public double value;

        /**
         * Sets the raw value.
         *
         * @param unit The raw value.
         */
        public void init(final double unit) { value = unit; }

        /**
         * Sets the value of this unit by converting it from given unit.
         * Values are first converted to centimeters, and then to the units
         * of this unit.
         *
         * @param unit The unit to convert from.
         */
        public void init(final Unit unit) {
            value = (unit.value * unit.getCoefficient()) / getCoefficient();
        }

        /**
         * The printable representation of units.
         */
        protected abstract String getUnits();

        /**
         * The scale ration to centimeter.
         */
        protected abstract double getCoefficient();

        /**
         * The rounding threshold (required by the course syllabus).
         */
        protected double getThreshold() { return 100d; }

        /**
         * The rounding procedure (defined in the course syllabus).
         *
         * @param value The value with maximum precision.
         */
        protected double round(final double value) {
            return Math.round(value * getThreshold()) / (getThreshold() * 10d);
        }

        /**
         * Pretty prints this unit.
         */
        public String toString() {
            return String.format("%.2f%s", value, getUnits());
        }
    }

    protected class Centimeter extends Unit {

        @Override protected double getCoefficient() { return 1d; }
        
        @Override protected String getUnits() { return "cm"; }
    }

    protected class Meter extends Unit {

        @Override protected double getCoefficient() { return 100d; }
        
        @Override protected String getUnits() { return "m"; }
    }

    protected class Inch extends Unit {

        @Override protected double getCoefficient() { return 2.54d; }
        
        @Override protected String getUnits() { return "\""; }
    }

    protected class Foot extends Unit {

        @Override protected double getCoefficient() { return 12d * 2.54d; }
        
        @Override protected String getUnits() { return "'"; }
    }

    private abstract class State {

        /**
         * A reference to the container, where this state resides.
         */
        public final States states;

        /**
         * This class is abstract.  You should not try to instantiate it.
         */
        public State(final States states) {
            this.states = states;
        }

        /**
         * This method is the means of passing control to the next proper state.
         * States pick the next valid state based on user interaction.  This
         * method should be called from the code outside the state in order to
         * continue interacting with the user.
         */
        public abstract void next();
    }

    private abstract class ReadState extends State {

        /**
         * The question asked before data are collected from user.
         */
        protected String prompt;

        /**
         * The regular expression used to parse user provided data.
         */
        protected Pattern re;

        /**
         * A reference to the matcher which matched the most recent
         * input against <code>re</code>.
         */
        protected Matcher mtc;

        /**
         * This class is abstract.  You should not attempt instantiating it.
         */
        public ReadState(final States states,
                         final String prompt,
                         final String pattern) {
            super(states);
            this.prompt = prompt;
            this.re = Pattern.compile(pattern);
        }

        /**
         * @see tld.opuni.assignments.Assignment11a$State#next() next
         */
        @Override public void next() {
            System.out.println(prompt);

            String scanned = states.scanner.next();
            logger.debug("Reading: " + scanned + " re: " + re + " this: " + this);
            mtc = re.matcher(scanned);
            states.backtrack = this;
            if (mtc.matches()) tryNext();
            else states.current = states.error;
        }

        /**
         * Reads units from user input and transfers control to
         * <code>change</code> state.
         *
         * @param str The token red most recently.
         * 
         * @param change The state to switch to, if parsing was successful.
         */
        protected void readUnit(final String str, final State change) {
            try {
                final Class cls = states.handlers.get(str);
                final Constructor ctr = cls.getDeclaredConstructors()[0];
                ctr.setAccessible(true);
                logger.debug("cls: " + ctr);
                states.unit = (Unit)(ctr.newInstance(Assignment11a.this));
                states.current = change;
                logger.debug("state switched: " + states.current);
            } catch (InstantiationException    |
                     InvocationTargetException |
                     IllegalArgumentException  |
                     IllegalAccessException exp) {
                logger.debug("Instantiation failed: " + str +
                             " (This shouldn't happen.) " +
                             states.handlers.get(str).getDeclaredConstructors() +
                             " error: " + exp);
            }
        }

        /**
         * This method is called after this state had successfully processed the
         * input.
         */
        public abstract void tryNext();
    }

    private class InputState extends ReadState {

        /**
         * Creates new <code>ConvertState</code>.
         *
         * @param states States is used internally to transfer initiative to
         *               other states as well as store shared data.
         */
        public InputState(final States states) {
            super(states,
                  "Please enter a number followed by the unit (m, cm, ', \"):",
                  "(-?(?:\\d*\\.\\d+|\\d+)(?:[eE][+-]?\\d+)?)\\s*(m|cm|'|\")");
        }

        /**
         * @see tld.opuni.assignments.Assignment11a$ReadState#tryNext() tryNext
         */
        @Override public void tryNext() {
            readUnit(mtc.group(2), states.conversion);
            states.unit.init(Double.parseDouble(mtc.group(1)));
        }
    }

    private class ConvertState extends ReadState {

        /**
         * Creates new <code>ConvertState</code>.
         *
         * @param states States is used internally to transfer initiative to
         *               other states as well as store shared data.
         */
        public ConvertState(final States states) {
            super(states, "Select unit to convert to (m, cm, ', \"):",
                  "m|cm|'|\"");
        }
        
        /**
         * @see tld.opuni.assignments.Assignment11a$ReadState#tryNext() tryNext
         */
        @Override public void tryNext() {
            final Unit before = states.unit;
            readUnit(mtc.group(), states.input);
            states.unit.init(before);
            System.out.println("After conversion: " + states.unit);
        }
    }

    private class ErrorState extends State {

        private final App app;

        /**
         * Creates new <code>ErrorState</code>.
         *
         * @param states States is used internally to transfer initiative to
         *               other states as well as store shared data.
         * @param app The reference to the application which loaded the assignment.
         *            It is used internally to transfer control to the application
         *            when the user inputs invalid data.
         */
        public ErrorState(final States states, final App app) {
            super(states);
            this.app = app;
        }

        /**
         * @see tld.opuni.assignments.Assignment11a$State#next() next
         */
        @Override public void next() {
            System.out.println("The value you provided cannot be understood.");
            app.quitOrReload();
            states.current = states.backtrack;
        }
    }
    
    private class States {

        /**
         * The unit currently being read.
         */
        public Unit unit;

        /**
         * The concrete states handling the input at any given moment.
         */
        public final HashMap<String, Class> handlers;

        /**
         * The last non-error state to back-track to.
         */
        public State backtrack;

        /**
         * The currently active state.
         */
        public State current;

        /**
         * The <code>InputState</code> instance.
         */
        public State input;

        /**
         * The <code>ConvertState</code> instance.
         */
        public State conversion;

        /**
         * The <code>ErrorState</code> instance.
         */
        public State error;

        /**
         * The scanner reading user's input (shared by all states).
         */
        public final Scanner scanner;

        /**
         * The default and only constructor.
         */
        public States() {
            handlers = new HashMap<String, Class>();
            handlers.put("m", Meter.class);
            handlers.put("cm", Centimeter.class);
            handlers.put("'", Foot.class);
            handlers.put("\"", Inch.class);
            scanner = new Scanner(System.in);
        }
    }

    /**
     * @see tld.opuni.assignments.Assignment#interact(App) interact
     */
    @Override public void interact(final App app) {
        logger.debug("interacting");
        final States states = new States();
        System.out.println("Welcome to unit conversion program!");
        states.input = new InputState(states);
        states.conversion = new ConvertState(states);
        states.error = new ErrorState(states, app);
        states.current = states.input;
        
        while (true) states.current.next();
    }
}
