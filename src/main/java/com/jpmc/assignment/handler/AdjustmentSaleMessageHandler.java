package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Adjustment;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.IncomingSaleMessage;
import com.jpmc.assignment.entity.Sale;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Collection;


public class AdjustmentSaleMessageHandler implements MessageHandler {

    private final SalesRepository salesRepository;

    public AdjustmentSaleMessageHandler(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }


    private final static Logger logger = Logger.getLogger(AdjustmentSaleMessageHandler.class);


    @Override
    public void handle(IncomingSaleMessage incomingSaleMessage) {
        if (incomingSaleMessage == null || !(incomingSaleMessage instanceof AdjustmentSaleMessage)) {
            throw new IllegalArgumentException("Invalid Adjustment Sales Message received");
        }

        AdjustmentSaleMessage adjustmentSaleMessage = (AdjustmentSaleMessage) incomingSaleMessage;
        Collection<Sale> sales = salesRepository.getSalesForGivenProduct(incomingSaleMessage.getSale().getProduct());
        if (sales == null || sales.isEmpty()) {
            logger.info("No sale exists of given product to adjust as off now");
            return;
        }

        adjustSales(adjustmentSaleMessage, sales);
        salesRepository.storeAdjustmentMessage(adjustmentSaleMessage);
    }

    private void adjustSales(AdjustmentSaleMessage message, Collection<Sale> sales) {
        final Adjustment adjustment = message.getAdjustment();
        final BigDecimal adjustAmount = message.getSale().getPrice();
        sales.parallelStream().forEach(sale -> adjustPrice(adjustment, adjustAmount, sale));


    }

    private void adjustPrice(Adjustment adjustment, BigDecimal adjustAmount, Sale sale) {
        BigDecimal price = sale.getPrice();
        switch (adjustment) {
            case ADD:
                price = price.add(adjustAmount);
                break;
            case SUBTRACT:
                price = price.subtract(adjustAmount);
                break;
            case MULTIPLY:
                price = price.multiply(adjustAmount);
                break;
            default:
                logger.info("Invalid adjustment");
        }

        sale.setPrice(price);
    }


}
