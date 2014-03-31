package edu.yale.library.ladybird.engine.model;


interface FieldDataValidator {
    boolean validate();

    boolean validate(FieldConstant f, String value);

}