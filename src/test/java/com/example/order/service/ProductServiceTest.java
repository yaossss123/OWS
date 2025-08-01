package com.example.order.service;

import com.example.order.dto.ProductDTO;
import com.example.order.entity.Product;
import com.example.order.exception.ResourceNotFoundException;
import com.example.order.exception.DuplicateResourceException;
import com.example.order.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDTO testProductDTO;

    @BeforeEach
    void setUp() {
        // 设置测试产品
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setProductCode("IPHONE15");
        testProduct.setName("iPhone 15");
        testProduct.setDescription("苹果最新手机");
        testProduct.setUnitPrice(new BigDecimal("5999.00"));
        testProduct.setStockQuantity(100);
        testProduct.setCategory("ELECTRONICS");
        testProduct.setStatus(Product.ProductStatus.ACTIVE);

        // 设置测试产品DTO
        testProductDTO = new ProductDTO();
        testProductDTO.setId(1L);
        testProductDTO.setProductCode("IPHONE15");
        testProductDTO.setName("iPhone 15");
        testProductDTO.setDescription("苹果最新手机");
        testProductDTO.setUnitPrice(new BigDecimal("5999.00"));
        testProductDTO.setStockQuantity(100);
        testProductDTO.setCategory("ELECTRONICS");
        testProductDTO.setStatus(Product.ProductStatus.ACTIVE);
    }

    @Test
    void testCreateProduct_Success() {
        // Given
        when(productRepository.existsByProductCode("IPHONE15")).thenReturn(false);
        when(productRepository.existsByName("iPhone 15")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductDTO result = productService.createProduct(testProductDTO);

        // Then
        assertNotNull(result);
        assertEquals("iPhone 15", result.getName());
        assertEquals("苹果最新手机", result.getDescription());
        assertEquals(new BigDecimal("5999.00"), result.getUnitPrice());
        assertEquals(100, result.getStockQuantity());

        verify(productRepository).existsByProductCode("IPHONE15");
        verify(productRepository).existsByName("iPhone 15");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testCreateProduct_DuplicateName() {
        // Given
        when(productRepository.existsByProductCode("IPHONE15")).thenReturn(false);
        when(productRepository.existsByName("iPhone 15")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            productService.createProduct(testProductDTO);
        });

        verify(productRepository).existsByProductCode("IPHONE15");
        verify(productRepository).existsByName("iPhone 15");
        verify(productRepository, never()).save(any());
    }

    @Test
    void testGetProducts_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct), pageable, 1);
        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // When
        Page<ProductDTO> result = productService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("iPhone 15", result.getContent().get(0).getName());

        verify(productRepository).findAll(pageable);
    }

    @Test
    void testGetProductById_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Optional<ProductDTO> result = productService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("iPhone 15", result.get().getName());
        assertEquals("苹果最新手机", result.get().getDescription());

        verify(productRepository).findById(1L);
    }

    @Test
    void testGetProductById_NotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<ProductDTO> result = productService.findById(1L);

        // Then
        assertFalse(result.isPresent());

        verify(productRepository).findById(1L);
    }

    @Test
    void testUpdateProduct_Success() {
        // Given
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("iPhone 15 Pro");
        updateDTO.setDescription("苹果最新专业版手机");
        updateDTO.setUnitPrice(new BigDecimal("6999.00"));
        updateDTO.setStockQuantity(80);
        updateDTO.setCategory("ELECTRONICS");
        updateDTO.setStatus(Product.ProductStatus.ACTIVE);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductDTO result = productService.updateProduct(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("iPhone 15 Pro", result.getName());
        assertEquals("苹果最新专业版手机", result.getDescription());

        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateProduct_NotFound() {
        // Given
        ProductDTO updateDTO = new ProductDTO();
        updateDTO.setName("iPhone 15 Pro");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(1L, updateDTO);
        });

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productRepository).delete(testProduct);

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(testProduct);
    }

    @Test
    void testDeleteProduct_NotFound() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(1L);
        });

        verify(productRepository).findById(1L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void testSearchProducts_Success() {
        // Given
        String keyword = "iPhone";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(Arrays.asList(testProduct), pageable, 1);
        when(productRepository.findByNameContainingIgnoreCase(keyword, pageable))
            .thenReturn(productPage);

        // When
        Page<ProductDTO> result = productService.searchProducts(keyword, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("iPhone 15", result.getContent().get(0).getName());

        verify(productRepository).findByNameContainingIgnoreCase(keyword, pageable);
    }

    @Test
    void testFindByCategory_Success() {
        // Given
        String category = "ELECTRONICS";
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategory(category)).thenReturn(products);

        // When
        List<ProductDTO> result = productService.findByCategory(category);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ELECTRONICS", result.get(0).getCategory());

        verify(productRepository).findByCategory(category);
    }

    @Test
    void testUpdateStock_Success() {
        // Given
        Integer quantity = 10;
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.updateStock(1L, quantity);

        // Then
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void testUpdateStock_ProductNotFound() {
        // Given
        Integer quantity = 10;
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            productService.updateStock(1L, quantity);
        });

        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }
} 