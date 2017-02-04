package com.jpmc.assignment.service;

import com.jpmc.assignment.dao.OrderCache;
import com.jpmc.assignment.model.AdjustmentSaleMessage;
import com.jpmc.assignment.model.Sale;

import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ReportGenerator {
    private OrderCache orderCache;

    private final static Logger logger = Logger.getLogger("ReportGenerator.class");
    /**
     * generates report with the details of processed products
     */
    public void generateProductDetailsReport() {
        Map<String, List<Sale>> processedSaleMap = orderCache.getProductSaleMap();
        Set<String> keySet = processedSaleMap.keySet();
        for(String key : keySet) {
            List<Sale> sales = processedSaleMap.get(key);
            BigDecimal totalValue = BigDecimal.ZERO;
            for(Sale sale : sales) {
                totalValue = totalValue.add(sale.getPrice());
            }
            logger.info("For Product : "+key+", Total number of sales: "+sales.size()+" and total value: "+totalValue);
        }
    }

    /**
     * generates report with the details for adjustmentsalemessages
     */
    public void generateAdjustmentReport() {
        Map<String, List<AdjustmentSaleMessage>> saleAdjustmentMap= orderCache.getSaleAdjustmentsMap();

        if(MapUtils.isEmpty(saleAdjustmentMap)) {
            System.out.println("No Adjustments made till now");
            return;
        }

        Set<String> keySet = saleAdjustmentMap.keySet();

        for(String product : keySet) {
            List<AdjustmentSaleMessage> adjustmentSaleMessages = saleAdjustmentMap.get(product);
            logger.info(adjustmentSaleMessages.toString());
        }

    }

    public OrderCache getOrderCache() {
        return orderCache;
    }

    public void setOrderCache(OrderCache orderCache) {
        this.orderCache = orderCache;
    }
}
