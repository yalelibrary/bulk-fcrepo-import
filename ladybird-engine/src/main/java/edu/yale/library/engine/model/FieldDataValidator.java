package edu.yale.library.engine.model;


interface FieldDataValidator {
    boolean validate();

    boolean validate(FieldConstant f, String value);

}