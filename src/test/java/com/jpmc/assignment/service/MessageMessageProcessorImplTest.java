package com.jpmc.assignment.service;

import com.jpmc.assignment.dao.OrderCache;
import com.jpmc.assignment.handler.AdjustmentSaleMessageHandler;
import com.jpmc.assignment.handler.MessageHandler;
import com.jpmc.assignment.handler.SimpleSaleMessageHandler;
import com.jpmc.assignment.model.*;
import com.jpmc.assignment.service.MessageMessageProcessorImpl;
import com.jpmc.assignment.service.ReportGenerator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class MessageMessageProcessorImplTest {

    private MessageMessageProcessorImpl messageProcessorImpl;

    private Map<MessageType, MessageHandler> messageHandlers;

    private OrderCache orderCache;

    private ReportGenerator reportGenerator;

    @Before
    public void setup(){
        messageProcessorImpl = new MessageMessageProcessorImpl();
        messageHandlers = new HashMap<MessageType, MessageHandler>();
        AdjustmentSaleMessageHandler adjustmentSaleMessageHandler = new AdjustmentSaleMessageHandler();
        orderCache = new OrderCache();
        reportGenerator = new ReportGenerator();
        reportGenerator.setOrderCache(orderCache);
        messageProcessorImpl.setReportGenerator(reportGenerator);
        adjustmentSaleMessageHandler.setOrderCache(orderCache);
        SimpleSaleMessageHandler simpleSaleMessageHandler = new SimpleSaleMessageHandler();
        simpleSaleMessageHandler.setOrderCache(orderCache);
        messageHandlers.put(MessageType.ADJUSTMENT_SALE_MESSAGE, adjustmentSaleMessageHandler);
        messageHandlers.put(MessageType.SIMPLE_SALE_MESSAGE, simpleSaleMessageHandler);
        messageProcessorImpl.setMessageHandlers(messageHandlers);
        messageProcessorImpl.setLogCutOffCount(10);
        messageProcessorImpl.setPauseCutOffCount(50);
    }

    @Test
    public void testProcessMessageWithCombinationOfFiftySimpleAdjustmentSaleMessages() {
        Sale sale = new Sale("Apple", new BigDecimal(100));
        Sale adjustmentSale = new Sale("Apple", new BigDecimal(100));
        IncomingSaleMessage incomingSaleMessage = new SimpleSaleMessage(sale,1);
        IncomingSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        IncomingSaleMessage subtractAdjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.SUBTRACT);
        for(int i = 0; i<45; i++) {
            messageProcessorImpl.process(incomingSaleMessage);
        }
        IncomingSaleMessage recurrentSaleMessage = new SimpleSaleMessage(sale, 4);
        messageProcessorImpl.process(recurrentSaleMessage);
        for(int i =0;i<2;i++) {
            messageProcessorImpl.process(adjustmentSaleMessage);
            messageProcessorImpl.process(subtractAdjustmentSaleMessage);
        }

        for(int i = 0; i<10; i++) {
            messageProcessorImpl.process(incomingSaleMessage);
        }

        orderCache.flush();

    }

    @Test
    public void shouldProcessTheSaleMessage() {
        Sale sale = new Sale("Apple", new BigDecimal(100));
        IncomingSaleMessage incomingSaleMessage = new SimpleSaleMessage(sale, 1);

        for(int i = 0; i<10; i++) {
            messageProcessorImpl.process(incomingSaleMessage);
        }

        Assert.assertEquals(10, orderCache.getSaleRecords().size());
        Assert.assertEquals(10, orderCache.getProductSaleMap().get(sale.getProduct()).size());
        orderCache.flush();
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwIllegalArgumentExceptionWhenProvidedWithNullMessage() {
        messageProcessorImpl.process(null);
    }

    @Test
    public void sendFirstMessageAsAdjustmentMessageShouldNotBeAbleToProcess() {
        Sale adjustmentSale = new Sale("Apple", new BigDecimal(100));
        IncomingSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        messageProcessorImpl.process(adjustmentSaleMessage);
        Assert.assertEquals(0, orderCache.getSaleRecords().size());
        orderCache.flush();
    }
}
