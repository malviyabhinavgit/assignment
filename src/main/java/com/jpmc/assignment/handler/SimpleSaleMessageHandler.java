package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.OrderCache;
import com.jpmc.assignment.model.IncomingSaleMessage;
import com.jpmc.assignment.model.Sale;
import com.jpmc.assignment.model.SimpleSaleMessage;

import java.util.ArrayList;
import java.util.List;


public class SimpleSaleMessageHandler implements MessageHandler{

    private OrderCache orderCache;
    /**
     *
     * @param incomingSaleMessage  sale message to be processed
     */
    public void handle(IncomingSaleMessage incomingSaleMessage) {

        if(incomingSaleMessage == null) {
            throw new IllegalArgumentException("Invalid Recurrent Sales Message received");
        }
        int count = 0;

        SimpleSaleMessage simpleSaleMessage = (SimpleSaleMessage) incomingSaleMessage;
        Sale sale = simpleSaleMessage.getSale();
        orderCache.addSaleRecords(sale);
        List<Sale> sales = orderCache.getSalesForGivenProduct(sale.getProduct());
        if(sales == null) {
            sales = new ArrayList<Sale>();
        }

        while (count < simpleSaleMessage.getNoOfOccurrence()) {
            sales.add(sale);
            count++;
        }

        orderCache.updateProductSaleMap(sale.getProduct(), sales);

    }

    public OrderCache getOrderCache() {
        return orderCache;
    }

    public void setOrderCache(OrderCache orderCache) {
        this.orderCache = orderCache;
    }
}
