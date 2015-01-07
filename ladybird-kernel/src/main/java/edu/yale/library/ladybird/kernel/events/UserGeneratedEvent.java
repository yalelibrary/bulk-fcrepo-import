package edu.yale.library.ladybird.kernel.events;

public interface UserGeneratedEvent extends Event {

    String getPrincipal();

    String getValue();
}
