package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Sale;
import com.jpmc.assignment.entity.SimpleSaleMessage;
import com.jpmc.assignment.handler.SimpleSaleMessageHandler;

import org.junit.Test;
import org.mockito.Matchers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;


public class SimpleSaleMessageHandlerTest {

    private SimpleSaleMessageHandler handler;
    private SalesRepository salesRepository = mock(SalesRepository.class);


    @Test
    public void handleMethodShouldBeAbleToHandleSimpleSaleMessage() {
        setup();
        Sale sale = new Sale("Apple", new BigDecimal(10));
        SimpleSaleMessage simpleSaleMessage = new SimpleSaleMessage(sale, 10);
        handler.handle(simpleSaleMessage);

        verify(salesRepository, times(10)).addSaleRecord(sale);
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleMethodShouldThrowIllegalArgumentExceptionIfMessageIsInvalid() {
        setup();
        handler.handle(null);
    }


    private void setup() {
        doNothing().when(salesRepository).addSaleRecord(Matchers.any(Sale.class));
        handler = new SimpleSaleMessageHandler(salesRepository);
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
