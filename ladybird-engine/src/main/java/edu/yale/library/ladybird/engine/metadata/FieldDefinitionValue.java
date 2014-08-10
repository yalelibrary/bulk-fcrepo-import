package edu.yale.library.ladybird.engine.metadata;

import edu.yale.library.ladybird.entity.FieldDefinition;

/**
 * Helper datastructure used to pass field definition values for template projects around.
 */
public class FieldDefinitionValue {
    FieldDefinition fdid;
    String value;

    public FieldDefinition getFdid() {
        return fdid;
    }

    public void setFdid(FieldDefinition fdid) {
        this.fdid = fdid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FieldDefinitionValue(FieldDefinition fdid, String value) {
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