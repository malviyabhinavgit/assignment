package com.jpmc.assignment.service;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.jpmc.assignment.dao.SalesRepository;
import com.jpmc.assignment.entity.Adjustment;
import com.jpmc.assignment.entity.AdjustmentSaleMessage;
import com.jpmc.assignment.entity.Sale;


public class ReportGeneratorTest {

    private ReportGenerator reportGenerator;
    private SalesRepository salesRepository = mock(SalesRepository.class);
    private ReportWriter reportWriter = mock(ReportWriter.class);


    @Test
    public void shouldGenerateProductDetailsReport() {
        createSalesRepositoryExpectations();
        reportGenerator.generateProductDetailsReport();
        verify(salesRepository, times(1)).getAllSales();
        verify(reportWriter, times(1)).write("Product,Total Sales,TotalSaleAmount");
        verify(reportWriter, times(1)).write("Apple,1,100.00");
    }

    @Test
    public void shouldGenerateAdjustmentReport() {
        createSalesRepositoryExpectationsForAdjustments();
        reportGenerator.generateAdjustmentReport();
        verify(salesRepository, times(1)).getAllProcessedAdjustmentSaleMessages();
        verify(reportWriter, times(1)).write("Product,Adjustment,Price");
        verify(reportWriter, times(1)).write("Apple,ADD,10.00");
    }


    private void createSalesRepositoryExpectations() {
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

    private void createSalesRepositoryExpectationsForAdjustments() {
        Map<String, ConcurrentLinkedQueue<AdjustmentSaleMessage>> adjustmentSaleMap = getAdjustmentSaleMap();
        when(salesRepository.getAllProcessedAdjustmentSaleMessages()).thenReturn(adjustmentSaleMap);
        reportGenerator = new ReportGenerator(salesRepository, reportWriter);

    }
}
