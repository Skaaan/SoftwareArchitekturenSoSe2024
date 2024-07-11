package com.microservices.product.service;

import com.microservices.product.config.RabbitMQConfig;
import com.microservices.product.dto.ProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    public void createProduct(ProductRequest productRequest) {
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .imageLink(productRequest.getImageLink())
                .author(productRequest.getAuthor())
                .genre(productRequest.getGenre())
                .publishedYear(productRequest.getPublishedYear())
                .isbn(productRequest.getIsbn())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());

        rabbitTemplate.convertAndSend(exchangeName, RabbitMQConfig.PRODUCT_ROUTING_KEY, product.getName());
        log.info("Product creation event published to RabbitMQ: {}", product.getName());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(Long.valueOf(id));
        log.info("Product {} is deleted", id);
    }

    public ProductResponse updateProduct(String id, ProductRequest productRequest) {
        Optional<Product> optionalProduct = productRepository.findById(Long.valueOf(id));

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setImageLink(productRequest.getImageLink());
            product.setAuthor(productRequest.getAuthor());
            product.setGenre(productRequest.getGenre());
            product.setPublishedYear(productRequest.getPublishedYear());
            product.setIsbn(productRequest.getIsbn());
            productRepository.save(product);

            log.info("Product {} is updated", product.getId());
            return mapToProductResponse(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageLink(product.getImageLink())
                .author(product.getAuthor())
                .genre(product.getGenre())
                .publishedYear(product.getPublishedYear())
                .isbn(product.getIsbn())
                .build();
    }
}
