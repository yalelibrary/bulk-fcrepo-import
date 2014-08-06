package edu.yale.library.ladybird.entity.event;

import edu.yale.library.ladybird.entity.EventType;

/**
 *  Represents user rollback event activity
 */
public final class RollbackEventType extends EventType {

    public RollbackEventType() {
        super(EventLabel.OBJECT_ROLLBACK.name());
    }
}
