package com.example.GameHub.Controller;

import com.example.GameHub.entities.Product;
import com.example.GameHub.entities.User;
import com.example.GameHub.model.request.ProductRequest;
import com.example.GameHub.model.response.ProductResponse;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.repository.ProductRepository;
import com.example.GameHub.service.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/products")
@Tag(name = "Product", description = "Quản lý Product")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> createProduct(
            @Parameter(description = "Tiêu đề sản phẩm", required = true)
            @RequestParam("title") String title,

            @Parameter(description = "Mô tả sản phẩm", required = true)
            @RequestParam("description") String description,

            @Parameter(description = "Giá sản phẩm", required = true)
            @RequestParam("price") Double price,

            @Parameter(description = "Giá giảm giá (nếu có)", required = false)
            @RequestParam(value = "discountedPrice", required = false) Double discountedPrice,

            @Parameter(description = "Link tải xuống sản phẩm", required = false)
            @RequestParam(value = "downloadLink", required = false) String downloadLink,

            @Parameter(description = "Tên phiên bản sản phẩm", required = false)
            @RequestParam(value = "versionName", required = false) String versionName,

            @Parameter(description = "ID danh mục", required = true)
            @RequestParam("categoryId") Long categoryId,

            @Parameter(description = "ID của người dùng", required = true)
            @RequestParam("userId") Long userId,

            @Parameter(description = "Danh sách file ảnh của sản phẩm", required = false)
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        return productService.createProduct(title, description, price, discountedPrice, downloadLink, versionName, categoryId, userId, images);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(
            @Parameter(description = "ID của product cần xóa", required = true)
            @PathVariable("id") Long id
    ) {
        return productService.deleteProduct(id);
    }

}
