package com.microservices.product.service;


import com.microservices.product.dto.ProductRequest;
import com.microservices.product.dto.ProductResponse;
import com.microservices.product.model.Order;
import com.microservices.product.model.Product;
import com.microservices.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


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
                .skuCode(productRequest.getSkuCode())
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .imageLink(productRequest.getImageLink())
                .build();

        productRepository.save(product);
        log.info("Product {} is saved", product.getId());

        rabbitTemplate.convertAndSend(exchangeName, "product.routing.key", product.getSkuCode());
        log.info("Product creation event published to RabbitMQ: {}", product.getSkuCode());
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
            product.setSkuCode(productRequest.getSkuCode());
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product.setImageLink(productRequest.getImageLink());
            productRepository.save(product);

            log.info("Product {} is updated", product.getId());
            return mapToProductResponse(product);
        } else {
            throw new RuntimeException("Product not found");
        }
    }

    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse productResponse = ProductResponse.builder()
                .id(product.getId())
                .skuCode(product.getSkuCode())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageLink(product.getImageLink())
                .build();

        log.info("Mapped product to response: {}", productResponse);
        return productResponse;
    }

    public void sendOrderMessage(Order order) {
        rabbitTemplate.convertAndSend(exchangeName, "order.routing.key", order);
        log.info("Order event published to RabbitMQ: {}", order);
    }
}