package edu.yale.library.ladybird.engine;


import edu.yale.library.ladybird.engine.model.FieldConstant;

interface FieldDataValidator {
    boolean validate();

    boolean validate(FieldConstant f, String value);

}