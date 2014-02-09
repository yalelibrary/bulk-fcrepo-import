package edu.yale.library.engine.model;

import edu.yale.library.engine.model.FieldConstant;

interface FieldDataValidator
{
    boolean validate();

    boolean validate(FieldConstant f, String value);

}