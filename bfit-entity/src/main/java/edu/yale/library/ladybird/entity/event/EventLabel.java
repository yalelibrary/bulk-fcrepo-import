package edu.yale.library.ladybird.entity.event;

/**
 * Possible label values. These values strings are used when writing events to db,
 * but these values do not represent the actual event type.
 *
 * @author Osman Din
 */
public enum EventLabel {
    USER_EDIT, OBJECT_ROLLBACK, FIELD_TRUNCATED, FIELD_UDPATE_FAILURE
}
