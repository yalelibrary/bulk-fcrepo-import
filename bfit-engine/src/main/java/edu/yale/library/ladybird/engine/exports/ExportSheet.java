package edu.yale.library.ladybird.engine.exports;

import edu.yale.library.ladybird.engine.imports.Import;

import java.util.List;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class ExportSheet {

    private String title;

    private List<Import.Row> contents;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Import.Row> getContents() {
        return contents;
    }

    public void setContents(List<Import.Row> contents) {
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
