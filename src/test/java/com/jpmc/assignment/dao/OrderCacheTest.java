package com.jpmc.assignment.dao;

import com.jpmc.assignment.dao.OrderCache;
import com.jpmc.assignment.model.Adjustment;
import com.jpmc.assignment.model.AdjustmentSaleMessage;
import com.jpmc.assignment.model.Sale;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class OrderCacheTest {
    private OrderCache orderCache;

    @Test(expected = IllegalArgumentException.class)
    public void getSaleForGivenProductShouldThrowIllegalArgumentExceptionWhenProductIsInvalid() {
        setup();
        orderCache.getSalesForGivenProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateProductSaleMapShouldThrowIllegalArgumentExceptionWhenProductIsInvalid() {
        setup();
        orderCache.updateProductSaleMap(null, new ArrayList<Sale>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateProductSaleMapShouldThrowIllegalArgumentExceptionWhenSaleIsNull() {
        setup();
        orderCache.updateProductSaleMap("Apple", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateProductSaleMapShouldThrowIllegalArgumentExceptionWhenSaleIsEmpty() {
        setup();
        orderCache.updateProductSaleMap("Apple", new ArrayList<Sale>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAdjustmentSaleMessageShouldThrowIllegalArgumentExceptionWhenProductIsInvalid() {
        setup();
        orderCache.getAdjustmentSaleMessageForGivenProduct(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public  void updateSaleAdjustmentMapShouldThrowIllegalArgumentExceptionWhenProductIsInvalid() {
        setup();
        orderCache.updateSaleAdjustmentMap(null, new ArrayList<AdjustmentSaleMessage>());
    }

    @Test(expected = IllegalArgumentException.class)
    public  void updateSaleAdjustmentMapShouldThrowIllegalArgumentExceptionWhenAdjustmentSaleMessageListIsEmpty() {
        setup();
        orderCache.updateSaleAdjustmentMap("Apple", new ArrayList<AdjustmentSaleMessage>());
    }

    @Test(expected = IllegalArgumentException.class)
    public  void updateSaleAdjustmentMapShouldThrowIllegalArgumentExceptionWhenAdjustmentSaleMessageListIsInvalid() {
        setup();
        orderCache.updateSaleAdjustmentMap("Apple", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addSaleRecordsShouldThrowIllegalArgumentExceptionWhenSaleIsInvalid() {
        setup();
        orderCache.addSaleRecords(null);
    }

    @Test
    public void addSaleRecordsShouldAddNewRecord() {
        setup();
        orderCache.flush();
        Sale sale = new Sale("Apple", new BigDecimal(10));
        orderCache.addSaleRecords(sale);
        Assert.assertEquals(1, orderCache.getSaleRecords().size());
        Assert.assertEquals(sale, orderCache.getSaleRecords().get(0));
        Assert.assertEquals(sale.getPrice(), orderCache.getSaleRecords().get(0).getPrice());
        flush();
    }

    @Test
    public void getSalesForGivenProductShouldReturnSaleListOfSalesForGivenProduct(){
        setup();
        Assert.assertEquals(49,orderCache.getSalesForGivenProduct("Apple").size());
        flush();
    }

    @Test
    public void updateProductSaleMapShouldUpdateSalesForGivenProduct() {
        setup();
        Assert.assertEquals(49,orderCache.getSalesForGivenProduct("Apple").size());
        List<Sale> sales = new ArrayList<Sale>();
        sales.add(new Sale("Apple", new BigDecimal(10)));
        orderCache.updateProductSaleMap("Apple", sales);
        Assert.assertEquals(1,orderCache.getSalesForGivenProduct("Apple").size());
        flush();
    }

    @Test
    public void getAdjustmentSaleMessageShouldGiveAdjustmentSaleMessageForGivenPrduct() {
        setupAdjustments();
        Assert.assertEquals(1, orderCache.getAdjustmentSaleMessageForGivenProduct("Apple").size());
        Assert.assertEquals("Apple", orderCache.getAdjustmentSaleMessageForGivenProduct("Apple").get(0).getSale().getProduct());
        flush();
    }

    @Test
    public void updateSaleAdjustmentMapShouldUpdateAdjustmentSaleMessageForGivenPrduct() {
        setupAdjustments();
        Assert.assertEquals(1, orderCache.getAdjustmentSaleMessageForGivenProduct("Apple").size());
        Assert.assertEquals("Apple", orderCache.getAdjustmentSaleMessageForGivenProduct("Apple").get(0).getSale().getProduct());

        Sale adjustmentSale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        List<AdjustmentSaleMessage> adjustmentSaleMessages = new ArrayList<AdjustmentSaleMessage>();
        adjustmentSaleMessages.add(adjustmentSaleMessage);
        adjustmentSaleMessages.add(adjustmentSaleMessage);
        orderCache.updateSaleAdjustmentMap("Apple", adjustmentSaleMessages);

        Assert.assertEquals(2, orderCache.getAdjustmentSaleMessageForGivenProduct("Apple").size());
        Assert.assertEquals("Apple", orderCache.getAdjustmentSaleMessageForGivenProduct("Apple").get(0).getSale().getProduct());
        flush();
    }





    private void flush() {
        orderCache.flush();
    }



    private void setup() {
        orderCache = new OrderCache();
        List<Sale> mangoSales = getSales("Mango", new BigDecimal(100));
        orderCache.updateProductSaleMap("Mango", mangoSales);
        orderCache.setSaleRecords(mangoSales);

        List<Sale> appleSales = getSales("Apple", new BigDecimal(100));
        orderCache.updateProductSaleMap("Apple", appleSales);
        orderCache.setSaleRecords(appleSales);

    }

    private void setupAdjustments() {
        orderCache = new OrderCache();
        List<Sale> mangoSales = getSales("Mango", new BigDecimal(100));
        orderCache.updateProductSaleMap("Mango", mangoSales);
        orderCache.setSaleRecords(mangoSales);

        List<Sale> appleSales = getSales("Apple", new BigDecimal(100));
        orderCache.updateProductSaleMap("Apple", appleSales);
        orderCache.setSaleRecords(appleSales);

        Sale adjustmentSale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        List<AdjustmentSaleMessage> adjustmentSaleMessages = new ArrayList<AdjustmentSaleMessage>();
        adjustmentSaleMessages.add(adjustmentSaleMessage);
        orderCache.updateSaleAdjustmentMap("Apple", adjustmentSaleMessages);
    }

    private List<Sale> getSales( String product, BigDecimal price) {
        List<Sale> sales = new ArrayList<Sale>();
        for(int i=0;i<49;i++){
            Sale sale1 = new Sale(product, price);
            sales.add(sale1);
        }
        return sales;
    }
}
