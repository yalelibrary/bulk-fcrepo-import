package edu.yale.library.events.imports;

import edu.yale.library.beans.User;
import edu.yale.library.events.Event;

import java.util.Date;

/**
 * An import event. Classes may subcass this. Subject to modification.
 */
public class ImportEvent implements Event {
    /* User requesting action */
    private User user;

    /* TIme started */
    private Date startTime;

    /* Time ended */
    private Date endTime;

}
