package com.example.order.controller;

import com.example.order.dto.ProductDTO;
import com.example.order.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 产品管理控制器
 * 
 * @author Order Management System
 * @version 1.0
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Tag(name = "产品管理", description = "产品相关API接口")
public class ProductController {

    private final ProductService productService;

    /**
     * 创建产品
     * 
     * @param productDTO 产品信息
     * @return 创建的产品信息
     */
    @PostMapping
    @Operation(summary = "创建产品", description = "创建新产品")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        
        log.info("开始创建产品，产品编码: {}", productDTO.getProductCode());
        
        try {
            ProductDTO createdProduct = productService.createProduct(productDTO);
            log.info("产品创建成功，产品ID: {}", createdProduct.getId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (Exception e) {
            log.error("产品创建失败，错误信息: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据ID查询产品
     * 
     * @param id 产品ID
     * @return 产品信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询产品", description = "根据产品ID获取产品详细信息")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "产品ID") @PathVariable @NotNull Long id) {
        
        log.info("查询产品，产品ID: {}", id);
        
        return productService.findById(id)
                .map(product -> {
                    log.info("产品查询成功，产品ID: {}", id);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    log.warn("产品不存在，产品ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 根据产品编码查询产品
     * 
     * @param productCode 产品编码
     * @return 产品信息
     */
    @GetMapping("/code/{productCode}")
    @Operation(summary = "根据产品编码查询产品", description = "根据产品编码获取产品详细信息")
    public ResponseEntity<ProductDTO> getProductByCode(
            @Parameter(description = "产品编码") @PathVariable @NotNull String productCode) {
        
        log.info("根据产品编码查询产品，产品编码: {}", productCode);
        
        return productService.findByProductCode(productCode)
                .map(product -> {
                    log.info("产品查询成功，产品编码: {}", productCode);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    log.warn("产品不存在，产品编码: {}", productCode);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 根据产品名称查询产品
     * 
     * @param name 产品名称
     * @return 产品信息
     */
    @GetMapping("/name/{name}")
    @Operation(summary = "根据产品名称查询产品", description = "根据产品名称获取产品详细信息")
    public ResponseEntity<ProductDTO> getProductByName(
            @Parameter(description = "产品名称") @PathVariable @NotNull String name) {
        
        log.info("根据产品名称查询产品，产品名称: {}", name);
        
        return productService.findByName(name)
                .map(product -> {
                    log.info("产品查询成功，产品名称: {}", name);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    log.warn("产品不存在，产品名称: {}", name);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * 更新产品信息
     * 
     * @param id 产品ID
     * @param productDTO 更新的产品信息
     * @return 更新后的产品信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新产品", description = "更新产品基本信息")
    public ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "产品ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        
        log.info("更新产品，产品ID: {}", id);
        
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
            log.info("产品更新成功，产品ID: {}", id);
            
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            log.error("产品更新失败，产品ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除产品
     * 
     * @param id 产品ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除产品", description = "删除指定产品")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "产品ID") @PathVariable @NotNull Long id) {
        
        log.info("删除产品，产品ID: {}", id);
        
        try {
            productService.deleteProduct(id);
            log.info("产品删除成功，产品ID: {}", id);
            
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("产品删除失败，产品ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 分页查询所有产品
     * 
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @GetMapping
    @Operation(summary = "分页查询产品", description = "分页查询所有产品")
    public ResponseEntity<Page<ProductDTO>> getAllProducts(
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("分页查询产品，页码: {}, 大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ProductDTO> products = productService.findAll(pageable);
        log.info("产品查询完成，总记录数: {}", products.getTotalElements());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 搜索产品
     * 
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @GetMapping("/search")
    @Operation(summary = "搜索产品", description = "根据关键词搜索产品")
    public ResponseEntity<Page<ProductDTO>> searchProducts(
            @Parameter(description = "搜索关键词") @RequestParam @NotNull String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("搜索产品，关键词: {}", keyword);
        
        Page<ProductDTO> products = productService.searchProducts(keyword, pageable);
        log.info("产品搜索完成，关键词: {}, 总记录数: {}", keyword, products.getTotalElements());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 根据状态查询产品
     * 
     * @param status 产品状态
     * @return 产品列表
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "根据状态查询产品", description = "查询指定状态的所有产品")
    public ResponseEntity<List<ProductDTO>> getProductsByStatus(
            @Parameter(description = "产品状态") @PathVariable @NotNull String status) {
        
        log.info("根据状态查询产品，状态: {}", status);
        
        List<ProductDTO> products = productService.findByStatus(
                com.example.order.entity.Product.ProductStatus.valueOf(status));
        log.info("状态产品查询完成，状态: {}, 产品数量: {}", status, products.size());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 根据分类查询产品
     * 
     * @param category 产品分类
     * @return 产品列表
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "根据分类查询产品", description = "查询指定分类的所有产品")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @Parameter(description = "产品分类") @PathVariable @NotNull String category) {
        
        log.info("根据分类查询产品，分类: {}", category);
        
        List<ProductDTO> products = productService.findByCategory(category);
        log.info("分类产品查询完成，分类: {}, 产品数量: {}", category, products.size());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 查询所有活跃产品
     * 
     * @return 活跃产品列表
     */
    @GetMapping("/active")
    @Operation(summary = "查询活跃产品", description = "查询所有状态为活跃的产品")
    public ResponseEntity<List<ProductDTO>> getActiveProducts() {
        
        log.info("查询活跃产品");
        
        List<ProductDTO> products = productService.findAllActiveProducts();
        log.info("活跃产品查询完成，产品数量: {}", products.size());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 查询库存不足的产品
     * 
     * @return 库存不足产品列表
     */
    @GetMapping("/low-stock")
    @Operation(summary = "查询库存不足产品", description = "查询库存低于最小库存的产品")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        
        log.info("查询库存不足产品");
        
        List<ProductDTO> products = productService.findLowStockProducts();
        log.info("库存不足产品查询完成，产品数量: {}", products.size());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 查询缺货产品
     * 
     * @return 缺货产品列表
     */
    @GetMapping("/out-of-stock")
    @Operation(summary = "查询缺货产品", description = "查询库存为0的产品")
    public ResponseEntity<List<ProductDTO>> getOutOfStockProducts() {
        
        log.info("查询缺货产品");
        
        List<ProductDTO> products = productService.findOutOfStockProducts();
        log.info("缺货产品查询完成，产品数量: {}", products.size());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 根据价格范围查询产品
     * 
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @GetMapping("/price-range")
    @Operation(summary = "根据价格范围查询产品", description = "查询指定价格范围内的产品")
    public ResponseEntity<Page<ProductDTO>> getProductsByPriceRange(
            @Parameter(description = "最低价格") @RequestParam @NotNull BigDecimal minPrice,
            @Parameter(description = "最高价格") @RequestParam @NotNull BigDecimal maxPrice,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("根据价格范围查询产品，最低价格: {}, 最高价格: {}", minPrice, maxPrice);
        
        Page<ProductDTO> products = productService.findByUnitPriceBetween(minPrice, maxPrice, pageable);
        log.info("价格范围产品查询完成，总记录数: {}", products.getTotalElements());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 根据库存范围查询产品
     * 
     * @param minStock 最小库存
     * @param maxStock 最大库存
     * @param pageable 分页参数
     * @return 产品分页结果
     */
    @GetMapping("/stock-range")
    @Operation(summary = "根据库存范围查询产品", description = "查询指定库存范围内的产品")
    public ResponseEntity<Page<ProductDTO>> getProductsByStockRange(
            @Parameter(description = "最小库存") @RequestParam @NotNull Integer minStock,
            @Parameter(description = "最大库存") @RequestParam @NotNull Integer maxStock,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.info("根据库存范围查询产品，最小库存: {}, 最大库存: {}", minStock, maxStock);
        
        Page<ProductDTO> products = productService.findByStockQuantityBetween(minStock, maxStock, pageable);
        log.info("库存范围产品查询完成，总记录数: {}", products.getTotalElements());
        
        return ResponseEntity.ok(products);
    }

    /**
     * 更新产品库存
     * 
     * @param id 产品ID
     * @param quantity 库存变化量
     * @return 更新结果
     */
    @PatchMapping("/{id}/stock")
    @Operation(summary = "更新产品库存", description = "更新指定产品的库存数量")
    public ResponseEntity<Void> updateProductStock(
            @Parameter(description = "产品ID") @PathVariable @NotNull Long id,
            @Parameter(description = "库存变化量") @RequestParam @NotNull Integer quantity) {
        
        log.info("更新产品库存，产品ID: {}, 库存变化量: {}", id, quantity);
        
        try {
            productService.updateStock(id, quantity);
            log.info("产品库存更新成功，产品ID: {}", id);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("产品库存更新失败，产品ID: {}, 错误信息: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 检查产品编码是否存在
     * 
     * @param productCode 产品编码
     * @return 是否存在
     */
    @GetMapping("/exists/code/{productCode}")
    @Operation(summary = "检查产品编码是否存在", description = "检查指定产品编码是否已存在")
    public ResponseEntity<Boolean> checkProductCodeExists(
            @Parameter(description = "产品编码") @PathVariable @NotNull String productCode) {
        
        log.info("检查产品编码是否存在，产品编码: {}", productCode);
        
        boolean exists = productService.existsByProductCode(productCode);
        log.info("产品编码检查完成，产品编码: {}, 存在: {}", productCode, exists);
        
        return ResponseEntity.ok(exists);
    }

    /**
     * 检查产品名称是否存在
     * 
     * @param name 产品名称
     * @return 是否存在
     */
    @GetMapping("/exists/name/{name}")
    @Operation(summary = "检查产品名称是否存在", description = "检查指定产品名称是否已存在")
    public ResponseEntity<Boolean> checkProductNameExists(
            @Parameter(description = "产品名称") @PathVariable @NotNull String name) {
        
        log.info("检查产品名称是否存在，产品名称: {}", name);
        
        boolean exists = productService.existsByName(name);
        log.info("产品名称检查完成，产品名称: {}, 存在: {}", name, exists);
        
        return ResponseEntity.ok(exists);
    }

    /**
     * 统计产品状态分布
     * 
     * @return 状态统计结果
     */
    @GetMapping("/statistics/status")
    @Operation(summary = "统计产品状态分布", description = "统计各状态产品数量")
    public ResponseEntity<List<Object[]>> getProductStatusStatistics() {
        
        log.info("统计产品状态分布");
        
        List<Object[]> statistics = productService.countByStatus();
        log.info("产品状态统计完成，统计项数量: {}", statistics.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 统计产品分类分布
     * 
     * @return 分类统计结果
     */
    @GetMapping("/statistics/category")
    @Operation(summary = "统计产品分类分布", description = "统计各分类产品数量")
    public ResponseEntity<List<Object[]>> getProductCategoryStatistics() {
        
        log.info("统计产品分类分布");
        
        List<Object[]> statistics = productService.countByCategory();
        log.info("产品分类统计完成，统计项数量: {}", statistics.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 统计产品库存水平分布
     * 
     * @return 库存水平统计结果
     */
    @GetMapping("/statistics/stock-level")
    @Operation(summary = "统计产品库存水平分布", description = "统计各库存水平产品数量")
    public ResponseEntity<List<Object[]>> getProductStockLevelStatistics() {
        
        log.info("统计产品库存水平分布");
        
        List<Object[]> statistics = productService.countByStockLevel();
        log.info("产品库存水平统计完成，统计项数量: {}", statistics.size());
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 统计库存不足产品数量
     * 
     * @return 库存不足产品数量
     */
    @GetMapping("/statistics/low-stock-count")
    @Operation(summary = "统计库存不足产品数量", description = "统计库存低于最小库存的产品数量")
    public ResponseEntity<Long> getLowStockProductCount() {
        
        log.info("统计库存不足产品数量");
        
        long count = productService.countLowStockProducts();
        log.info("库存不足产品统计完成，数量: {}", count);
        
        return ResponseEntity.ok(count);
    }

    /**
     * 查询需要补货的产品
     * 
     * @return 需要补货的产品列表
     */
    @GetMapping("/needing-restock")
    @Operation(summary = "查询需要补货的产品", description = "查询库存低于最小库存且状态为活跃的产品")
    public ResponseEntity<List<ProductDTO>> getProductsNeedingRestock() {
        
        log.info("查询需要补货的产品");
        
        List<ProductDTO> products = productService.findProductsNeedingRestock();
        log.info("需要补货产品查询完成，产品数量: {}", products.size());
        
        return ResponseEntity.ok(products);
    }
} 