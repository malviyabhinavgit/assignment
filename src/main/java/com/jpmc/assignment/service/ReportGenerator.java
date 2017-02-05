package com.jpmc.assignment.service;

import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;
import com.jpmc.assignment.util.Constants;

import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ReportGenerator {
    private final SalesRepository salesRepository;
    private final ReportWriter reportWriter;

    public ReportGenerator(SalesRepository salesRepository, ReportWriter reportWriter) {
        this.salesRepository = salesRepository;
        this.reportWriter = reportWriter;
    }

    private final static Logger logger = Logger.getLogger(ReportGenerator.class);


    /**
     * generates report with the details of processed products
     */
    public void generateProductDetailsReport() {
        Map<String, ConcurrentLinkedQueue<Sale>> processedSaleMap = salesRepository.getAllSales();
        reportWriter.write("Product,Total Sales,TotalSaleAmount");
        processedSaleMap.entrySet().stream().forEach(entry -> {
            reportWriter.write(entry.getKey() + "," +
                    entry.getValue().size() + "," +
                    new BigDecimal(entry.getValue().stream().filter(sale -> sale.getPrice() != null).mapToDouble(sale -> sale.getPrice().doubleValue()).sum()).setScale(Constants.PRECISION, Constants.ROUNDING_POLICY));
        });

    }

    /**
     * generates report with the details for adjustmentsalemessages
     */
    public void generateAdjustmentReport() {
        Map<String, ConcurrentLinkedQueue<AdjustmentSaleMessage>> saleAdjustmentMap = salesRepository.getAllProcessedAdjustmentSaleMessages();

        if (saleAdjustmentMap == null || saleAdjustmentMap.isEmpty()) {
            logger.info("No Adjustments made till now");
            return;
        }

        reportWriter.write("Product,Adjustment,Price");
        saleAdjustmentMap.entrySet().stream().forEach(entry -> {
            entry.getValue().forEach(adjustmentSaleMessage -> {
                reportWriter.write(entry.getKey() + "," + adjustmentSaleMessage.getAdjustment() + "," + adjustmentSaleMessage.getSale().getPrice());
            });

        });


    }
}
