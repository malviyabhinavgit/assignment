package com.jpmc.assignment.dao;

import org.apache.commons.collections4.CollectionUtils;

import com.jpmc.assignment.model.AdjustmentSaleMessage;
import com.jpmc.assignment.model.Sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OrderCache {
    private final Map<String, List<AdjustmentSaleMessage>> saleAdjustmentsMap = new HashMap<String,List<AdjustmentSaleMessage>>();
    private final Map<String, List<Sale>> productSaleMap = new HashMap<String, List<Sale>>();
    List<Sale> saleRecords = new ArrayList<Sale>();

    /**
     *
     * @param product product type
     * @return returns list of sale for given product type
     */
    public List<Sale> getSalesForGivenProduct(String product) {
        if(product == null) {
            throw new IllegalArgumentException("Invalid Product received");
        }
        return productSaleMap.get(product);
    }

    /**
     *
     * @param product product type
     * @param sales list of sales for given product type
     */
    public void updateProductSaleMap(String product, List<Sale> sales) {
        if(product == null || CollectionUtils.isEmpty(sales)) {
            throw new IllegalArgumentException("Invalid product & sale combination received");
        }
        productSaleMap.put(product, sales);
    }

    /**
     *
     * @param product product type
     * @return list of adjustmentSaleMessages for given product type
     */
    public List<AdjustmentSaleMessage> getAdjustmentSaleMessageForGivenProduct(String product) {
        if(product == null) {
            throw new IllegalArgumentException("Invalid Product received");
        }
        return saleAdjustmentsMap.get(product);
    }

    /*
     *
     * @param product product type
     * @param adjustmentSaleMessages list of adjustmentSaleMessgaes for given product type
     */
    public void updateSaleAdjustmentMap(String product, List<AdjustmentSaleMessage> adjustmentSaleMessages) {
        if(product == null || CollectionUtils.isEmpty(adjustmentSaleMessages)) {
            throw new IllegalArgumentException("Invalid product & adjustment sales combination received");
        }
        saleAdjustmentsMap.put(product, adjustmentSaleMessages);
    }


    public Map<String, List<AdjustmentSaleMessage>> getSaleAdjustmentsMap() {
        return saleAdjustmentsMap;
    }

    public Map<String, List<Sale>> getProductSaleMap() {
        return productSaleMap;
    }

    public void addSaleRecords(Sale sale) {
        if(sale == null) {
            throw new IllegalArgumentException("Invalid sale received");
        }
        saleRecords.add(sale);
    }

    public List<Sale> getSaleRecords() {
        return saleRecords;
    }

    public void setSaleRecords(List<Sale> records) {
        this.saleRecords.addAll(records);
    }

    public void flush() {
        saleRecords.clear();
        productSaleMap.clear();
        saleAdjustmentsMap.clear();
    }

}
