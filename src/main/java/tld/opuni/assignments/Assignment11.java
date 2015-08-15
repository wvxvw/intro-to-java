package tld.opuni.assignments;

import java.util.Locale;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.ArrayUtils;

class Assignment11 extends Assignment {

    private static final Logger logger = LoggerFactory.getLogger(Assignment11.class);
    
    private abstract class Unit {

        public double value;

        public void init(double unit) { value = unit; }
        
        public void init(Unit unit) {
            value = (unit.value * unit.getCoefficient()) / getCoefficient();
        }

        protected abstract String getUnits();

        protected abstract double getCoefficient();
        
        protected double getThreshold() { return 100; }
        
        protected double round(double value) {
            return Math.round(value * getThreshold()) / (getThreshold() * 10);
        }

        public String toString() {
            return String.format("%.2f%s", value, getUnits());
        }
    }

    private class Centimeter extends Unit {

        @Override protected double getCoefficient() { return 1d; }
        
        @Override protected String getUnits() { return "cm"; }
    }

    private class Meter extends Unit {

        @Override protected double getCoefficient() { return 100d; }
        
        @Override protected String getUnits() { return "m"; }
    }

    private class Inch extends Unit {

        @Override protected double getCoefficient() { return 2.54d; }
        
        @Override protected String getUnits() { return "\""; }
    }

    private class Foot extends Unit {

        @Override protected double getCoefficient() { return 12d * 2.54d; }
        
        @Override protected String getUnits() { return "'"; }
    }

    private abstract class State {

        public States states;
        
        public abstract void next();
    }

    private abstract class ReadState extends State {
        
        protected String prompt;
        protected Pattern re;
        protected Matcher mtc;
        
        @Override public void next() {
            System.out.println(prompt);

            String scanned = states.scanner.next();
            logger.debug("Reading: " + scanned + " re: " + re + " this: " + this);
            mtc = re.matcher(scanned);
            states.backtrack = this;
            if (mtc.matches()) tryNext();
            else states.current = states.error;
        }

        protected void readUnit(String str, State change) {
            try {
                Class cls = states.handlers.get(str);
                Constructor ctr = cls.getDeclaredConstructors()[0];
                ctr.setAccessible(true);
                logger.debug("cls: " + ctr);
                states.unit = (Unit)(ctr.newInstance(Assignment11.this));
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

        public abstract void tryNext();
    }

    private class InputState extends ReadState {

        public InputState(States states) {
            this.states = states;
            this.prompt = "Please enter a number followed by the unit (m, cm, ', \"):";
            this.re = Pattern.compile(
                "(-?(?:\\d*\\.\\d+|\\d+)(?:[eE][+-]?\\d+)?)\\s*(m|cm|'|\")");
        }
        
        @Override public void tryNext() {
            readUnit(mtc.group(2), states.conversion);
            states.unit.init(Double.parseDouble(mtc.group(1)));
        }
    }

    private class ConvertState extends ReadState {

        public ConvertState(States states) {
            this.states = states;
            this.prompt = "Select unit to convert to (m, cm, ', \"):";
            this.re = Pattern.compile("m|cm|'|\"");
        }
        
        @Override public void tryNext() {
            Unit before = states.unit;
            readUnit(mtc.group(), states.input);
            states.unit.init(before);
            System.out.println("After conversion: " + states.unit);
        }
    }

    private class ErrorState extends State {

        private App app;
        
        public ErrorState(States states, final App app) {
            this.states = states;
            this.app = app;
        }
        
        @Override public void next() {
            System.out.println("The value you provided cannot be understood.");
            app.quitOrReload();
            states.current = states.backtrack;
        }
    }
    
    private class States {
        
        public Unit unit;
        
        public HashMap<String, Class> handlers;

        public State backtrack;

        public State current;

        public State input;

        public State conversion;

        public State error;

        public Scanner scanner;
        
        public States() {
            handlers = new HashMap<String, Class>();
            handlers.put("m", Meter.class);
            handlers.put("cm", Centimeter.class);
            handlers.put("'", Foot.class);
            handlers.put("\"", Inch.class);
            scanner = new Scanner(System.in);
        }
    }
    
    @Override public void interact(App app) {
        logger.debug("interacting");
        States states = new States();
        System.out.println("Welcome to unit conversion program!");
        states.input = new InputState(states);
        states.conversion = new ConvertState(states);
        states.error = new ErrorState(states, app);
        states.current = states.input;
        
        while (true) states.current.next();
    }
}
