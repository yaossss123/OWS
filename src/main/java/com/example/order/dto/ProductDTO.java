package com.example.order.dto;

import com.example.order.entity.Product;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 产品数据传输对象
 *
 * 功能: 用于产品数据的传输和展示
 * 逻辑链: 数据接收 -> 验证转换 -> 业务处理 -> 结果返回
 * 注意事项: 需要验证产品编码的唯一性和价格、库存的合理性
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
public class ProductDTO {

    /**
     * 产品ID
     */
    private Long id;

    /**
     * 产品编码
     */
    @NotBlank(message = "产品编码不能为空")
    @Size(min = 3, max = 20, message = "产品编码长度必须在3-20个字符之间")
    private String productCode;

    /**
     * 产品名称
     */
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 200, message = "产品名称长度不能超过200个字符")
    private String name;

    /**
     * 产品描述
     */
    @Size(max = 1000, message = "产品描述长度不能超过1000个字符")
    private String description;

    /**
     * 产品分类
     */
    @Size(max = 100, message = "产品分类长度不能超过100个字符")
    private String category;

    /**
     * 单价
     */
    @NotNull(message = "单价不能为空")
    @DecimalMin(value = "0.01", message = "单价必须大于0")
    private BigDecimal unitPrice;

    /**
     * 成本价
     */
    @DecimalMin(value = "0.0", message = "成本价不能为负数")
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    @NotNull(message = "库存数量不能为空")
    private Integer stockQuantity;

    /**
     * 最小库存
     */
    @NotNull(message = "最小库存不能为空")
    private Integer minStock;

    /**
     * 单位
     */
    @Size(max = 20, message = "单位长度不能超过20个字符")
    private String unit;

    /**
     * 状态
     */
    private Product.ProductStatus status;

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
     * @param product 产品实体
     * @return 产品DTO
     */
    public static ProductDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setProductCode(product.getProductCode());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setCostPrice(product.getCostPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setMinStock(product.getMinStock());
        dto.setUnit(product.getUnit());
        dto.setStatus(product.getStatus());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        return dto;
    }

    /**
     * 转换为实体对象
     *
     * @return 产品实体
     */
    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setProductCode(this.productCode);
        product.setName(this.name);
        product.setDescription(this.description);
        product.setCategory(this.category);
        product.setUnitPrice(this.unitPrice);
        product.setCostPrice(this.costPrice);
        product.setStockQuantity(this.stockQuantity);
        product.setMinStock(this.minStock);
        product.setUnit(this.unit);
        product.setStatus(this.status);

        return product;
    }
} 