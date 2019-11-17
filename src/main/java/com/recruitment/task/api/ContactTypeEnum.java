package com.recruitment.task.api;

public enum ContactTypeEnum {
    UNKNOWN(1),
    EMAIL(2),
    PHONE(3),
    JABBER(4);

    private int shortForm;

    ContactTypeEnum(int shortForm){
        this.shortForm = shortForm;
    }

    public int getShortForm() {
        return shortForm;
    }
}
