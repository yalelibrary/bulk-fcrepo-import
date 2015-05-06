package edu.yale.library.ladybird.entity.event;

import edu.yale.library.ladybird.entity.EventType;

/**
 * Represents user object editing activity.
 *
 * @author Osman Din
 */
public final class UserEditEvent extends EventType {

    public UserEditEvent() {
        super(EventLabel.USER_EDIT.name());
    }
}
