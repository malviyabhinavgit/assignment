package com.jpmc.assignment.service;


import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Adjustment;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;
import com.jpmc.assignment.service.ReportGenerator;
import com.jpmc.assignment.service.ReportWriter;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.mockito.Mockito.*;


public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private SalesRepository salesRepository = mock(SalesRepository.class);
    private ReportWriter reportWriter = mock(ReportWriter.class);


    @Test
    public void testGenerateProductDetailsReport() {
        setUpProcessedSales();
        reportGenerator.generateProductDetailsReport();
        verify(salesRepository, times(1)).getAllSales();
        verify(reportWriter, times(1)).write("Product,Total Sales,TotalSaleAmount");
        verify(reportWriter, times(1)).write("Apple,1,100.00");
    }

    @Test
    public void testGenerateAdjustmentReport() {
        setUpAdjustmentSaleMessage();
        reportGenerator.generateAdjustmentReport();
        verify(salesRepository, times(1)).getAllProcessedAdjustmentSaleMessages();
        verify(reportWriter, times(1)).write("Product,Adjustment,Price");
        verify(reportWriter, times(1)).write("Apple,ADD,10.00");
    }


    private void setUpProcessedSales() {
        Map<String, ConcurrentLinkedQueue<Sale>> processedSaleMap = getProcessedSaleMap();
        when(salesRepository.getAllSales()).thenReturn(processedSaleMap);
        reportGenerator = new ReportGenerator(salesRepository, reportWriter);

    }

    private Map<String, ConcurrentLinkedQueue<Sale>> getProcessedSaleMap() {
        Map<String, ConcurrentLinkedQueue<Sale>> processedSaleMap = new ConcurrentHashMap<>();
        Sale sale = new Sale("Apple", new BigDecimal(100));
        processedSaleMap.putIfAbsent("Apple", new ConcurrentLinkedQueue<>(Collections.singletonList(sale)));
        return processedSaleMap;
    }

    private Map<String, ConcurrentLinkedQueue<AdjustmentSaleMessage>> getAdjustmentSaleMap() {
        Map<String, ConcurrentLinkedQueue<AdjustmentSaleMessage>> adjustmentSaleMap = new ConcurrentHashMap<>();
        Sale sale = new Sale("Apple", new BigDecimal(10));
        AdjustmentSaleMessage adjustmentSaleMessage = new AdjustmentSaleMessage(sale, Adjustment.ADD);
        adjustmentSaleMap.putIfAbsent("Apple", new ConcurrentLinkedQueue<>(Collections.singletonList(adjustmentSaleMessage)));
        return adjustmentSaleMap;
    }

    private void setUpAdjustmentSaleMessage() {
        Map<String, ConcurrentLinkedQueue<AdjustmentSaleMessage>> adjustmentSaleMap = getAdjustmentSaleMap();
        when(salesRepository.getAllProcessedAdjustmentSaleMessages()).thenReturn(adjustmentSaleMap);
        reportGenerator = new ReportGenerator(salesRepository, reportWriter);

    }
}
