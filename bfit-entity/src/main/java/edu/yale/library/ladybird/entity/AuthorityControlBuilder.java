package edu.yale.library.ladybird.entity;

import java.util.Date;

/**
 * @author Osman Din {@literal <osman.din.yale@gmail.com>}
 */
public class AuthorityControlBuilder {

    private AuthorityControl ac;
    private Date date;
    private int fdid;
    private String value;
    private int userId;
    private String code;

    public AuthorityControlBuilder setAc(AuthorityControl ac) {
        this.ac = ac;
        return this;
    }

    public AuthorityControlBuilder setDate(Date date) {
        this.date = date;
        return this;
    }

    public AuthorityControlBuilder setFdid(int fdid) {
        this.fdid = fdid;
        return this;
    }

    public AuthorityControlBuilder setValue(String value) {
        this.value = value;
        return this;
    }

    public AuthorityControlBuilder setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public AuthorityControlBuilder setCode(String code) {
        this.code = code;
        return this;
    }

    public AuthorityControl createAuthorityControl() {
        return new AuthorityControl(date, fdid, value, code, userId);
    }

    public AuthorityControl createAuthorityControl2() {
        return new AuthorityControl(ac);
    }
}