package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.IncomingSaleMessage;
import com.jpmc.assignment.entity.Sale;
import com.jpmc.assignment.entity.SimpleSaleMessage;

import java.util.stream.IntStream;


public class SimpleSaleMessageHandler implements MessageHandler{

    private final SalesRepository salesRepository;

    public SimpleSaleMessageHandler(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Override
    public void handle(IncomingSaleMessage incomingSaleMessage) {

        if(incomingSaleMessage == null || !(incomingSaleMessage instanceof SimpleSaleMessage) || ((SimpleSaleMessage) incomingSaleMessage).getOccurrence() <= 0) {
            throw new IllegalArgumentException("Invalid  Sales Message received "+ incomingSaleMessage);
        }

        SimpleSaleMessage simpleSaleMessage = (SimpleSaleMessage) incomingSaleMessage;
        Sale sale = simpleSaleMessage.getSale();
        IntStream.rangeClosed(1,simpleSaleMessage.getOccurrence()).parallel().forEach( i-> salesRepository.addSaleRecord(sale));

    }

}
