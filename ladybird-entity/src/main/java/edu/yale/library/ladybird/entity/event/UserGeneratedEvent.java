package edu.yale.library.ladybird.entity.event;

public interface UserGeneratedEvent extends Event {

    String getPrincipal();

    String getValue();
}
