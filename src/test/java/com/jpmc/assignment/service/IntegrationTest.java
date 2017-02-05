package com.jpmc.assignment.service;

import com.jpmc.assignment.dao.SalesCache;
import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.*;
import com.jpmc.assignment.handler.AdjustmentSaleMessageHandler;
import com.jpmc.assignment.handler.MessageHandler;
import com.jpmc.assignment.handler.SimpleSaleMessageHandler;
import com.jpmc.assignment.service.ConsoleReportWriter;
import com.jpmc.assignment.service.MessageProcessorImpl;
import com.jpmc.assignment.service.ReportGenerator;
import com.jpmc.assignment.service.ReportWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;



public class IntegrationTest {

    private MessageProcessorImpl messageProcessorImpl;
    private Map<MessageType, MessageHandler> messageHandlers;
    private ReportGenerator reportGenerator;
    private SalesRepository salesRepository;

    @Before
    public void setup(){

        messageHandlers = new HashMap<MessageType, MessageHandler>();
        salesRepository = new SalesCache();
        AdjustmentSaleMessageHandler adjustmentSaleMessageHandler = new AdjustmentSaleMessageHandler(salesRepository);
        SimpleSaleMessageHandler simpleSaleMessageHandler = new SimpleSaleMessageHandler(salesRepository);
        messageHandlers.put(MessageType.SIMPLE_SALE_MESSAGE, simpleSaleMessageHandler);
        messageHandlers.put(MessageType.ADJUSTMENT_SALE_MESSAGE, adjustmentSaleMessageHandler);
        ReportWriter reportWriter = new ConsoleReportWriter();
        reportGenerator = new ReportGenerator(salesRepository, reportWriter);
        messageProcessorImpl = new MessageProcessorImpl(messageHandlers, reportGenerator,10,50);

    }

    @Test
    public void testProcessMessageWithCombinationOfSimpleAndAdjustmentSaleMessages() {
        //Testing SimpleSaleMessage
        IncomingSaleMessage incomingSaleMessage = null;
        for(int i = 0; i<45; i++) {
            Sale sale = new Sale("Apple", new BigDecimal(100));
            incomingSaleMessage = new SimpleSaleMessage(sale,1);
            messageProcessorImpl.process(incomingSaleMessage);
        }
        Assert.assertEquals(45, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertEquals(new BigDecimal(100), sale.getPrice()));

        //Testing AdjustmentSaleMessage
        Sale adjustmentSale = new Sale("Apple", new BigDecimal(10));
        IncomingSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        messageProcessorImpl.process(adjustmentSaleMessage);
        Assert.assertEquals(45, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertEquals(new BigDecimal(110), sale.getPrice()));
        Assert.assertEquals(adjustmentSaleMessage, salesRepository.getAllProcessedAdjustmentSaleMessages().get(adjustmentSaleMessage.getSale().getProduct()).peek());

        //Testing Subtract Adjustment message
        IncomingSaleMessage subtractAdjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.SUBTRACT);
        messageProcessorImpl.process(subtractAdjustmentSaleMessage);
        Assert.assertEquals(45, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(subtractAdjustmentSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertEquals(new BigDecimal(100), sale.getPrice()));
        Assert.assertEquals(true, salesRepository.getAllProcessedAdjustmentSaleMessages().get(adjustmentSaleMessage.getSale().getProduct()).contains(adjustmentSaleMessage));
        Assert.assertEquals(true, salesRepository.getAllProcessedAdjustmentSaleMessages().get(subtractAdjustmentSaleMessage.getSale().getProduct()).contains(subtractAdjustmentSaleMessage));

        //Adding one Diff Product to test SimpleSaleMessage with more than one occurance
        Sale mangoSale = new Sale("Mango", new BigDecimal(50));
        incomingSaleMessage = new SimpleSaleMessage(mangoSale,100);
        messageProcessorImpl.process(incomingSaleMessage);
        Assert.assertEquals(100, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertEquals(new BigDecimal(50), sale.getPrice()));

        //Testing adjustment on diff product
        adjustmentSale = new Sale("Mango", new BigDecimal(10));
        adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        messageProcessorImpl.process(adjustmentSaleMessage);

        Assert.assertEquals(100, salesRepository.getAllSales().get(adjustmentSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(adjustmentSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertEquals(new BigDecimal(60), sale.getPrice()));
        Assert.assertEquals(adjustmentSaleMessage, salesRepository.getAllProcessedAdjustmentSaleMessages().get(adjustmentSaleMessage.getSale().getProduct()).peek());
    }
}
