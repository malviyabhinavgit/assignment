package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.OrderCache;
import com.jpmc.assignment.handler.AdjustmentSaleMessageHandler;
import com.jpmc.assignment.model.Adjustment;
import com.jpmc.assignment.model.AdjustmentSaleMessage;
import com.jpmc.assignment.model.MessageType;
import com.jpmc.assignment.model.Sale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdjustmentSaleMessageHandlerTest {
    private static OrderCache orderCache;
    private static AdjustmentSaleMessageHandler handler;

    @BeforeClass
    public static void setupServices() {
        orderCache = new OrderCache();
        handler = new AdjustmentSaleMessageHandler();
        handler.setOrderCache(orderCache);
    }

    @Before
    public void setup() {
        orderCache.flush();
        List<Sale> sales = getSales("Apple", new BigDecimal(100));
        orderCache.updateProductSaleMap("Apple", sales);
    }

    @Test
    public void handleTestWithSingleTypeOfProduct() {
        Sale sale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);

        handler.handle(adjustmentSaleMessage);
        Assert.assertEquals(49,handler.getOrderCache().getSaleAdjustmentsMap().get(sale.getProduct()).size());
        Assert.assertEquals(new BigDecimal(110), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(0).getPrice());
        Assert.assertEquals(new BigDecimal(110), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(25).getPrice());
        Assert.assertEquals(new BigDecimal(110), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(48).getPrice());
        orderCache.flush();
        handler.getOrderCache().flush();

    }

    @Test
    public void testHandleWithADDAdjustmentForMultipleProducts() {
        setupMultipleProductSales();
        Sale sale = new Sale("Mango", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);

        handler.handle(adjustmentSaleMessage);

        Assert.assertEquals(98, orderCache.getSaleRecords().size());
        Assert.assertEquals(49, orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).size());
        Assert.assertEquals(null, orderCache.getSaleAdjustmentsMap().get("Apple"));
        Assert.assertEquals(new BigDecimal(110), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(0).getPrice());
        Assert.assertEquals(new BigDecimal(110), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(25).getPrice());
        Assert.assertEquals(new BigDecimal(110), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(48).getPrice());
        Assert.assertEquals(new BigDecimal(100), handler.getOrderCache().getProductSaleMap().get("Apple").get(48).getPrice());
        orderCache.flush();
        handler.getOrderCache().flush();
    }

    @Test
    public void testHandleWithSUBTRACTAdjustmentForMultipleProducts() {
        setupMultipleProductSales();
        Sale sale = new Sale("Mango", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.SUBTRACT);

        handler.handle(adjustmentSaleMessage);

        Assert.assertEquals(98, orderCache.getSaleRecords().size());
        Assert.assertEquals(49, orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).size());
        Assert.assertEquals(null, orderCache.getSaleAdjustmentsMap().get("Apple"));
        Assert.assertEquals(new BigDecimal(90), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(0).getPrice());
        Assert.assertEquals(new BigDecimal(90), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(25).getPrice());
        Assert.assertEquals(new BigDecimal(90), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(48).getPrice());
        Assert.assertEquals(new BigDecimal(100), handler.getOrderCache().getProductSaleMap().get("Apple").get(48).getPrice());
    }

    @Test
    public void testHandleWithMULTIPLYAdjustmentForMultipleProducts() {
        setupMultipleProductSales();
        Sale sale = new Sale("Mango", new BigDecimal(2));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.MULTIPLY);

        handler.handle(adjustmentSaleMessage);

        Assert.assertEquals(98, orderCache.getSaleRecords().size());
        Assert.assertEquals(49, orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).size());
        Assert.assertEquals(null, orderCache.getSaleAdjustmentsMap().get("Apple"));
        Assert.assertEquals(new BigDecimal(200), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(0).getPrice());
        Assert.assertEquals(new BigDecimal(200), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(25).getPrice());
        Assert.assertEquals(new BigDecimal(200), handler.getOrderCache().getProductSaleMap().get(sale.getProduct()).get(48).getPrice());
        Assert.assertEquals(new BigDecimal(100), handler.getOrderCache().getProductSaleMap().get("Apple").get(48).getPrice());
    }

    @Test
    public void testHandleWithALLAdjustmentForWithMultipleProduct() {
        setupMultipleProductSales();
        Sale sale = new Sale("Mango", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);

        Assert.assertEquals(98,orderCache.getSaleRecords().size());
        Assert.assertEquals(new BigDecimal(110),orderCache.getProductSaleMap().get(sale.getProduct()).get(0).getPrice());
        Assert.assertEquals(new BigDecimal(10),orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).get(0).getSale().getPrice());
        Assert.assertEquals(49,orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).size());

        adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.SUBTRACT);
        handler.handle(adjustmentSaleMessage);

        Assert.assertEquals(98,orderCache.getSaleRecords().size());
        Assert.assertEquals(new BigDecimal(100),orderCache.getProductSaleMap().get(sale.getProduct()).get(0).getPrice());
        Assert.assertEquals(new BigDecimal(10),orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).get(0).getSale().getPrice());
        Assert.assertEquals(98,orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).size());

        sale = new Sale("Apple", new BigDecimal(10));
        adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.MULTIPLY);
        handler.handle(adjustmentSaleMessage);

        Assert.assertEquals(98,orderCache.getSaleRecords().size());
        Assert.assertEquals(new BigDecimal(1000),orderCache.getProductSaleMap().get(sale.getProduct()).get(0).getPrice());
        Assert.assertEquals(new BigDecimal(10),orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).get(0).getSale().getPrice());
        Assert.assertEquals(49,orderCache.getSaleAdjustmentsMap().get(sale.getProduct()).size());
    }

    @Test
    public void testHandleWithAddAdjustmentForNonExistingProduct() {
        setupMultipleProductSales();
        Sale sale = new Sale("Cucumber", new BigDecimal(0));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);

        handler.handle(adjustmentSaleMessage);

        Assert.assertEquals(98,orderCache.getSaleRecords().size());
        Assert.assertEquals(null,orderCache.getProductSaleMap().get(sale.getProduct()));
        Assert.assertEquals(null,orderCache.getSaleAdjustmentsMap().get(sale.getProduct()));
    }

    private List<Sale> getSales( String product, BigDecimal price) {
        List<Sale> sales = new ArrayList<Sale>();
        for(int i=0;i<49;i++){
            Sale sale1 = new Sale(product, price);
            sales.add(sale1);
        }
        return sales;
    }

    private void setupMultipleProductSales() {
        orderCache.flush();
        List<Sale> mangoSales = getSales("Mango", new BigDecimal(100));
        orderCache.updateProductSaleMap("Mango", mangoSales);
        orderCache.setSaleRecords(mangoSales);

        List<Sale> appleSales = getSales("Apple", new BigDecimal(100));
        orderCache.updateProductSaleMap("Apple", appleSales);
        orderCache.setSaleRecords(appleSales);
        handler.setOrderCache(orderCache);
    }
}
