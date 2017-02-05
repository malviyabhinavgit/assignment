package com.jpmc.assignment.handler;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Sale;
import com.jpmc.assignment.entity.SimpleSaleMessage;


public class SimpleSaleMessageHandlerTest {

    private SimpleSaleMessageHandler handler;
    private SalesRepository salesRepository = mock(SalesRepository.class);


    @Test
    public void shouldStoreSaleRecord() {
        Sale sale = new Sale("Apple", new BigDecimal(10));
        SimpleSaleMessage simpleSaleMessage = new SimpleSaleMessage(sale, 10);
        handler.handle(simpleSaleMessage);

        verify(salesRepository, times(10)).addSaleRecord(sale);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenInvalidMessageIsGiven() {
        handler.handle(null);
    }

    @Before
    public void setup() {
        doNothing().when(salesRepository).addSaleRecord(Matchers.any(Sale.class));
        handler = new SimpleSaleMessageHandler(salesRepository);
    }

   

}
