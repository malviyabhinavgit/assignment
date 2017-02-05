package com.jpmc.assignment.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jpmc.assignment.dao.SalesCache;
import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Adjustment;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.IncomingSaleMessage;
import com.jpmc.assignment.entity.MessageType;
import com.jpmc.assignment.entity.Sale;
import com.jpmc.assignment.entity.SimpleSaleMessage;
import com.jpmc.assignment.exception.MessageProcessorException;
import com.jpmc.assignment.handler.AdjustmentSaleMessageHandler;
import com.jpmc.assignment.handler.MessageHandler;
import com.jpmc.assignment.handler.SimpleSaleMessageHandler;


public class IntegrationTest {

    private MessageProcessorImpl messageProcessorImpl;
    private Map<MessageType, MessageHandler> messageHandlers;
    private ReportGenerator reportGenerator;
    private SalesRepository salesRepository;

    @Before
    public void setup() {

        messageHandlers = new HashMap<>();
        salesRepository = new SalesCache();
        AdjustmentSaleMessageHandler adjustmentSaleMessageHandler = new AdjustmentSaleMessageHandler(salesRepository);
        SimpleSaleMessageHandler simpleSaleMessageHandler = new SimpleSaleMessageHandler(salesRepository);
        messageHandlers.put(MessageType.SIMPLE_SALE_MESSAGE, simpleSaleMessageHandler);
        messageHandlers.put(MessageType.ADJUSTMENT_SALE_MESSAGE, adjustmentSaleMessageHandler);
        ReportWriter reportWriter = new ConsoleReportWriter();
        reportGenerator = new ReportGenerator(salesRepository, reportWriter);
        messageProcessorImpl = new MessageProcessorImpl(messageHandlers, reportGenerator, 10, 50);

    }

    @Test
    public void shouldProcessCombinationOfSimpleAndAdjustmentSaleMessagesAndGenerateReport() throws MessageProcessorException{
        //Testing SimpleSaleMessage
        IncomingSaleMessage incomingSaleMessage = null;
        for (int i = 0; i < 45; i++) {
            Sale sale = new Sale("Apple", new BigDecimal(100));
            incomingSaleMessage = new SimpleSaleMessage(sale);
            messageProcessorImpl.process(incomingSaleMessage);
        }
        Assert.assertEquals(45, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertTrue(new BigDecimal(100).doubleValue() == sale.getPrice().doubleValue()));

        //Testing AdjustmentSaleMessage
        Sale adjustmentSale = new Sale("Apple", new BigDecimal(10));
        IncomingSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        messageProcessorImpl.process(adjustmentSaleMessage);
        Assert.assertEquals(45, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertTrue(new BigDecimal(110).doubleValue() == sale.getPrice().doubleValue()));
        Assert.assertEquals(adjustmentSaleMessage, salesRepository.getAllProcessedAdjustmentSaleMessages().get(adjustmentSaleMessage.getSale().getProduct()).peek());

        //Testing Subtract Adjustment message
        IncomingSaleMessage subtractAdjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.SUBTRACT);
        messageProcessorImpl.process(subtractAdjustmentSaleMessage);
        Assert.assertEquals(45, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(subtractAdjustmentSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertTrue(new BigDecimal(100).doubleValue() == sale.getPrice().doubleValue()));
        Assert.assertEquals(true, salesRepository.getAllProcessedAdjustmentSaleMessages().get(adjustmentSaleMessage.getSale().getProduct()).contains(adjustmentSaleMessage));
        Assert.assertEquals(true, salesRepository.getAllProcessedAdjustmentSaleMessages().get(subtractAdjustmentSaleMessage.getSale().getProduct()).contains(subtractAdjustmentSaleMessage));

        //Adding one different product to test SimpleSaleMessage with more than one occurrence
        Sale mangoSale = new Sale("Mango", new BigDecimal(50));
        incomingSaleMessage = new SimpleSaleMessage(mangoSale, 100);
        messageProcessorImpl.process(incomingSaleMessage);
        Assert.assertEquals(100, salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(incomingSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertTrue(new BigDecimal(50).doubleValue() == sale.getPrice().doubleValue()));

        //Testing adjustment on different product
        adjustmentSale = new Sale("Mango", new BigDecimal(10));
        adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.ADD);
        messageProcessorImpl.process(adjustmentSaleMessage);

        Assert.assertEquals(100, salesRepository.getAllSales().get(adjustmentSaleMessage.getSale().getProduct()).size());
        salesRepository.getAllSales().get(adjustmentSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertTrue(new BigDecimal(60).doubleValue() == sale.getPrice().doubleValue()));

        //Sending adjustment message with multiplier
        adjustmentSale = new Sale("Mango", new BigDecimal(1.3));
        adjustmentSaleMessage = new AdjustmentSaleMessage(adjustmentSale, Adjustment.MULTIPLY);
        messageProcessorImpl.process(adjustmentSaleMessage);
        salesRepository.getAllSales().get(adjustmentSaleMessage.getSale().getProduct()).stream().forEach(sale -> Assert.assertTrue(new BigDecimal(78).doubleValue() == sale.getPrice().doubleValue()));

    }
}
