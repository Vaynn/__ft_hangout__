package com.e.ft_hangout.models;

public class Fox {
    private final Contact contact;
    private final Boolean known;

    public Fox(Contact contact, Boolean known) {
        this.contact = contact;
        this.known = known;
    }

    public Contact getContact() {
        return contact;
    }

    public Boolean getKnown() {
        return known;
    }
}
