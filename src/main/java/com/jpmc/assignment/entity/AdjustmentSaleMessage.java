package com.jpmc.assignment.entity;


public class AdjustmentSaleMessage extends IncomingSaleMessage {
    private final Adjustment adjustment;

    public AdjustmentSaleMessage(Sale sale, Adjustment adjustment) {
        super(sale);
        this.adjustment = adjustment;
    }
    public Adjustment getAdjustment() {
        return adjustment;
    }

    @Override
    public String toString() {
        return "AdjustmentSaleMessage{" +
                "adjustment=" + adjustment + " product= "+getSale().getProduct()+" price= "+getSale().getPrice()+
                '}';
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.ADJUSTMENT_SALE_MESSAGE;
    }
}
