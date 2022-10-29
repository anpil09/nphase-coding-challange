package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ProductCategory;
import com.nphase.entity.ShoppingCart;

import javax.naming.OperationNotSupportedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;

public class ShoppingCartService {

    private static final int MIN_PRODUCT_QUANTITY_FOR_DISCOUNT = 4;
    private static final BigDecimal DISCOUNT_FACTOR = BigDecimal.valueOf(0.9);

    public BigDecimal calculateTotalPrice(ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .collect(groupingBy(Product::getCategory))
                .values()
                .stream()
                .map(this::calculatePriceForCategory)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculatePriceForCategory(List<Product> products) {
        int quantityInCategory = products.stream()
                .map(Product::getQuantity)
                .mapToInt(Integer::intValue)
                .sum();

        return products.stream()
                .map(this::calculateProductPrice)
                .reduce(BigDecimal::add)
                .map(priceForCategory -> quantityInCategory >= MIN_PRODUCT_QUANTITY_FOR_DISCOUNT
                        ? priceForCategory.multiply(DISCOUNT_FACTOR)
                        : priceForCategory)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateProductPrice(Product product) {
        return product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
    }
}
