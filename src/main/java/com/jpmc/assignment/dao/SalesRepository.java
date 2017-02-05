package com.jpmc.assignment.dao;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;


public interface SalesRepository {

    Collection<Sale> getSalesForGivenProduct(String product);

    void storeAdjustmentMessage(AdjustmentSaleMessage adjustmentSaleMessage);

    void addSaleRecord(Sale sale);

    Map<String,ConcurrentLinkedQueue<Sale>> getAllSales();

    Map<String,ConcurrentLinkedQueue<AdjustmentSaleMessage>> getAllProcessedAdjustmentSaleMessages();

}

