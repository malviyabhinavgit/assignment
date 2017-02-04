package com.jpmc.assignment.model;


public abstract class IncomingSaleMessage {
    private final Sale sale;


    public IncomingSaleMessage(Sale sale) {
        this.sale = sale;
    }

    public abstract MessageType getMessageType();

    public Sale getSale() {
        return sale;
    }


}
