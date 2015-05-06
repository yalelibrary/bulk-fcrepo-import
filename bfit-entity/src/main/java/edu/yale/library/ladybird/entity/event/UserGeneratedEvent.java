package edu.yale.library.ladybird.entity.event;


/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public interface UserGeneratedEvent extends Event {

    String getPrincipal();

    String getValue();
}
