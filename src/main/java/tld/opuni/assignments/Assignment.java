package tld.opuni.assignments;

/**
 * Abstract base class for all assignments.  All assignments can
 * be started from <code>App</code>.  <code>App</code> will call
 * <code>interact()</code> after it loads the assignment.
 */
public abstract class Assignment {

    /**
     * If assignment is supposed to interact with the user, its
     * interaction should start here.
     *
     * @param app The application launching the assignment.
     */
    public abstract void interact(App app);
}
