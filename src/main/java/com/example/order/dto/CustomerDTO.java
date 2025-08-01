package com.example.order.dto;

import com.example.order.entity.Customer;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户数据传输对象
 *
 * 功能: 用于客户数据的传输和展示
 * 逻辑链: 数据接收 -> 验证转换 -> 业务处理 -> 结果返回
 * 注意事项: 需要验证客户编码的唯一性和信用额度限制
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
public class CustomerDTO {

    /**
     * 客户ID
     */
    private Long id;

    /**
     * 客户编码
     */
    @NotBlank(message = "客户编码不能为空")
    @Size(min = 3, max = 20, message = "客户编码长度必须在3-20个字符之间")
    private String customerCode;

    /**
     * 客户名称
     */
    @NotBlank(message = "客户名称不能为空")
    @Size(max = 200, message = "客户名称长度不能超过200个字符")
    private String name;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 电话
     */
    @Size(max = 20, message = "电话长度不能超过20个字符")
    private String phone;

    /**
     * 地址
     */
    @Size(max = 500, message = "地址长度不能超过500个字符")
    private String address;

    /**
     * 联系人
     */
    @Size(max = 100, message = "联系人长度不能超过100个字符")
    private String contactPerson;

    /**
     * 联系人电话
     */
    @Size(max = 20, message = "联系人电话长度不能超过20个字符")
    private String contactPhone;

    /**
     * 状态
     */
    private Customer.CustomerStatus status;

    /**
     * 信用额度
     */
    private BigDecimal creditLimit;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 从实体转换为DTO
     *
     * @param customer 客户实体
     * @return 客户DTO
     */
    public static CustomerDTO fromEntity(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setName(customer.getName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setContactPerson(customer.getContactPerson());
        dto.setContactPhone(customer.getContactPhone());
        dto.setStatus(customer.getStatus());
        dto.setCreditLimit(customer.getCreditLimit());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());

        return dto;
    }

    /**
     * 转换为实体对象
     *
     * @return 客户实体
     */
    public Customer toEntity() {
        Customer customer = new Customer();
        customer.setId(this.id);
        customer.setCustomerCode(this.customerCode);
        customer.setName(this.name);
        customer.setEmail(this.email);
        customer.setPhone(this.phone);
        customer.setAddress(this.address);
        customer.setContactPerson(this.contactPerson);
        customer.setContactPhone(this.contactPhone);
        customer.setStatus(this.status);
        customer.setCreditLimit(this.creditLimit);

        return customer;
    }
} 