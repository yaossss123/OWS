package com.example.order.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 库存变动实体类
 * 
 * 功能: 记录库存变动历史
 * 逻辑链: 变动触发 -> 记录创建 -> 数据持久化 -> 库存更新
 * 注意事项: 变动数量需要数值验证，关联类型需要枚举验证
 * 
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "inventory_transactions", indexes = {
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_transaction_type", columnList = "transaction_type"),
    @Index(name = "idx_reference_type", columnList = "reference_type"),
    @Index(name = "idx_reference_id", columnList = "reference_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class InventoryTransaction extends BaseEntity {

    /**
     * 变动ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 产品ID
     */
    @NotNull(message = "产品ID不能为空")
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 产品信息（关联查询）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    /**
     * 变动类型
     */
    @NotNull(message = "变动类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    /**
     * 变动数量
     */
    @NotNull(message = "变动数量不能为空")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * 变动前数量
     */
    @NotNull(message = "变动前数量不能为空")
    @Column(name = "before_quantity", nullable = false)
    private Integer beforeQuantity;

    /**
     * 变动后数量
     */
    @NotNull(message = "变动后数量不能为空")
    @Column(name = "after_quantity", nullable = false)
    private Integer afterQuantity;

    /**
     * 关联类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 20)
    private ReferenceType referenceType;

    /**
     * 关联ID
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * 备注
     */
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    /**
     * 变动类型枚举
     */
    public enum TransactionType {
        IN("入库"),
        OUT("出库"),
        ADJUSTMENT("调整");

        private final String description;

        TransactionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 关联类型枚举
     */
    public enum ReferenceType {
        ORDER("订单"),
        PURCHASE("采购"),
        ADJUSTMENT("调整"),
        RETURN("退货");

        private final String description;

        ReferenceType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 创建入库记录
     * 
     * @param productId 产品ID
     * @param quantity 入库数量
     * @param beforeQuantity 入库前数量
     * @param referenceType 关联类型
     * @param referenceId 关联ID
     * @param notes 备注
     * @return 库存变动记录
     */
    public static InventoryTransaction createInTransaction(Long productId, Integer quantity, 
                                                        Integer beforeQuantity, ReferenceType referenceType, 
                                                        Long referenceId, String notes) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(productId);
        transaction.setTransactionType(TransactionType.IN);
        transaction.setQuantity(quantity);
        transaction.setBeforeQuantity(beforeQuantity);
        transaction.setAfterQuantity(beforeQuantity + quantity);
        transaction.setReferenceType(referenceType);
        transaction.setReferenceId(referenceId);
        transaction.setNotes(notes);
        return transaction;
    }

    /**
     * 创建出库记录
     * 
     * @param productId 产品ID
     * @param quantity 出库数量
     * @param beforeQuantity 出库前数量
     * @param referenceType 关联类型
     * @param referenceId 关联ID
     * @param notes 备注
     * @return 库存变动记录
     */
    public static InventoryTransaction createOutTransaction(Long productId, Integer quantity, 
                                                         Integer beforeQuantity, ReferenceType referenceType, 
                                                         Long referenceId, String notes) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(productId);
        transaction.setTransactionType(TransactionType.OUT);
        transaction.setQuantity(quantity);
        transaction.setBeforeQuantity(beforeQuantity);
        transaction.setAfterQuantity(beforeQuantity - quantity);
        transaction.setReferenceType(referenceType);
        transaction.setReferenceId(referenceId);
        transaction.setNotes(notes);
        return transaction;
    }

    /**
     * 创建调整记录
     * 
     * @param productId 产品ID
     * @param quantity 调整数量（正数为增加，负数为减少）
     * @param beforeQuantity 调整前数量
     * @param notes 备注
     * @return 库存变动记录
     */
    public static InventoryTransaction createAdjustmentTransaction(Long productId, Integer quantity, 
                                                                Integer beforeQuantity, String notes) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setProductId(productId);
        transaction.setTransactionType(TransactionType.ADJUSTMENT);
        transaction.setQuantity(Math.abs(quantity));
        transaction.setBeforeQuantity(beforeQuantity);
        transaction.setAfterQuantity(beforeQuantity + quantity);
        transaction.setReferenceType(ReferenceType.ADJUSTMENT);
        transaction.setNotes(notes);
        return transaction;
    }
} 