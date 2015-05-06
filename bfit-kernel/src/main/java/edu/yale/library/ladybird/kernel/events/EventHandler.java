package edu.yale.library.ladybird.kernel.events;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import edu.yale.library.ladybird.entity.event.Event;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class EventHandler {

    private static final Logger logger = getLogger(EventHandler.class);

    private static Module guiceModule;

    private static EventBus eventBus;

    public static void postEvent(final Event event) {
        if (eventBus == null) {
            final Injector injector = Guice.createInjector(guiceModule);
            eventBus = new EventBus();
            logger.debug("Inst. eventbus");
            final List<Class> globalListeners = injector.getInstance(List.class);
            for (Class o: globalListeners) {
                eventBus.register(injector.getInstance(o));
            }
        }
        eventBus.post(event);
    }

    public static void setGuiceModule(Module abstractModule) {
        if (guiceModule != null) {
            throw new UnsupportedOperationException("Already inst");
        }
        guiceModule = abstractModule;

    }

}
