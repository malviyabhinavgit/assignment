package com.jpmc.assignment.handler;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Adjustment;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;


public class AdjustmentSaleMessageHandlerTest {


    private SalesRepository salesRepository = mock(SalesRepository.class);
    private AdjustmentSaleMessageHandler handler;
 
    @Test
    public void shouldAdjustPriceInSingleProductAvailableScenario() {
        createRepositoryExpectationsWithSingleProductType();
        Sale adjustmentSale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);
        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        Collection<Sale> adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertTrue(new BigDecimal(110).doubleValue() == adjustedSale.getPrice().doubleValue()));
    }

    @Test
    public void shouldAdjustPriceInMultipleProductAvailableScenario() {
        createRepositoryExpectationsWithMultipleProductType();
        Sale sale = new Sale("Mango", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        Collection<Sale> mangoSales = salesRepository.getSalesForGivenProduct("Mango");
        Assert.assertEquals(10, mangoSales.size());
        //check if mango prices have been adjusted
        mangoSales.stream().forEach(mangoSale -> Assert.assertTrue(new BigDecimal(30).doubleValue() == mangoSale.getPrice().doubleValue()));

        Collection<Sale> appleSales = salesRepository.getSalesForGivenProduct("Apple");
        //check if apple sales are intact
        Assert.assertEquals(10, appleSales.size());
        appleSales.stream().forEach(appleSale -> Assert.assertTrue(new BigDecimal(100).doubleValue() == appleSale.getPrice().doubleValue()));

    }

    @Test
    public void shouldApplyMultipleAdjustmentsInSingleProductScenario() {
        createRepositoryExpectationsWithSingleProductType();
        Sale sale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        Collection<Sale> adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertTrue(new BigDecimal(110).doubleValue() == adjustedSale.getPrice().doubleValue()));

        adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.SUBTRACT);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertTrue(new BigDecimal(100).doubleValue() == adjustedSale.getPrice().doubleValue()));


        adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.MULTIPLY);
        handler.handle(adjustmentSaleMessage);

        verify(salesRepository, times(1)).storeAdjustmentMessage(eq(adjustmentSaleMessage));
        adjustedSales = salesRepository.getSalesForGivenProduct("Apple");
        Assert.assertEquals(10, adjustedSales.size());
        adjustedSales.stream().forEach(adjustedSale -> Assert.assertTrue(new BigDecimal(1000).doubleValue() == adjustedSale.getPrice().doubleValue()));
    }

    @Test
    public void shouldNotApplyAdjustmentWhenProductIsNotAvailable() {
        createRepositoryExpectationsWithSingleProductType();
        Sale sale = new Sale("Cucumber", new BigDecimal(0));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        handler.handle(adjustmentSaleMessage);
        verify(salesRepository, times(0)).storeAdjustmentMessage(adjustmentSaleMessage);
        Collection<Sale> sales = salesRepository.getSalesForGivenProduct("Cucumber");
        Collection<Sale> appleSales = salesRepository.getSalesForGivenProduct("Apple");
        //Apple sales are intact
        appleSales.stream().forEach(appleSale -> Assert.assertTrue(new BigDecimal(100).doubleValue() == appleSale.getPrice().doubleValue()));
        Assert.assertEquals(0, sales.size());
    }

    private void createRepositoryExpectationsWithSingleProductType() {
        Collection<Sale> sales = getSales("Apple", 100, 10);
        when(salesRepository.getSalesForGivenProduct("Apple")).thenReturn(sales);
        doNothing().when(salesRepository).storeAdjustmentMessage(Matchers.any(AdjustmentSaleMessage.class));
        handler = new AdjustmentSaleMessageHandler(salesRepository);
    }

    private void createRepositoryExpectationsWithMultipleProductType() {
        Collection<Sale> appleSales = getSales("Apple", 100, 10);
        when(salesRepository.getSalesForGivenProduct("Apple")).thenReturn(appleSales);
        Collection<Sale> mangoSales = getSales("Mango", 20, 10);
        when(salesRepository.getSalesForGivenProduct("Mango")).thenReturn(mangoSales);
        doNothing().when(salesRepository).storeAdjustmentMessage(Matchers.any(AdjustmentSaleMessage.class));
        handler = new AdjustmentSaleMessageHandler(salesRepository);
    }

    private Collection<Sale> getSales(String product, int price, int count) {
        Collection<Sale> sales = new ArrayList<Sale>();

        for (int i = 0; i < count; i++) {
            Sale sale = new Sale(product, new BigDecimal(price));
            sales.add(sale);
        }
        return sales;
    }
}
