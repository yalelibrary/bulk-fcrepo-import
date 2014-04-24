package edu.yale.library.ladybird.engine;


import edu.yale.library.ladybird.engine.model.FieldConstant;

public class DefaultFieldDataValidator implements FieldDataValidator {
    /**
     * TODO remove
     *
     * @return
     */
    public boolean validate() {
        return true;
    }

    /**
     * TODO
     * Checks whether value corresponding to header value is OK
     *
     * @param f
     * @param value
     * @return
     */
    public boolean validate(FieldConstant f, String value) {
        return false;
    }

}