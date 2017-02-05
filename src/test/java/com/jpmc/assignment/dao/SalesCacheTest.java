package com.jpmc.assignment.dao;

import org.junit.Test;

import com.jpmc.assignment.dao.SalesCache;


public class SalesCacheTest {
    private SalesCache salesCache = new SalesCache();

    @Test(expected = IllegalArgumentException.class)
    public void getSalesForGivenProductShouldThrowIllegalArgumentIfInvalidProductIsGiven() {
        salesCache.getSalesForGivenProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void storeAdjustmentMessageShouldThrowIllegalArgumentIfInvalidAdjustmentMessageIsGiven() {
        salesCache.storeAdjustmentMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSaleRecordShouldThrowIllegalArgumentExceptionWhenSaleIsInvalid() {
        salesCache.addSaleRecord(null);
    }
}
