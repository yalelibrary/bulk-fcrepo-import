package edu.yale.library.ladybird.engine.oai;

import org.junit.Test;

import java.util.Collections;

public class ImportSourceDataWriterTest {

    @Test
    public void shouldPersistMarcData() { //TODO
        ImportSourceDataWriter importSourceDataWriter = new ImportSourceDataWriter();
        importSourceDataWriter.write(Collections.emptyList(), 0);
    }
}
