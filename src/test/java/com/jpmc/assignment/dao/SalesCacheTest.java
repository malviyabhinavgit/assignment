package com.jpmc.assignment.dao;

import org.junit.Test;


public class SalesCacheTest {
    private SalesCache salesCache = new SalesCache();

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfInvalidProductIsGiven() {
        salesCache.getSalesForGivenProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentIfInvalidAdjustmentMessageIsGiven() {
        salesCache.storeAdjustmentMessage(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenInvalidSaleIsGiven() {
        salesCache.addSaleRecord(null);
    }
}
