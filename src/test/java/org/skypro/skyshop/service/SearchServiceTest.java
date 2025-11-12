package org.skypro.skyshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.*;
import org.skypro.skyshop.model.search.SearchResult;
import org.skypro.skyshop.model.search.Searchable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StorageService storageService;

    @InjectMocks
    private SearchService searchService;

    private Product testProduct;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        testProduct = new SimpleProduct("тестовый товар", 100, UUID.randomUUID());
        testArticle = new Article("тестовая статья", "описание тестовой статьи", UUID.randomUUID());
    }

    // Сценарий 1: Поиск в случае отсутствия объектов в StorageService
    @Test
    void search_WhenStorageIsEmpty_ShouldReturnEmptyCollection() {
        when(storageService.getAllSearchables()).thenReturn(Collections.emptyList());

        Collection<SearchResult> result = searchService.search("любой запрос");

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Результат поиска должен быть пустым при пустом хранилище");
        verify(storageService, times(1)).getAllSearchables();
    }

    // Сценарий 2: Объекты есть, но нет подходящих по запросу
    @Test
    void search_WhenNoMatchingObjects_ShouldReturnEmptyCollection() {
        List<Searchable> searchables = Arrays.asList(testProduct, testArticle);
        when(storageService.getAllSearchables()).thenReturn(searchables);

        Collection<SearchResult> result = searchService.search("несуществующий запрос");

        assertNotNull(result, "Результат не должен быть null");
        assertTrue(result.isEmpty(), "Результат поиска должен быть пустым при отсутствии совпадений");
        verify(storageService, times(1)).getAllSearchables();
    }

    // Сценарий 3: Есть подходящий продукт
    @Test
    void search_WhenMatchingProductExists_ShouldReturnSearchResult() {
        List<Searchable> searchables = Arrays.asList(testProduct, testArticle);
        when(storageService.getAllSearchables()).thenReturn(searchables);

        Collection<SearchResult> result = searchService.search("тестовый");

        assertEquals(1, result.size(), "Должен найтись один подходящий объект");
        SearchResult searchResult = result.iterator().next();
        assertEquals(testProduct.getId().toString(), searchResult.getId(), "ID должен совпадать с тестовым продуктом");
        assertEquals(testProduct.getSearchTerm(), searchResult.getSearchTerm(), "Search term должен совпадать");
    }

    // Сценарий 4: Поиск по частичному совпадению
    @Test
    void search_WhenPartialMatch_ShouldReturnResults() {
        List<Searchable> searchables = Collections.singletonList(testProduct);
        when(storageService.getAllSearchables()).thenReturn(searchables);

        Collection<SearchResult> result = searchService.search("тест");

        assertEquals(1, result.size(), "Должен найти объект по частичному совпадению");
        SearchResult searchResult = result.iterator().next();
        assertEquals(testProduct.getId().toString(), searchResult.getId());
    }
}
