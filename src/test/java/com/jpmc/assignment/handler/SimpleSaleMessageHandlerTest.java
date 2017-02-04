package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.OrderCache;
import com.jpmc.assignment.handler.SimpleSaleMessageHandler;
import com.jpmc.assignment.model.Sale;
import com.jpmc.assignment.model.SimpleSaleMessage;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;


public class SimpleSaleMessageHandlerTest {

    private OrderCache orderCache;
    private SimpleSaleMessageHandler handler;

    @Before
    public void setup(){
        orderCache = new OrderCache();
        handler = new SimpleSaleMessageHandler();
        handler.setOrderCache(orderCache);
    }

    @Test
    public void handleTest() {
        Sale sale = new Sale("Apple", new BigDecimal(10));
        SimpleSaleMessage simpleSaleMessage = new SimpleSaleMessage(sale, 10);

        handler.handle(simpleSaleMessage);
        List<Sale> sales = orderCache.getSalesForGivenProduct(sale.getProduct());
        Assert.assertEquals(10, sales.size());
    }
}
