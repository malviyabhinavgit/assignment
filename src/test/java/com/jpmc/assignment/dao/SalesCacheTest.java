package com.jpmc.assignment.dao;

import com.jpmc.assignment.dao.SalesCache;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;

import org.junit.Test;

import java.util.ArrayList;


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
