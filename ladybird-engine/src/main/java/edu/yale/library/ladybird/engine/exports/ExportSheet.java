package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.ImportEntity;

import java.util.List;

/**
 * Abstraction for a sheet to be written
 */
public class ExportSheet {

    private String title;
    private List<ImportEntity.Row> contents;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ImportEntity.Row> getContents() {
        return contents;
    }

    public void setContents(List<ImportEntity.Row> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return "ExportSheet{"
                + "title='" + title + '\''
                + ", contents=" + contents
                + '}';
    }
}
