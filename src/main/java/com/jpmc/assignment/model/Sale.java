package com.jpmc.assignment.model;

import java.math.BigDecimal;


public class Sale {



    private final String product;
    private BigDecimal price;

    public Sale(String product, BigDecimal price) {
        this.product = product;
        this.price = price;

    }
    public String getProduct() {
        return product;
    }



    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) { this.price = price; }



    public int hashCode() {
        return product.hashCode() + price.hashCode();
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Sale) {
            Sale anotherSale = (Sale)anObject;
            if(!anotherSale.getProduct().equals(this.product)) {
                return false;
            }
            return anotherSale.getPrice().compareTo(this.price)==0;

        }
        return false;
    }
}
