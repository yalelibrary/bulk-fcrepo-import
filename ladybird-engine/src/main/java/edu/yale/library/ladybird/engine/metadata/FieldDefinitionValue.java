package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.entity.FieldDefinition;

import java.util.List;

/**
 * Helper datastructure
 */
public class FieldDefinitionValue {

    private FieldDefinition fdid;

    private List<String> value;

    public FieldDefinition getFdid() {
        return fdid;
    }

    public void setFdid(FieldDefinition fdid) {
        this.fdid = fdid;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

    public FieldDefinitionValue(FieldDefinition fdid, List<String> value) {
        this.fdid = fdid;
        this.value = value;
    }

    @Override
    public String toString() {
        return "FieldDefinitionvalue{"
                + "fdid=" + fdid.getHandle()
                + ", value='" + value + '\''
                + '}';
    }
}