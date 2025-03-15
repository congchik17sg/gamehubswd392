package com.example.GameHub.service;

import com.example.GameHub.entities.Category;
import com.example.GameHub.entities.Product;
import com.example.GameHub.entities.ProductImage;
import com.example.GameHub.entities.User;
import com.example.GameHub.model.request.ProductRequest;
import com.example.GameHub.model.response.ProductResponse;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.repository.CategoryRepository;
import com.example.GameHub.repository.ProductImageRepository;
import com.example.GameHub.repository.ProductRepository;
import com.example.GameHub.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private CloudinaryService cloudinaryService;


    public ResponseEntity<ResponseObject> getProductById(Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Tìm thấy sản phẩm", foundProduct.get())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy sản phẩm với ID: " + id, "")
            );
        }
    }

    public ResponseEntity<ResponseObject> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Danh sách sản phẩm", products)
        );
    }
    public ResponseEntity<ResponseObject> createProduct(
            String title, String description, Double price, Double discountedPrice,
            String downloadLink, String versionName, Long categoryId, Long userId,
            MultipartFile[] images) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Product product = new Product();
        product.setProductTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscountPrice(discountedPrice);
        product.setDownloadLink(downloadLink);
        product.setUser(user);
        product.setCategory(category);
        product.setStatus("ACTIVE");
        product.setCreateAt(LocalDateTime.now());
        product.setUpdateAt(LocalDateTime.now());
        product.setReleaseDate(LocalDate.now());
        product.setVersionName(versionName);

        List<String> imageUrls = new ArrayList<>();

        if (images != null && images.length > 0) {
            List<ProductImage> productImages = Arrays.stream(images)
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String imageUrl = cloudinaryService.uploadFile(file);
                        imageUrls.add(imageUrl);
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(imageUrl);
                        productImage.setCreateAt(LocalDateTime.now());
                        productImage.setUpdateAt(LocalDateTime.now());
                        productImage.setProduct(product);
                        return productImage;
                    })
                    .collect(Collectors.toList());
            product.setImages(productImages);
        }

        productRepository.save(product);

        ProductResponse productResponse = new ProductResponse(
                product.getId(),
                product.getProductTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getDownloadLink(),
                product.getVersionName(),
                product.getStatus(),
                product.getReleaseDate(),
                product.getCreateAt(),
                imageUrls
        );

        return ResponseEntity.ok(new ResponseObject("ok", "Tạo sản phẩm thành công", productResponse));
    }

    @Transactional
    public ResponseEntity<ResponseObject> updateProduct(
            Long productId, String title, String description, Double price, Double discountedPrice,
            String downloadLink, String versionName, Long categoryId, MultipartFile[] images) {

        // Tìm sản phẩm cần cập nhật
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Cập nhật các thông tin cơ bản
        product.setProductTitle(title);
        product.setDescription(description);
        product.setPrice(price);
        product.setDiscountPrice(discountedPrice);
        product.setDownloadLink(downloadLink);
        product.setVersionName(versionName);
        product.setUpdateAt(LocalDateTime.now());

        // Kiểm tra và cập nhật category nếu có
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }

        // Xử lý cập nhật ảnh nếu có ảnh mới
        if (images != null && images.length > 0) {
            // Xóa ảnh cũ trên Cloudinary và trong database
            List<ProductImage> oldImages = product.getImages();
            for (ProductImage img : oldImages) {
                cloudinaryService.deleteFile(img.getImageUrl()); // Xóa ảnh trên Cloudinary
                productImageRepository.delete(img); // Xóa trong database
            }

            // Upload ảnh mới
            List<ProductImage> newProductImages = Arrays.stream(images)
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String imageUrl = cloudinaryService.uploadFile(file);
                        ProductImage productImage = new ProductImage();
                        productImage.setImageUrl(imageUrl);
                        productImage.setCreateAt(LocalDateTime.now());
                        productImage.setUpdateAt(LocalDateTime.now());
                        productImage.setProduct(product);
                        return productImage;
                    })
                    .collect(Collectors.toList());

            product.setImages(newProductImages);
        }

        productRepository.save(product);

        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse(
                product.getId(),
                product.getProductTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getDiscountPrice(),
                product.getDownloadLink(),
                product.getVersionName(),
                product.getStatus(),
                product.getReleaseDate(),
                product.getCreateAt(),
                imageUrls
        );

        return ResponseEntity.ok(new ResponseObject("ok", "Cập nhật sản phẩm thành công", productResponse));
    }
    public ResponseEntity<ResponseObject> deleteProduct(Long id) {
        try {
            Optional<Product> productOptional = productRepository.findById(id);
            if (productOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy product với ID: " + id, "")
                );
            }

            productRepository.deleteById(id);
            return ResponseEntity.ok(
                    new ResponseObject("ok", "Xóa product thành công!", "")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("failed", "Lỗi khi xóa product: " + e.getMessage(), "")
            );
        }
    }

}


