package com.nphase.service;


import com.nphase.entity.Product;
import com.nphase.entity.ProductCategory;
import com.nphase.entity.ShoppingCart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class ShoppingCartServiceTest {

    private final ConfigPropertiesService configPropertiesService = new ConfigPropertiesService();
    private final ShoppingCartService service = new ShoppingCartService(configPropertiesService);

    @Test
    public void calculatesPrice()  {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", ProductCategory.DRINKS, BigDecimal.valueOf(5.0), 2),
                new Product("Coffee", ProductCategory.DRINKS, BigDecimal.valueOf(6.5), 1)
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        Assertions.assertEquals(result, BigDecimal.valueOf(16.5));
    }

    @Test
    public void calculatePriceWithDiscount() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", ProductCategory.DRINKS, BigDecimal.valueOf(5.0), 5),
                new Product("Cheese", ProductCategory.FOOD, BigDecimal.valueOf(3.5), 3)
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        assertStrippingTrailingZeros(result.stripTrailingZeros(), BigDecimal.valueOf(33.0).stripTrailingZeros());
    }

    @Test
    public void calculatePriceWithDiscountForCategory() {
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", ProductCategory.DRINKS, BigDecimal.valueOf(5.3), 2),
                new Product("Coffee", ProductCategory.DRINKS, BigDecimal.valueOf(3.5), 2),
                new Product("Cheese", ProductCategory.FOOD, BigDecimal.valueOf(8), 2)
        ));

        BigDecimal result = service.calculateTotalPrice(cart);

        assertStrippingTrailingZeros(result.stripTrailingZeros(), BigDecimal.valueOf(31.84).stripTrailingZeros());
    }

    private void assertStrippingTrailingZeros(BigDecimal expected, BigDecimal actual) {
        Assertions.assertEquals(expected.stripTrailingZeros(), actual.stripTrailingZeros());
    }

}