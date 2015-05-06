package edu.yale.library.ladybird.entity;

import java.io.Serializable;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class Settings implements Serializable {

    private Integer id;
    private String property;
    private String value;
    private Integer enabled;

    public Settings() {
    }

    public Settings(String property, String value, Integer enabled) {
        this.property = property;
        this.value = value;
        this.enabled = enabled;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Settings{"
                + "id=" + id
                + ", property='" + property + '\''
                + ", value='" + value + '\''
                + ", enabled=" + enabled
                + '}';
    }
}