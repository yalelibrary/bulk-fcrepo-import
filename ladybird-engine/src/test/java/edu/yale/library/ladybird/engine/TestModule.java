package edu.yale.library.ladybird.engine;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import edu.yale.library.ladybird.engine.cron.DummyEventCounter;

import java.util.Collections;

public class TestModule implements Module {

    public void configure(Binder binder) {
    }

    @Provides
    java.util.List provideListeners() {
        return Collections.singletonList(DummyEventCounter.class);
    }

}
