package tld.opuni.assignments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Assignment11 extends Assignment {

    static final Logger logger = LoggerFactory.getLogger(Assignment11.class);
    
    private abstract class Unit {

        protected double value;
        
        public double getValue() { return value; }
        
        protected double getThreshold() { return 100; }
        
        protected double round(double value) {
            return Math.round(value * getThreshold()) / (getThreshold() * 10);
        }
    }

    private class Centimeter extends Unit {
        
        public Centimeter(Centimeter unit) { value = unit.getValue(); }

        public Centimeter(Meter unit) { value = unit.getValue() * 100d; }

        public Centimeter(Inch unit) { value = unit.getValue() * 2.54d; }

        public Centimeter(Foot unit) { value = new Inch(unit).getValue() * 12d; }

        public Centimeter(Unit unit) {
            throw new RuntimeException("Unkown unit: " + unit);
        }
    }

    private class Meter extends Unit {
        
        public Meter(Centimeter unit) { value = unit.getValue() / 100d; }

        public Meter(Unit unit) { this(new Centimeter(unit)); }
    }

    private class Inch extends Unit {
        
        public Inch(Centimeter unit) { value = unit.getValue() / 2.54d; }

        public Inch(Unit unit) { this(new Centimeter(unit)); }
    }

    private class Foot extends Unit {
        
        public Foot(Centimeter unit) { value = new Inch(unit).getValue() / 12d; }

        public Foot(Unit unit) { this(new Centimeter(unit)); }
    }
    
    @Override public void interact() {
        logger.debug("interacting");
    }
}
