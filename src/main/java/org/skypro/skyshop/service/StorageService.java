package org.skypro.skyshop.service;

import org.skypro.skyshop.model.article.Article;
import org.skypro.skyshop.model.product.DiscountedProduct;
import org.skypro.skyshop.model.product.FixPriceProduct;
import org.skypro.skyshop.model.product.Product;
import org.skypro.skyshop.model.product.SimpleProduct;
import org.skypro.skyshop.model.search.Searchable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StorageService {
    private final Map<UUID, Product> productStorage;
    private final Map<UUID, Article> articleStorage;

    private StorageService() {
        this.productStorage = new TreeMap<>();
        this.articleStorage = new TreeMap<>();
        addTestData();
    }

    private void addTestData() {
        Article article1 = new Article("товар", "статья о товаре", UUID.randomUUID());
        Article article2 = new Article("товарчик", "рассказ о товарчике", UUID.randomUUID());
        Article article3 = new Article("товаришка", "повесть о товаришке товар товар", UUID.randomUUID());
        Article article4 = new Article("товаришка", "повесть о товаришке товарик товарчонок товарото", UUID.randomUUID());
        Article article5 = new Article("товарчик", "еще один рассказ о товарчике", UUID.randomUUID());

        Product fish = new SimpleProduct("рыба", 150, UUID.randomUUID());
        Product cheese = new SimpleProduct("сыр", 300, UUID.randomUUID());
        Product sausage = new SimpleProduct("колбаса", 200, UUID.randomUUID());
        Product bacon = new SimpleProduct("бекон", 350, UUID.randomUUID());
        Product rottenFish = new DiscountedProduct("тухлая рыба", 150, 30, UUID.randomUUID());
        Product rottenCheese = new DiscountedProduct("тухлый сыр", 300, 20, UUID.randomUUID());
        Product rottenSausage = new DiscountedProduct("тухлая колбаса", 200, 60, UUID.randomUUID());
        Product rottenBacon = new DiscountedProduct("тухлый бекон", 350, 50, UUID.randomUUID());
        Product milk = new FixPriceProduct("молоко", UUID.randomUUID());
        Product honey = new FixPriceProduct("мед", UUID.randomUUID());
        Product butter = new FixPriceProduct("масло", UUID.randomUUID());
        Product bread = new FixPriceProduct("хлеб", UUID.randomUUID());

        articleStorage.put(article1.getId(), article1);
        articleStorage.put(article2.getId(), article2);
        articleStorage.put(article3.getId(), article3);
        articleStorage.put(article4.getId(), article4);
        articleStorage.put(article5.getId(), article5);

        productStorage.put(fish.getId(), fish);
        productStorage.put(cheese.getId(), cheese);
        productStorage.put(sausage.getId(), sausage);
        productStorage.put(bacon.getId(), bacon);
        productStorage.put(rottenFish.getId(), rottenFish);
        productStorage.put(rottenCheese.getId(), rottenCheese);
        productStorage.put(rottenSausage.getId(), rottenSausage);
        productStorage.put(rottenBacon.getId(), rottenBacon);
        productStorage.put(milk.getId(), milk);
        productStorage.put(honey.getId(), honey);
        productStorage.put(butter.getId(), butter);
        productStorage.put(bread.getId(), bread);
    }

    public Collection<Product> getAllProducts() {
        return new ArrayList<>(productStorage.values());
    }

    public Collection<Article> getAllArticles() {
        return new ArrayList<>(articleStorage.values());
    }

    public Collection<Searchable> getAllSearchables() {
        Collection<Searchable> searchables = new ArrayList<>();
        searchables.addAll(productStorage.values());
        searchables.addAll(articleStorage.values());
        return searchables;

    }

    public Optional<Product> getProductById(UUID id) {
        return Optional.ofNullable(productStorage.get(id));
    }
}
