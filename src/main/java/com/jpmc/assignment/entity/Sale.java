package com.jpmc.assignment.entity;

import java.math.BigDecimal;

import com.jpmc.assignment.util.Constants;


public class Sale {
    private final String product;
    private BigDecimal price;

    public Sale(String product, BigDecimal price) {
        this.product = product;
        this.price = price.setScale(Constants.PRECISION, Constants.ROUNDING_POLICY);

    }

    public String getProduct() {
        return product;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    @Override
    public int hashCode() {
        return product.hashCode() + price.hashCode();
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Sale) {
            Sale anotherSale = (Sale) anObject;
            if (!anotherSale.getProduct().equals(this.product)) {
                return false;
            }
            return anotherSale.getPrice().compareTo(this.price) == 0;

        }
        return false;
    }
}
