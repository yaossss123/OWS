package com.example.order.integration;

import com.example.order.dto.CreateOrderRequest;
import com.example.order.dto.CustomerDTO;
import com.example.order.dto.LoginRequest;
import com.example.order.dto.ProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单管理系统端到端集成测试
 * 
 * 测试完整的业务流程：
 * 1. 用户登录获取令牌
 * 2. 创建客户
 * 3. 创建产品
 * 4. 创建订单
 * 5. 查询订单
 * 6. 更新订单状态
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class OrderManagementIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 登录获取访问令牌
        accessToken = loginAndGetToken();
    }

    @Test
    void testCompleteOrderWorkflow() throws Exception {
        // 1. 创建客户
        Long customerId = createCustomer();
        
        // 2. 创建产品
        Long productId = createProduct();
        
        // 3. 创建订单
        Long orderId = createOrder(customerId, productId);
        
        // 4. 查询订单
        getOrder(orderId);
        
        // 5. 更新订单状态
        updateOrderStatus(orderId);
        
        // 6. 查询订单列表
        getOrders();
    }

    private String loginAndGetToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 从响应中提取访问令牌
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    private Long createCustomer() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setName("测试客户");
        customerDTO.setEmail("test@example.com");
        customerDTO.setPhone("13800138000");
        customerDTO.setAddress("测试地址");
        customerDTO.setStatus("ACTIVE");

        String response = mockMvc.perform(post("/api/v1/customers")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("测试客户"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductCode("TEST001");
        productDTO.setName("测试产品");
        productDTO.setDescription("测试产品描述");
        productDTO.setUnitPrice(new BigDecimal("99.99"));
        productDTO.setStockQuantity(100);
        productDTO.setCategory("TEST");
        productDTO.setStatus(com.example.order.entity.Product.ProductStatus.ACTIVE);

        String response = mockMvc.perform(post("/api/v1/products")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("测试产品"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createOrder(Long customerId, Long productId) throws Exception {
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setShippingAddress("测试收货地址");
        orderRequest.setNotes("测试订单备注");
        orderRequest.setItems(Arrays.asList(
            new CreateOrderRequest.OrderItemRequest(productId, 2, new BigDecimal("99.99"))
        ));

        String response = mockMvc.perform(post("/api/v1/orders")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.orderNumber").exists())
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private void getOrder(Long orderId) throws Exception {
        mockMvc.perform(get("/api/v1/orders/" + orderId)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.orderNumber").exists())
                .andExpect(jsonPath("$.customerId").exists())
                .andExpect(jsonPath("$.totalAmount").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    private void updateOrderStatus(Long orderId) throws Exception {
        mockMvc.perform(patch("/api/v1/orders/" + orderId + "/status")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    private void getOrders() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", "Bearer " + accessToken)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists());
    }

    @Test
    void testAuthenticationFlow() throws Exception {
        // 测试未认证访问
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());

        // 测试无效令牌
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", "Bearer invalid_token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAuthorizationFlow() throws Exception {
        // 使用普通用户令牌
        String userToken = loginAsUser();
        
        // 普通用户应该能够查看订单
        mockMvc.perform(get("/api/v1/orders")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());

        // 普通用户不应该能够删除订单
        mockMvc.perform(delete("/api/v1/orders/1")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private String loginAsUser() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("123456");

        String response = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("accessToken").asText();
    }

    @Test
    void testErrorHandling() throws Exception {
        // 测试无效的请求数据
        mockMvc.perform(post("/api/v1/orders")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"invalid\":\"data\"}"))
                .andExpect(status().isBadRequest());

        // 测试访问不存在的资源
        mockMvc.perform(get("/api/v1/orders/999999")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
} 