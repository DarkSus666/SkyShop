package org.skypro.skyshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.basket.BasketItem;
import org.skypro.skyshop.model.basket.ProductBasket;
import org.skypro.skyshop.model.basket.UserBasket;
import org.skypro.skyshop.model.exeptions.NoSuchProductException;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BasketServiceTest {

    @Mock
    private ProductBasket productBasket;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private BasketService basketService;

    private UUID existingProductId;
    private UUID nonExistingProductId;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        existingProductId = UUID.randomUUID();
        nonExistingProductId = UUID.randomUUID();
        testProduct = new SimpleProduct("Тестовый продукт", 100, existingProductId);
    }

    // Сценарий 1: Добавление несуществующего товара в корзину приводит к выбросу исключения
    @Test
    void addToBasket_WhenProductDoesNotExist_ShouldThrowNoSuchProductException() {
        when(storageService.getProductById(nonExistingProductId)).thenReturn(Optional.empty());

        NoSuchProductException exception = assertThrows(
                NoSuchProductException.class,
                () -> basketService.addToBasket(nonExistingProductId)
        );

        assertEquals("Продукт не найден: " + nonExistingProductId, exception.getMessage());

        verify(productBasket, never()).addToBasket(any(UUID.class));
        verify(storageService, times(1)).getProductById(nonExistingProductId);
    }

    // Сценарий 2: Добавление существующего товара вызывает метод addProduct у мока ProductBasket
    @Test
    void addToBasket_WhenProductExists_ShouldCallAddToBasket() {
        when(storageService.getProductById(existingProductId)).thenReturn(Optional.of(testProduct));

        basketService.addToBasket(existingProductId);

        verify(storageService, times(1)).getProductById(existingProductId);
        verify(productBasket, times(1)).addToBasket(existingProductId);
    }

    // Сценарий 3: Метод getUserBasket возвращает пустую корзину, если ProductBasket пуст
    @Test
    void getUserBasket_WhenProductBasketIsEmpty_ShouldReturnEmptyUserBasket() {
        when(productBasket.getBasket()).thenReturn(Collections.emptyMap());

        UserBasket result = basketService.getUserBasket();

        assertNotNull(result, "UserBasket не должен быть null");
        assertNotNull(result.getItems(), "Список items не должен быть null");
        assertTrue(result.getItems().isEmpty(), "Список items должен быть пустым");

        verify(productBasket, times(1)).getBasket();
        // StorageService не должен вызываться, так как корзина пуста
        verify(storageService, never()).getProductById(any(UUID.class));
    }

    // Сценарий 4: Метод getUserBasket возвращает подходящую корзину, если в ProductBasket есть товары
    @Test
    void getUserBasket_WhenProductBasketHasItems_ShouldReturnUserBasketWithItems() {
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        Product product1 = new SimpleProduct("Продукт 1", 100, productId1);
        Product product2 = new SimpleProduct("Продукт 2", 200, productId2);

        Map<UUID, Integer> basketItems = new HashMap<>();
        basketItems.put(productId1, 2);
        basketItems.put(productId2, 1);

        when(productBasket.getBasket()).thenReturn(basketItems);
        when(storageService.getProductById(productId1)).thenReturn(Optional.of(product1));
        when(storageService.getProductById(productId2)).thenReturn(Optional.of(product2));

        UserBasket result = basketService.getUserBasket();

        assertNotNull(result, "UserBasket не должен быть null");
        assertNotNull(result.getItems(), "Список items не должен быть null");
        assertEquals(2, result.getItems().size(), "Должно быть 2 элемента в корзине");

        List<BasketItem> items = result.getItems();

        BasketItem item1 = findBasketItemById(items, productId1);
        assertNotNull(item1, "Должен найти продукт 1");
        assertEquals(product1, item1.getProduct());
        assertEquals(2, item1.getQuantity());

        BasketItem item2 = findBasketItemById(items, productId2);
        assertNotNull(item2, "Должен найти продукт 2");
        assertEquals(product2, item2.getProduct());
        assertEquals(1, item2.getQuantity());

        verify(productBasket, times(1)).getBasket();
        verify(storageService, times(1)).getProductById(productId1);
        verify(storageService, times(1)).getProductById(productId2);
    }

    private BasketItem findBasketItemById(List<BasketItem> items, UUID productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);
    }
}