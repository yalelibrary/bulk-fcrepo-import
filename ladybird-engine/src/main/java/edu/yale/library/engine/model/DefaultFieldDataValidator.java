package edu.yale.library.engine.model;


public class DefaultFieldDataValidator implements FieldDataValidator
{
    /**
     * TODO remove
     * @return
     */
    public boolean validate()
    {
        return true;
    }

    /** TODO
     * Checks whether value corresponding to header value is OK
     * @param f
     * @param value
     * @return
     */
    public boolean validate(FieldConstant f, String value)
    {
        return false;
    }

}