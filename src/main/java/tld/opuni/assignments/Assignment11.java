package tld.opuni.assignments;

import java.util.Locale;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Assignment11 extends Assignment {

    static final Logger logger = LoggerFactory.getLogger(Assignment11.class);
    
    private abstract class Unit {

        public double value;

        protected abstract String getUnits();
        
        protected double getThreshold() { return 100; }
        
        protected double round(double value) {
            return Math.round(value * getThreshold()) / (getThreshold() * 10);
        }

        public String toString() {
            return String.format("%.2f%s", value, getUnits());
        }
    }

    private class Centimeter extends Unit {

        public Centimeter() { }

        public Centimeter(double value) { this.value = value; }
        
        public Centimeter(Centimeter unit) { value = unit.value; }

        public Centimeter(Meter unit) { value = unit.value * 100d; }

        public Centimeter(Inch unit) { value = unit.value * 2.54d; }

        public Centimeter(Foot unit) { value = new Inch(unit).value * 12d; }

        public Centimeter(Unit unit) {
            throw new RuntimeException("Unkown unit: " + unit);
        }

        @Override protected String getUnits() { return "cm"; }
    }

    private class Meter extends Unit {

        public Meter() { }

        public Meter(double value) { this.value = value; }
        
        public Meter(Centimeter unit) { value = unit.value / 100d; }

        public Meter(Unit unit) { this(new Centimeter(unit)); }

        @Override protected String getUnits() { return "m"; }
    }

    private class Inch extends Unit {

        public Inch() { }

        public Inch(double value) { this.value = value; }
        
        public Inch(Centimeter unit) { value = unit.value / 2.54d; }

        public Inch(Unit unit) { this(new Centimeter(unit)); }

        @Override protected String getUnits() { return "\""; }
    }

    private class Foot extends Unit {

        public Foot() { }

        public Foot(double value) { this.value = value; }
        
        public Foot(Centimeter unit) { value = new Inch(unit).value / 12d; }

        public Foot(Unit unit) { this(new Centimeter(unit)); }

        @Override protected String getUnits() { return "'"; }
    }

    private abstract class State {

        public States states;
        
        public abstract void next();
    }

    private class InputState extends State {

        private Pattern re =
            Pattern.compile("(-?(?:\\d*\\.\\d+|\\d+)(:?[eE][+-]?\\d+))\\s+(m|cm|'|\")");
        
        public InputState(States states) { this.states = states; }
        
        @Override public void next() {
            Matcher mtc = re.matcher(states.scanner.next());
        
            System.out.println("Please enter a number followed by the unit (m, cm, ', \"):");
            states.backtrack = this;
            
            if (mtc.matches()) {
                try {
                    states.unit = (Unit)(states.handlers
                                         .get(mtc.group(2)).newInstance());
                    states.unit.value = Double.parseDouble(mtc.group(1));
                    states.current = states.conversion;
                } catch (InstantiationException | IllegalAccessException exp) {
                    logger.debug("Instantiation failed: " + mtc.group(2) +
                                 "(This shouldn't happen.)");
                }
            } else {
                states.current = states.error;
            }
        }
    }

    private class ConvertState extends State {

        private Pattern re = Pattern.compile("m|cm|'|\"");
        
        public ConvertState(States states) { this.states = states; }
        
        @Override public void next() {
            Matcher mtc = re.matcher(states.scanner.next());

            System.out.println("Please enter unuits to convert to (m, cm, ', \"):");
            states.backtrack = this;
            
            if (mtc.matches()) {
                try {
                    Class cls = states.handlers.get(mtc.group());
                    Unit u = (Unit)(cls.getDeclaredConstructor(cls).newInstance(states.unit));
                    System.out.println("After conversion: " + u);
                    states.current = states.input;
                } catch (InstantiationException |
                         IllegalAccessException |
                         NoSuchMethodException  |
                         InvocationTargetException exp) {
                    logger.debug("Instantiation failed: " + mtc.group() +
                                 "(This shouldn't happen.)");
                }
            } else {
                states.current = states.error;
            }
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
        System.out.println("Welcome to unit conversion program!");
        States states = new States();
        states.input = new InputState(states);
        states.conversion = new InputState(states);
        states.error = new ErrorState(states, app);
        states.current = states.input;
        
        while (true) states.current.next();
    }
}
