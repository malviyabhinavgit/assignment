package com.jpmc.assignment.entity;


public class SimpleSaleMessage extends IncomingSaleMessage {

    private final Integer occurrence;

    public SimpleSaleMessage(Sale sale, Integer noOfOccurrence) {
        super(sale);
        this.occurrence = noOfOccurrence;
    }

    public SimpleSaleMessage(Sale sale) {
        super(sale);
        this.occurrence = 1;
    }

    public int getOccurrence() {
        return occurrence;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.SIMPLE_SALE_MESSAGE;
    }
}
