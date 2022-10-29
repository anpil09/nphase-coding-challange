package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;

import java.math.BigDecimal;

public class ShoppingCartService {

    private static final int MIN_PRODUCT_QUANTITY_FOR_DISCOUNT = 4;
    private static final BigDecimal DISCOUNT_FACTOR = BigDecimal.valueOf(0.9);

    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(this::calculateProductPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateProductPrice(Product product) {
        BigDecimal priceWithoutDiscount = product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));

        if (product.getQuantity() >= MIN_PRODUCT_QUANTITY_FOR_DISCOUNT) {
            return priceWithoutDiscount.multiply(DISCOUNT_FACTOR);
        }

        return priceWithoutDiscount;
    }
}
