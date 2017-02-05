package com.jpmc.assignment.service;

import com.jpmc.assignment.entity.*;
import com.jpmc.assignment.handler.AdjustmentSaleMessageHandler;
import com.jpmc.assignment.handler.MessageHandler;
import com.jpmc.assignment.handler.SimpleSaleMessageHandler;
import com.jpmc.assignment.service.MessageProcessorImpl;
import com.jpmc.assignment.service.ReportGenerator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class MessageProcessorImplTest {

    private MessageProcessorImpl messageProcessorImpl;
    private SimpleSaleMessageHandler simpleSaleMessageHandler = mock(SimpleSaleMessageHandler.class);
    private AdjustmentSaleMessageHandler adjustmentSaleMessageHandler = mock(AdjustmentSaleMessageHandler.class);
    private final Map<MessageType, MessageHandler> messageHandlers = mock(Map.class);
    private final ReportGenerator reportGenerator = mock(ReportGenerator.class);

    @Test
    public void processMessageShouldProcessTheSimpleSaleMessage() {
        setup();
        Sale sale = new Sale("Apple", new BigDecimal(100));
        IncomingSaleMessage incomingSaleMessage = new SimpleSaleMessage(sale, 1);

        for(int i = 0; i<10; i++) {
            messageProcessorImpl.process(incomingSaleMessage);
        }

        verify(messageHandlers,times(10)).get(MessageType.SIMPLE_SALE_MESSAGE);
        verify(simpleSaleMessageHandler, times(10)).handle(eq(incomingSaleMessage));
        verify(reportGenerator, times(1)).generateProductDetailsReport();
        verify(reportGenerator, times(0)).generateAdjustmentReport();
    }

    @Test
    public void processMessageShouldProcessTheSimpleAdjustmentMessage() {
        setup();
        Sale simpleSale = new Sale("Apple", new BigDecimal(10));
        IncomingSaleMessage incomingSaleMessage = new AdjustmentSaleMessage(simpleSale, Adjustment.ADD);
        messageProcessorImpl.process(incomingSaleMessage);

        verify(messageHandlers,times(1)).get(MessageType.ADJUSTMENT_SALE_MESSAGE);
        verify(adjustmentSaleMessageHandler, times(1)).handle(eq(incomingSaleMessage));
        verify(reportGenerator, times(0)).generateProductDetailsReport();
        verify(reportGenerator, times(0)).generateAdjustmentReport();


    }

    @Test
    public void processMessageShouldProcessBothSimpleAndAdjustmentMessage() {
        setup();
        Sale sale = new Sale("Apple", new BigDecimal(100));
        IncomingSaleMessage incomingSaleMessage = new SimpleSaleMessage(sale, 1);

        for(int i = 0; i<45; i++) {
            messageProcessorImpl.process(incomingSaleMessage);
        }

        verify(messageHandlers,times(45)).get(MessageType.SIMPLE_SALE_MESSAGE);
        verify(simpleSaleMessageHandler, times(45)).handle(eq(incomingSaleMessage));
        verify(reportGenerator, times(4)).generateProductDetailsReport();
        verify(reportGenerator, times(0)).generateAdjustmentReport();

        Sale simpleSale = new Sale("Apple", new BigDecimal(10));
        incomingSaleMessage = new AdjustmentSaleMessage(simpleSale, Adjustment.ADD);
        messageProcessorImpl.process(incomingSaleMessage);

        verify(messageHandlers,times(1)).get(MessageType.ADJUSTMENT_SALE_MESSAGE);
        verify(adjustmentSaleMessageHandler, times(1)).handle(eq(incomingSaleMessage));


        incomingSaleMessage = new SimpleSaleMessage(sale, 1);
        for(int i = 0; i<5; i++) {
            messageProcessorImpl.process(incomingSaleMessage);
        }

        verify(messageHandlers,times(50)).get(MessageType.SIMPLE_SALE_MESSAGE);
        verify(simpleSaleMessageHandler, times(5)).handle(eq(incomingSaleMessage));
        verify(reportGenerator, times(5)).generateProductDetailsReport();
        verify(reportGenerator, times(1)).generateAdjustmentReport();
    }

    private void setup() {
        doNothing().when(reportGenerator).generateAdjustmentReport();
        doNothing().when(reportGenerator).generateProductDetailsReport();
        when(messageHandlers.get(MessageType.SIMPLE_SALE_MESSAGE)).thenReturn(simpleSaleMessageHandler);
        when(messageHandlers.get(MessageType.ADJUSTMENT_SALE_MESSAGE)).thenReturn(adjustmentSaleMessageHandler);
        doNothing().when(simpleSaleMessageHandler).handle(Matchers.any(IncomingSaleMessage.class));
        doNothing().when(adjustmentSaleMessageHandler).handle(Matchers.any(IncomingSaleMessage.class));
        messageProcessorImpl = new MessageProcessorImpl(messageHandlers,reportGenerator,10,50);
    }

}
