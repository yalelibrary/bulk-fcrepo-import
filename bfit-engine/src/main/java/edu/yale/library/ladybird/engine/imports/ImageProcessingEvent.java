package edu.yale.library.ladybird.engine.imports;

import edu.yale.library.ladybird.entity.event.ImportEvent;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public final class ImageProcessingEvent extends ImportEvent {

    private  int importId;

    private int conversions;

    private long duration;

    public int getConversions() {
        return conversions;
    }

    public void setConversions(int conversions) {
        this.conversions = conversions;
    }

    @Override
    public int getImportId() {
        return importId;
    }

    @Override
    public void setImportId(int importId) {
        this.importId = importId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "MediaProcessingCompleteEvent{" + "importId=" + importId
                + ", duration=" + duration + '}';
    }

    @Override
    public String getEventName() {
        return "Media";
    }
}
