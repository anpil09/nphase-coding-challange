package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.Optional;

import static com.nphase.constant.ConfigPropertiesKeys.DISCOUNT_PERCENT;
import static com.nphase.constant.ConfigPropertiesKeys.MIN_PRODUCT_QUANTITY_FOR_DISCOUNT;
import static java.math.MathContext.UNLIMITED;
import static java.util.stream.Collectors.groupingBy;

public class ShoppingCartService {

    private static final int DEFAULT_MIN_PRODUCT_QUANTITY_FOR_DISCOUNT = 4;
    private static final BigDecimal DEFAULT_DISCOUNT_PERCENT = BigDecimal.valueOf(10);
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final ConfigPropertiesService configPropertiesService;

    public ShoppingCartService(ConfigPropertiesService configPropertiesService) {
        this.configPropertiesService = configPropertiesService;
    }

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
                .map(priceForCategory -> quantityInCategory >= getMinProductQuantityForDiscount()
                        ? priceForCategory.multiply(getDiscountFactor())
                        : priceForCategory)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateProductPrice(Product product) {
        return product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity()));
    }

    private int getMinProductQuantityForDiscount() {
        return Optional.ofNullable(configPropertiesService.getConfigProperty(MIN_PRODUCT_QUANTITY_FOR_DISCOUNT))
                .map(Integer::valueOf)
                .orElse(DEFAULT_MIN_PRODUCT_QUANTITY_FOR_DISCOUNT);
    }

    private BigDecimal getDiscountFactor() {
        return ONE_HUNDRED.subtract(getDiscountPercent()).divide(ONE_HUNDRED, UNLIMITED);
    }

    private BigDecimal getDiscountPercent() {
        return Optional.ofNullable(configPropertiesService.getConfigProperty(DISCOUNT_PERCENT))
                .map(BigDecimal::new)
                .orElse(DEFAULT_DISCOUNT_PERCENT);
    }
}
