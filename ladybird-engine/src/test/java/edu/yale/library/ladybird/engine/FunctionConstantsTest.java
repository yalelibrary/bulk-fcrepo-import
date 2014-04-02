package edu.yale.library.ladybird.engine;

import edu.yale.library.ladybird.engine.model.FunctionConstants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionConstantsTest {

    @Test
    public void shouldEqualName() {
        String val = "F104";
        assertEquals("Name mismatch", val, FunctionConstants.F104.getName());
    }
}
