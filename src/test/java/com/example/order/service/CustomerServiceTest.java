package com.example.order.service;

import com.example.order.dto.CustomerDTO;
import com.example.order.entity.Customer;
import com.example.order.repository.CustomerRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerDTO testCustomerDTO;

    @BeforeEach
    void setUp() {
        // 设置测试客户
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setCustomerCode("CUST001");
        testCustomer.setName("张三");
        testCustomer.setEmail("zhangsan@test.com");
        testCustomer.setPhone("13800138001");
        testCustomer.setAddress("北京市朝阳区");
        testCustomer.setStatus(Customer.CustomerStatus.ACTIVE);

        // 设置测试客户DTO
        testCustomerDTO = new CustomerDTO();
        testCustomerDTO.setId(1L);
        testCustomerDTO.setCustomerCode("CUST001");
        testCustomerDTO.setName("张三");
        testCustomerDTO.setEmail("zhangsan@test.com");
        testCustomerDTO.setPhone("13800138001");
        testCustomerDTO.setAddress("北京市朝阳区");
        testCustomerDTO.setStatus(Customer.CustomerStatus.ACTIVE);
    }

    @Test
    void testCreateCustomer_Success() {
        // Given
        when(customerRepository.existsByCustomerCode("CUST001")).thenReturn(false);
        when(customerRepository.existsByEmail("zhangsan@test.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        CustomerDTO result = customerService.createCustomer(testCustomerDTO);

        // Then
        assertNotNull(result);
        assertEquals("张三", result.getName());
        assertEquals("zhangsan@test.com", result.getEmail());
        assertEquals(Customer.CustomerStatus.ACTIVE, result.getStatus());
    }

    @Test
    void testCreateCustomer_DuplicateCustomerCode() {
        // Given
        when(customerRepository.existsByCustomerCode("CUST001")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            customerService.createCustomer(testCustomerDTO);
        });
    }

    @Test
    void testFindAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(Arrays.asList(testCustomer));
        when(customerRepository.findAll(pageable)).thenReturn(customerPage);

        // When
        Page<CustomerDTO> result = customerService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("张三", result.getContent().get(0).getName());
        assertEquals("zhangsan@test.com", result.getContent().get(0).getEmail());
    }

    @Test
    void testFindById_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // When
        Optional<CustomerDTO> result = customerService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("张三", result.get().getName());
        assertEquals("zhangsan@test.com", result.get().getEmail());
        assertEquals(Customer.CustomerStatus.ACTIVE, result.get().getStatus());
    }

    @Test
    void testFindById_NotFound() {
        // Given
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<CustomerDTO> result = customerService.findById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testUpdateCustomer_Success() {
        // Given
        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setName("李四");
        updateDTO.setEmail("lisi@test.com");
        updateDTO.setStatus(Customer.CustomerStatus.ACTIVE);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail("lisi@test.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // When
        CustomerDTO result = customerService.updateCustomer(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals("李四", result.getName());
        assertEquals("lisi@test.com", result.getEmail());
    }

    @Test
    void testUpdateCustomer_NotFound() {
        // Given
        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setName("李四");
        updateDTO.setEmail("lisi@test.com");

        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            customerService.updateCustomer(999L, updateDTO);
        });
    }

    @Test
    void testUpdateCustomer_DuplicateEmail() {
        // Given
        CustomerDTO updateDTO = new CustomerDTO();
        updateDTO.setName("李四");
        updateDTO.setEmail("lisi@test.com");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.existsByEmail("lisi@test.com")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            customerService.updateCustomer(1L, updateDTO);
        });
    }

    @Test
    void testDeleteCustomer_Success() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        doNothing().when(customerRepository).delete(testCustomer);

        // When & Then
        assertDoesNotThrow(() -> {
            customerService.deleteCustomer(1L);
        });
        verify(customerRepository, times(1)).delete(testCustomer);
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Given
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            customerService.deleteCustomer(999L);
        });
    }

    @Test
    void testFindByNameContaining_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(Arrays.asList(testCustomer));
        when(customerRepository.findByNameContainingIgnoreCase("张三", pageable)).thenReturn(customerPage);

        // When
        Page<CustomerDTO> result = customerService.findByNameContaining("张三", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("张三", result.getContent().get(0).getName());
    }

    @Test
    void testFindByStatus_Success() {
        // Given
        List<Customer> customers = Arrays.asList(testCustomer);
        when(customerRepository.findByStatus(Customer.CustomerStatus.ACTIVE)).thenReturn(customers);

        // When
        List<CustomerDTO> result = customerService.findByStatus(Customer.CustomerStatus.ACTIVE);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("张三", result.get(0).getName());
    }
} 