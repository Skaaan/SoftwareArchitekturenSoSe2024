package com.microservices.common;

public class StockCheckResponse {
    private boolean inStock;
    private String isbn;




    public StockCheckResponse() {
    }

    public StockCheckResponse(boolean inStock) {
        this.inStock = inStock;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }
}
