package edu.yale.library.ladybird.engine;


import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;

import java.util.Collections;
import java.util.List;

public class ListenerModule implements Module {

    public void configure(Binder binder) {
        //binder.bind(ProgressEventChangeRecorder.class);
    }

    @Provides
    List provideListeners() {
        return Collections.singletonList(ProgressEventListener.class);
    }
}
