package edu.yale.library.events.exports;

import edu.yale.library.beans.User;
import edu.yale.library.events.Event;

import java.util.Date;

/**
 * An import event. Classes may subcass this. Subject to modification.
 */
public class ExportEvent implements Event
{
    /* TIme started */
    Date startTime;

    /* Time ended */
    Date endTime;

}
