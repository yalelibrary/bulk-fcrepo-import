package edu.yale.library.events.exports;

import edu.yale.library.events.Event;

import java.util.Date;

/**
 * An import event. Classes may subcass this. Subject to modification.
 */
public class ExportEvent implements Event {
    /* TIme started */
    private Date startTime;

    /* Time ended */
    private Date endTime;

}
