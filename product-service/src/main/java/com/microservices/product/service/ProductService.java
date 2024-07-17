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
        log.info("Creating product with ISBN: {}", productRequest.getIsbn());
        Optional<Product> existingProductOpt = productRepository.findByIsbn(productRequest.getIsbn());

        if (existingProductOpt.isPresent()) {
            Product existingProduct = existingProductOpt.get();
            existingProduct.setQuantity(existingProduct.getQuantity() + 1);
            productRepository.save(existingProduct);
            log.info("Product {} quantity increased to {}", existingProduct.getId(), existingProduct.getQuantity());
        } else {
            Product product = Product.builder()
                    .name(productRequest.getName())
                    .description(productRequest.getDescription())
                    .price(productRequest.getPrice())
                    .imageLink(productRequest.getImageLink())
                    .author(productRequest.getAuthor())
                    .genre(productRequest.getGenre())
                    .publishedYear(productRequest.getPublishedYear())
                    .isbn(productRequest.getIsbn())
                    .quantity(1) // Initialize quantity
                    .build();

            productRepository.save(product);
            log.info("Product {} is saved", product.getId());
        }

        rabbitTemplate.convertAndSend(exchangeName, RabbitMQConfig.PRODUCT_ROUTING_KEY, productRequest.getIsbn());
        log.info("Product creation event published to RabbitMQ: {}", productRequest.getIsbn());
    }

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponse).toList();
    }

    public void deleteProduct(String isbn) {
        Optional<Product> productOpt = productRepository.findByIsbn(isbn);
        if (productOpt.isPresent()) {
            productRepository.deleteById(Long.valueOf(productOpt.get().getId()));
            log.info("Product with ISBN {} is deleted", isbn);
        } else {
            log.warn("Product with ISBN {} not found", isbn);
        }
    }

    public ProductResponse updateProduct(String isbn, ProductRequest productRequest) {
        Optional<Product> optionalProduct = productRepository.findByIsbn(isbn);

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

            log.info("Product with ISBN {} is updated", isbn);
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
                .quantity(product.getQuantity()) // Include quantity
                .build();
    }
}
