package com.example.GameHub.service;

import com.example.GameHub.entities.Category;
import com.example.GameHub.model.request.CategoryRequest;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository repository;

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public ResponseEntity<ResponseObject> findById(Long id) {
        Optional<Category> foundCategory = repository.findById(id);
        if (foundCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Tìm thấy category", foundCategory)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy category với id: " + id, "")
            );
        }
    }

    public ResponseEntity<ResponseObject> insertCategory(CategoryRequest requestDTO) {
        List<Category> foundCategories = repository.findByCategoryName(requestDTO.getCategoryName().trim());
        if (!foundCategories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "Tên category đã tồn tại", "")
            );
        }

        Category newCategory = new Category();
        newCategory.setCategoryName(requestDTO.getCategoryName());
        newCategory.setDescription(requestDTO.getDescription());
        newCategory.setCreateAt(LocalDateTime.now());
        newCategory.setUpdateAt(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Thêm category thành công", repository.save(newCategory))
        );
    }

    public ResponseEntity<ResponseObject> updateCategory(Long id, CategoryRequest requestDTO) {
        return repository.findById(id)
                .map(category -> {
                    category.setCategoryName(requestDTO.getCategoryName());
                    category.setDescription(requestDTO.getDescription());
                    category.setUpdateAt(LocalDateTime.now());
                    repository.save(category);
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject("ok", "Cập nhật danh mục thành công", category)
                    );
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy danh mục với ID: " + id, "")
                ));
    }

    public ResponseEntity<ResponseObject> deleteCategory(Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy danh mục", "")
            );
        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Danh mục đã được xóa thành công", "")
        );
    }
}
