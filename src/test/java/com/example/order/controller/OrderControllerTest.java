package com.example.order.controller;

import com.example.order.dto.OrderDTO;
import com.example.order.dto.CreateOrderRequest;
import com.example.order.entity.Customer;
import com.example.order.entity.Order;
import com.example.order.entity.Product;
import com.example.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDTO testOrderDTO;
    private CreateOrderRequest createOrderRequest;

    @BeforeEach
    void setUp() {
        // 设置测试订单DTO
        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setOrderNumber("ORD20240101001");
        testOrderDTO.setCustomerId(1L);
        testOrderDTO.setTotalAmount(new BigDecimal("11998.00"));
        testOrderDTO.setStatus("PENDING");
        testOrderDTO.setShippingAddress("北京市朝阳区xxx街道");
        testOrderDTO.setNotes("请尽快发货");

        // 设置创建订单请求
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setCustomerId(1L);
        createOrderRequest.setShippingAddress("北京市朝阳区xxx街道");
        createOrderRequest.setNotes("请尽快发货");
        createOrderRequest.setItems(Arrays.asList(
            new CreateOrderRequest.OrderItemRequest(1L, 2, new BigDecimal("5999.00"))
        ));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateOrder_Success() throws Exception {
        // Given
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD20240101001"))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.totalAmount").value(11998.00))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(orderService).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void testCreateOrder_InvalidRequest() throws Exception {
        // Given
        CreateOrderRequest invalidRequest = new CreateOrderRequest();
        // 不设置必要字段

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(orderService, never()).createOrder(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetOrders_Success() throws Exception {
        // Given
        Page<OrderDTO> orderPage = new PageImpl<>(Arrays.asList(testOrderDTO), PageRequest.of(0, 10), 1);
        when(orderService.getOrders(any())).thenReturn(orderPage);

        // When & Then
        mockMvc.perform(get("/api/v1/orders")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD20240101001"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(orderService).getOrders(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetOrderById_Success() throws Exception {
        // Given
        when(orderService.getOrderById(1L)).thenReturn(testOrderDTO);

        // When & Then
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderNumber").value("ORD20240101001"))
                .andExpect(jsonPath("$.customerId").value(1));

        verify(orderService).getOrderById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetOrderById_NotFound() throws Exception {
        // Given
        when(orderService.getOrderById(999L)).thenThrow(new RuntimeException("订单不存在"));

        // When & Then
        mockMvc.perform(get("/api/v1/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderService).getOrderById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testUpdateOrderStatus_Success() throws Exception {
        // Given
        OrderDTO updatedOrderDTO = new OrderDTO();
        updatedOrderDTO.setId(1L);
        updatedOrderDTO.setStatus("CONFIRMED");
        when(orderService.updateOrderStatus(1L, "CONFIRMED")).thenReturn(updatedOrderDTO);

        // When & Then
        mockMvc.perform(patch("/api/v1/orders/1/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(orderService).updateOrderStatus(1L, "CONFIRMED");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteOrder_Success() throws Exception {
        // Given
        doNothing().when(orderService).deleteOrder(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isNoContent());

        verify(orderService).deleteOrder(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void testDeleteOrder_Forbidden() throws Exception {
        // Given - 普通用户尝试删除订单

        // When & Then
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isForbidden());

        verify(orderService, never()).deleteOrder(any());
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // Given - 未认证用户

        // When & Then
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());

        verify(orderService, never()).getOrders(any());
    }
} 