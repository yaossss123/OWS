package com.example.order.service;

import com.example.order.dto.OrderDTO;
import com.example.order.entity.Customer;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.entity.Product;
import com.example.order.exception.ResourceNotFoundException;
import com.example.order.exception.InsufficientStockException;
import com.example.order.exception.InsufficientStockException;
import com.example.order.repository.CustomerRepository;
import com.example.order.repository.OrderItemRepository;
import com.example.order.repository.OrderRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer testCustomer;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // 设置测试客户
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setCustomerCode("CUST001");
        testCustomer.setName("张三");
        testCustomer.setEmail("zhangsan@test.com");
        testCustomer.setStatus(Customer.CustomerStatus.ACTIVE);

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

        // 设置测试订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNumber("ORD20240101001");
        testOrder.setCustomerId(1L);
        testOrder.setCustomer(testCustomer);
        testOrder.setOrderDate(LocalDate.now());
        testOrder.setTotalAmount(new BigDecimal("11998.00"));
        testOrder.setFinalAmount(new BigDecimal("11998.00"));
        testOrder.setStatus(Order.OrderStatus.PENDING);
    }

    @Test
    void testFindAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(Arrays.asList(testOrder));
        when(orderRepository.findAll(pageable)).thenReturn(orderPage);

        // When
        Page<OrderDTO> result = orderService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("ORD20240101001", result.getContent().get(0).getOrderNumber());
    }

    @Test
    void testFindById_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Optional<OrderDTO> result = orderService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("ORD20240101001", result.get().getOrderNumber());
        assertEquals(1L, result.get().getCustomerId());
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<OrderDTO> result = orderService.findById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderDTO result = orderService.updateOrderStatus(1L, Order.OrderStatus.CONFIRMED);

        // Then
        assertNotNull(result);
        assertEquals(Order.OrderStatus.CONFIRMED, result.getStatus());
    }

    @Test
    void testUpdateOrderStatus_OrderNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            orderService.updateOrderStatus(999L, Order.OrderStatus.CONFIRMED);
        });
    }

    @Test
    void testDeleteOrder_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findByOrderId(1L)).thenReturn(new ArrayList<>());
        doNothing().when(orderItemRepository).deleteByOrderId(1L);
        doNothing().when(orderRepository).delete(testOrder);

        // When & Then
        assertDoesNotThrow(() -> {
            orderService.deleteOrder(1L);
        });
        verify(orderRepository, times(1)).delete(testOrder);
        verify(orderItemRepository, times(1)).deleteByOrderId(1L);
    }

    @Test
    void testDeleteOrder_OrderNotFound() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            orderService.deleteOrder(999L);
        });
    }
} 