package edu.yale.library.ladybird.engine;

import com.google.common.eventbus.EventBus;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import edu.yale.library.ladybird.kernel.events.Event;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class ExportBus {
    private static final Logger logger = getLogger(ExportBus.class);

    private static Module module;
    private static EventBus eventBus;

    public void init() {
        try {
            final ExportBus kernelContext = new ExportBus();
            kernelContext.setAbstractModule(new ListenerModule());

            if (eventBus == null) {
                eventBus = getEventBus();
            }
        } catch (Throwable t) {
            logger.error("Error in context initialization", t);
        }
    }

    private static Module getModule() {
        return module;
    }

    public void setAbstractModule(Module abstractModule) {
        this.module = abstractModule;
    }

    /**
     * Get the event bus and instantiate listeners
     * @return
     */
    private static EventBus getEventBus() {
        final Injector injector = Guice.createInjector(getModule()); //TODO

        if (eventBus == null) {
            logger.debug("Inst. Export EventBus");
            eventBus = new EventBus();
            final List<Class> globalListeners = injector.getInstance(List.class);
            for (Class o: globalListeners) {
                eventBus.register(injector.getInstance(o));
            }
        }
        return eventBus;
    }

    public static void post(final Event event) {
        getEventBus().post(event);
    }

}
