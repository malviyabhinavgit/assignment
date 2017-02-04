package com.jpmc.assignment.model;


public class SimpleSaleMessage extends IncomingSaleMessage {

    private final Integer noOfOccurrence;

    public SimpleSaleMessage(Sale sale, Integer noOfOccurrence) {
        super(sale);
        this.noOfOccurrence = noOfOccurrence;
    }

    public int getNoOfOccurrence() {
        return noOfOccurrence;
    }

    public MessageType getMessageType() {
        return MessageType.SIMPLE_SALE_MESSAGE;
    }
}
