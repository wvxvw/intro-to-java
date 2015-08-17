package tld.opuni.assignments;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.log4j.PropertyConfigurator;

import tld.opuni.assignments.App;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    private static class TestApp extends App {
        
        TestApp() { PropertyConfigurator.configure("./etc/log4j.properties"); }
        
        public String testNormalize(final String denorm) throws App.InvalidAssignment {
            return super.normalize(denorm);
        }
    }

    private static class TestAssignment11a extends Assignment11a {

        private double convert(final Unit from, final Unit to, final double value) {
            from.init(value);
            to.init(from);
            return to.value;
        }
        
        public double cmToCm(final double cm) {
            return convert(new Centimeter(), new Centimeter(), cm);
        }

        public double cmToM(final double cm) {
            return convert(new Centimeter(), new Meter(), cm);
        }

        public double cmToInch(final double cm) {
            return convert(new Centimeter(), new Inch(), cm);
        }
        
        public double cmToFoot(final double cm) {
            return convert(new Centimeter(), new Foot(), cm);
        }
        
        public double mToCm(final double m) {
            return convert(new Meter(), new Centimeter(), m);
        }

        public double mToM(final double m) {
            return convert(new Meter(), new Meter(), m);
        }

        public double mToInch(final double m) {
            return convert(new Meter(), new Inch(), m);
        }

        public double mToFoot(final double m) {
            return convert(new Meter(), new Foot(), m);
        }
        
        public double inchToCm(final double inch) {
            return convert(new Inch(), new Centimeter(), inch);
        }

        public double inchToM(final double inch) {
            return convert(new Inch(), new Meter(), inch);
        }

        public double inchToInch(final double inch) {
            return convert(new Inch(), new Inch(), inch);
        }

        public double inchToFoot(final double inch) {
            return convert(new Inch(), new Foot(), inch);
        }
        
        public double footToCm(final double foot) {
            return convert(new Foot(), new Centimeter(), foot);
        }

        public double footToM(final double foot) {
            return convert(new Foot(), new Meter(), foot);
        }

        public double footToInch(final double foot) {
            return convert(new Foot(), new Inch(), foot);
        }

        public double footToFoot(final double foot) {
            return convert(new Foot(), new Foot(), foot);
        }
    }
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) { super(testName); }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Test parsing assignment label.
     */
    public void testAssignmentNormalization() {
        final TestApp app = new TestApp();
        try {
            assertEquals("11a", app.testNormalize("11A"));
            assertEquals("11a", app.testNormalize("11-a"));
            assertEquals("11a", app.testNormalize("11.0"));
            assertEquals("11a", app.testNormalize("1.0"));
            assertEquals("11a", app.testNormalize("1 0"));
        } catch (App.InvalidAssignment expt) {
            fail();
        }
    }

    public void testConversion() {
        final TestAssignment11a assignment = new TestAssignment11a();
        
        assertEquals(1d,          assignment.cmToCm(1d), 0.001d);
        assertEquals(1d,          assignment.cmToM(100d), 0.001d);
        assertEquals(1d,          assignment.cmToInch(2.54d), 0.001d);
        assertEquals(1d,          assignment.cmToFoot(12d * 2.54d), 0.001d);
        assertEquals(100d,        assignment.mToCm(1d), 0.001d);
        assertEquals(1d,          assignment.mToM(1d), 0.001d);
        assertEquals(1000d,       assignment.mToInch(25.4d), 0.001d);
        assertEquals(1000d,       assignment.mToFoot(25.4d * 12d), 0.001d);
        assertEquals(100d,        assignment.inchToCm(39.3701d), 0.001d);
        assertEquals(1d,          assignment.inchToM(39.3701d), 0.001d);
        assertEquals(1d,          assignment.inchToInch(1d), 0.001d);
        assertEquals(1d,          assignment.inchToFoot(12d), 0.001d);
        assertEquals(25.4d * 12d, assignment.footToCm(10d), 0.001d);
        assertEquals(2.54d * 12d, assignment.footToM(100d), 0.001d);
        assertEquals(12d,         assignment.footToInch(1d), 0.001d);
        assertEquals(1d,          assignment.footToFoot(1d), 0.001d);
    }
}
