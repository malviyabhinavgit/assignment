package com.jpmc.assignment.dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;


public class SalesCache implements SalesRepository {
    private final Map<String, ConcurrentLinkedQueue<AdjustmentSaleMessage>> saleAdjustmentsMap = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentLinkedQueue<Sale>> productSaleMap = new ConcurrentHashMap<>();


    @Override
    public Collection<Sale> getSalesForGivenProduct(String product) {
        if (product == null) {
            throw new IllegalArgumentException("Invalid Product received");
        }
        return productSaleMap.get(product);
    }


    @Override
    public void storeAdjustmentMessage(AdjustmentSaleMessage adjustmentSaleMessage) {
        if (adjustmentSaleMessage == null || adjustmentSaleMessage.getSale() == null || adjustmentSaleMessage.getSale().getProduct() == null) {
            throw new IllegalArgumentException("Invalid product & adjustment sales combination received");
        }

        ConcurrentLinkedQueue<AdjustmentSaleMessage> exitingMessages = saleAdjustmentsMap.putIfAbsent(adjustmentSaleMessage.getSale().getProduct(), new ConcurrentLinkedQueue<>(Collections.singletonList(adjustmentSaleMessage)));
        if (exitingMessages != null) {
            exitingMessages.add(adjustmentSaleMessage);
        }
    }


    @Override
    public void addSaleRecord(Sale sale) {
        if (sale == null || sale.getProduct() == null) {
            throw new IllegalArgumentException("Invalid product & sale combination received");
        }
        ConcurrentLinkedQueue<Sale> existingSaleRecords = productSaleMap.putIfAbsent(sale.getProduct(), new ConcurrentLinkedQueue<>(Collections.singletonList(sale)));
        if (existingSaleRecords != null) {
            existingSaleRecords.add(sale);
        }
    }

    @Override
    public Map<String, ConcurrentLinkedQueue<Sale>> getAllSales() {
        return this.productSaleMap;
    }


    @Override
    public Map<String, ConcurrentLinkedQueue<AdjustmentSaleMessage>> getAllProcessedAdjustmentSaleMessages() {
        return this.saleAdjustmentsMap;
    }

}
