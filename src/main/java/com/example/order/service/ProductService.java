package com.example.order.service;

import com.example.order.dto.ProductDTO;
import com.example.order.entity.Product;
import com.example.order.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 产品服务类
 *
 * 功能: 提供产品相关的业务逻辑处理
 * 逻辑链: 请求接收 -> 数据验证 -> 业务处理 -> 结果返回
 * 注意事项: 需要处理产品编码的唯一性验证和库存管理
 *
 * @author Order Management Team
 * @version 0.1.0
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 创建产品
     *
     * @param productDTO 产品DTO
     * @return 创建的产品DTO
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("开始创建产品，产品编码: {}", productDTO.getProductCode());

        // 验证产品编码唯一性
        if (productRepository.existsByProductCode(productDTO.getProductCode())) {
            throw new RuntimeException("产品编码已存在");
        }

        // 验证产品名称唯一性
        if (productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("产品名称已存在");
        }

        // 转换为实体并保存
        Product product = productDTO.toEntity();
        product.setStatus(Product.ProductStatus.ACTIVE);

        Product savedProduct = productRepository.save(product);
        log.info("产品创建成功，产品ID: {}", savedProduct.getId());

        return ProductDTO.fromEntity(savedProduct);
    }

    /**
     * 根据ID查找产品
     *
     * @param id 产品ID
     * @return 产品DTO
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findById(Long id) {
        log.debug("查找产品，产品ID: {}", id);
        return productRepository.findById(id).map(ProductDTO::fromEntity);
    }

    /**
     * 根据产品编码查找产品
     *
     * @param productCode 产品编码
     * @return 产品DTO
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findByProductCode(String productCode) {
        log.debug("根据产品编码查找产品，产品编码: {}", productCode);
        return productRepository.findByProductCode(productCode).map(ProductDTO::fromEntity);
    }

    /**
     * 根据产品名称查找产品
     *
     * @param name 产品名称
     * @return 产品DTO
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findByName(String name) {
        log.debug("根据产品名称查找产品，产品名称: {}", name);
        return productRepository.findByName(name).map(ProductDTO::fromEntity);
    }

    /**
     * 更新产品信息
     *
     * @param id 产品ID
     * @param productDTO 产品DTO
     * @return 更新后的产品DTO
     */
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("开始更新产品信息，产品ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在"));

        // 检查产品编码唯一性（排除当前产品）
        if (!product.getProductCode().equals(productDTO.getProductCode()) &&
            productRepository.existsByProductCode(productDTO.getProductCode())) {
            throw new RuntimeException("产品编码已存在");
        }

        // 检查产品名称唯一性（排除当前产品）
        if (!product.getName().equals(productDTO.getName()) &&
            productRepository.existsByName(productDTO.getName())) {
            throw new RuntimeException("产品名称已存在");
        }

        // 更新产品信息
        product.setProductCode(productDTO.getProductCode());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setUnitPrice(productDTO.getUnitPrice());
        product.setCostPrice(productDTO.getCostPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setMinStock(productDTO.getMinStock());
        product.setUnit(productDTO.getUnit());
        product.setStatus(productDTO.getStatus());

        Product updatedProduct = productRepository.save(product);
        log.info("产品信息更新成功，产品ID: {}", updatedProduct.getId());

        return ProductDTO.fromEntity(updatedProduct);
    }

    /**
     * 删除产品
     *
     * @param id 产品ID
     */
    public void deleteProduct(Long id) {
        log.info("开始删除产品，产品ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在"));

        productRepository.delete(product);
        log.info("产品删除成功，产品ID: {}", id);
    }

    /**
     * 分页查询产品
     *
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        log.debug("分页查询产品，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAll(pageable).map(ProductDTO::fromEntity);
    }

    /**
     * 搜索产品
     *
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        log.debug("搜索产品，关键词: {}", keyword);
        return productRepository.findByNameContainingIgnoreCase(keyword, pageable).map(ProductDTO::fromEntity);
    }

    /**
     * 根据状态查找产品
     *
     * @param status 产品状态
     * @return 产品列表
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findByStatus(Product.ProductStatus status) {
        log.debug("根据状态查找产品，状态: {}", status);
        return productRepository.findByStatus(status).stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据分类查找产品
     *
     * @param category 产品分类
     * @return 产品列表
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findByCategory(String category) {
        log.debug("根据分类查找产品，分类: {}", category);
        return productRepository.findByCategory(category).stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 查找所有激活状态的产品
     *
     * @return 激活产品列表
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findAllActiveProducts() {
        log.debug("查找所有激活状态的产品");
        return productRepository.findAllActiveProducts().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 查找库存不足的产品
     *
     * @return 库存不足产品列表
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findLowStockProducts() {
        log.debug("查找库存不足的产品");
        return productRepository.findLowStockProducts().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 查找缺货产品
     *
     * @return 缺货产品列表
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findOutOfStockProducts() {
        log.debug("查找缺货产品");
        return productRepository.findOutOfStockProducts().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 根据价格范围查找产品
     *
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findByUnitPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.debug("根据价格范围查找产品，范围: {} - {}", minPrice, maxPrice);
        return productRepository.findByUnitPriceBetween(minPrice, maxPrice, pageable)
                .map(ProductDTO::fromEntity);
    }

    /**
     * 根据库存范围查找产品
     *
     * @param minStock 最小库存
     * @param maxStock 最大库存
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> findByStockQuantityBetween(Integer minStock, Integer maxStock, Pageable pageable) {
        log.debug("根据库存范围查找产品，范围: {} - {}", minStock, maxStock);
        return productRepository.findByStockQuantityBetween(minStock, maxStock, pageable)
                .map(ProductDTO::fromEntity);
    }

    /**
     * 检查产品编码是否存在
     *
     * @param productCode 产品编码
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByProductCode(String productCode) {
        return productRepository.existsByProductCode(productCode);
    }

    /**
     * 检查产品名称是否存在
     *
     * @param name 产品名称
     * @return 是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    /**
     * 统计各状态产品数量
     *
     * @return 状态统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByStatus() {
        log.debug("统计各状态产品数量");
        return productRepository.countByStatus();
    }

    /**
     * 统计各分类产品数量
     *
     * @return 分类统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByCategory() {
        log.debug("统计各分类产品数量");
        return productRepository.countByCategory();
    }

    /**
     * 统计库存分布
     *
     * @return 库存统计结果
     */
    @Transactional(readOnly = true)
    public List<Object[]> countByStockLevel() {
        log.debug("统计库存分布");
        return productRepository.countByStockLevel();
    }

    /**
     * 统计库存不足的产品数量
     *
     * @return 库存不足产品数量
     */
    @Transactional(readOnly = true)
    public long countLowStockProducts() {
        log.debug("统计库存不足的产品数量");
        return productRepository.findLowStockProducts().size();
    }

    /**
     * 查找需要补货的产品
     *
     * @return 需要补货的产品列表
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> findProductsNeedingRestock() {
        log.debug("查找需要补货的产品");
        return productRepository.findProductsNeedingRestock().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 更新产品库存
     *
     * @param id 产品ID
     * @param quantity 库存变化量（正数为增加，负数为减少）
     */
    public void updateStock(Long id, Integer quantity) {
        log.info("更新产品库存，产品ID: {}, 变化量: {}", id, quantity);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("产品不存在"));

        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("库存不足，无法减少");
        }

        product.setStockQuantity(newStock);
        productRepository.save(product);
        log.info("产品库存更新成功，产品ID: {}, 新库存: {}", id, newStock);
    }
} 