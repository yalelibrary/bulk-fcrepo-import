package edu.yale.library.ladybird.engine.model;

import edu.yale.library.ladybird.kernel.beans.FieldDefinition;

import java.util.Map;
import java.util.HashMap;

/**
 * Extends a FieldDefinition and implements a FieldConstant (e.g. FDID90("Subject, topic", "Subject, topic{fdid=90}"))
 * Subject to removal (when model is moved another package) or re-design.
 */
public class FieldDefinitionValue extends FieldDefinition implements FieldConstant {

    private static Map<String, FieldConstant> fieldDefMap; // tmp; loaded at db init

    public static Map<String, FieldConstant> getFieldDefMap() {
        if (fieldDefMap == null) {
            fieldDefMap = new HashMap<>();
        }
        return fieldDefMap;
    }

    public void setFieldDefMap(Map<String, FieldConstant> fieldDefMap) {
        this.fieldDefMap = fieldDefMap;
    }

    public FieldDefinitionValue() {
        super();
    }

    public FieldDefinitionValue(int fdid, String name) {
        super(fdid, name);
    }

    @Override
    public String getName() {
        return super.getHandle();
    }

    @Override
    public void setName(String s) {
        //TODO
    }

    @Override
    public String getTitle() {
        return "N/A";
    }

    @Override
    public void setTitle(String s) {
        //TODO
    }
}
