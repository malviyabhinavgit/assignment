package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.SalesCache;
import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Adjustment;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;
import com.jpmc.assignment.handler.AdjustmentSaleMessageHandler;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;


public class AdjustmentSaleMessageHandlerTest {


    private SalesRepository salesRepository = mock(SalesRepository.class);
    private AdjustmentSaleMessageHandler handler;

    @Test
    public void handleTestWithSingleTypeOfProduct() {
        setup();
        Sale adjustmentSale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);
        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        Collection<Sale> adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertEquals(new BigDecimal(110), adjustedSale.getPrice()));
    }

    @Test
    public void testHandleWithADDAdjustmentForMultipleProducts() {
        setupMultipleProducts();
        Sale sale = new Sale("Mango", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        Collection<Sale> mangoSales = salesRepository.getSalesForGivenProduct("Mango");
        Assert.assertEquals(10, mangoSales.size());
        //check if mango prices have been adjusted
        mangoSales.stream().forEach(mangoSale -> Assert.assertEquals(new BigDecimal(30), mangoSale.getPrice()));

        Collection<Sale> appleSales = salesRepository.getSalesForGivenProduct("Apple");
        //check if apple sales are intact
        Assert.assertEquals(10, appleSales.size());
        appleSales.stream().forEach(appleSale -> Assert.assertEquals(new BigDecimal(100), appleSale.getPrice()));

    }

    @Test
    public void testHandleWithALLAdjustmentForProduct() {
        setup();
        Sale sale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        Collection<Sale> adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertEquals(new BigDecimal(110), adjustedSale.getPrice()));

        adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.SUBTRACT);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertEquals(new BigDecimal(100), adjustedSale.getPrice()));


        adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.MULTIPLY);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertEquals(new BigDecimal(1000), adjustedSale.getPrice()));
    }

    @Test
    public void testHandleWithAddAdjustmentForNonExistingProduct() {
        setup();
        Sale sale = new Sale("Cucumber", new BigDecimal(0));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);
        verify(salesRepository,times(0)).storeAdjustmentMessage(adjustmentSaleMessage);
        Collection<Sale> sales = salesRepository.getSalesForGivenProduct("Cucumber");
        Collection<Sale> appleSales = salesRepository.getSalesForGivenProduct("Apple");
        //Apple sales are intact
        appleSales.stream().forEach(appleSale -> Assert.assertEquals(new BigDecimal(100), appleSale.getPrice()));
        Assert.assertEquals(0, sales.size());
    }

    private void setup(){
        Collection<Sale> sales = getSales("Apple", 100, 10);
        when(salesRepository.getSalesForGivenProduct("Apple")).thenReturn(sales);
        doNothing().when(salesRepository).storeAdjustmentMessage(Matchers.any(AdjustmentSaleMessage.class));
        handler = new AdjustmentSaleMessageHandler(salesRepository);
    }

    private void setupMultipleProducts(){
        Collection<Sale> appleSales = getSales("Apple", 100, 10);
        when(salesRepository.getSalesForGivenProduct("Apple")).thenReturn(appleSales);
        Collection<Sale> mangoSales = getSales("Mango", 20, 10);
        when(salesRepository.getSalesForGivenProduct("Mango")).thenReturn(mangoSales);
        doNothing().when(salesRepository).storeAdjustmentMessage(Matchers.any(AdjustmentSaleMessage.class));
        handler = new AdjustmentSaleMessageHandler(salesRepository);
    }

    private Collection<Sale> getSales(String product, int price, int count) {
        Collection<Sale> sales = new ArrayList<Sale>();

        for(int i=0; i < count;i++) {
            Sale sale = new Sale(product, new BigDecimal(price));
            sales.add(sale);
        }
        return sales;
    }
}
