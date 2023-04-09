package com.yng.yngweekend.domain;

public class Listener {

    public enum Type{
        KAFKA, MQ, FILE
    }

    public String toString(){
        return "listner";
    }
}
