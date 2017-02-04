package com.jpmc.assignment.handler;

import com.jpmc.assignment.dao.OrderCache;
import com.jpmc.assignment.model.Adjustment;
import com.jpmc.assignment.model.AdjustmentSaleMessage;
import com.jpmc.assignment.model.IncomingSaleMessage;
import com.jpmc.assignment.model.Sale;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class AdjustmentSaleMessageHandler implements MessageHandler {

    private OrderCache orderCache;

    private final static Logger logger = Logger.getLogger("AdjustmentSaleMessageHandler.class");

    /**
     *
     * @param incomingSaleMessage  sale message to be processed
     */
    public void handle(IncomingSaleMessage incomingSaleMessage) {
        if(incomingSaleMessage == null) {
            throw new IllegalArgumentException("Invalid Adjustment Sales Message received");
        }

        AdjustmentSaleMessage adjustmentSaleMessage = (AdjustmentSaleMessage) incomingSaleMessage;
        List<Sale> sales = orderCache.getSalesForGivenProduct(incomingSaleMessage.getSale().getProduct());
        if(CollectionUtils.isEmpty(sales)) {
            logger.info("No sale exists of given product to adjust as off now");
            return;
        }

        adjustSales(adjustmentSaleMessage, sales);
    }

    private void adjustSales(AdjustmentSaleMessage message, List<Sale> sales) {
        List<Sale> adjustedSales = new ArrayList<Sale>();
        Adjustment adjustment = message.getAdjustment();
        BigDecimal adjustAmount = message.getSale().getPrice();

        for(Sale sale : sales) {
            mapSaleAdjustments(sale, message);
            adjustPrice(adjustment, adjustAmount, sale);
            adjustedSales.add(sale);
        }
         orderCache.updateProductSaleMap(message.getSale().getProduct(),adjustedSales);
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
            default: logger.info("Invalid adjustment");
        }
        sale.setPrice(price);
    }

    private void mapSaleAdjustments(Sale sale, AdjustmentSaleMessage message) {
        List<AdjustmentSaleMessage> adjustmentSaleMessages = orderCache.getAdjustmentSaleMessageForGivenProduct(sale.getProduct());
        if(CollectionUtils.isEmpty(adjustmentSaleMessages)) {
            adjustmentSaleMessages = new ArrayList<AdjustmentSaleMessage>();
        }
        adjustmentSaleMessages.add(message);
        orderCache.updateSaleAdjustmentMap(sale.getProduct(), adjustmentSaleMessages);
    }

    public OrderCache getOrderCache() {
        return orderCache;
    }

    public void setOrderCache(OrderCache orderCache) {
        this.orderCache = orderCache;
    }
}
