package com.jpmc.assignment.entity;


public enum MessageType {

    SIMPLE_SALE_MESSAGE("SimpleSaleMessage"),ADJUSTMENT_SALE_MESSAGE("AdjustmentSaleMessage");

    private final String messgeType;
    private MessageType(String messgeType) {
        this.messgeType = messgeType;
    }

}
