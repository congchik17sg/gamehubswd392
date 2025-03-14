package com.example.GameHub.Controller;

import com.example.GameHub.entities.Category;
import com.example.GameHub.model.request.CategoryRequest;
import com.example.GameHub.model.response.ResponseObject;
import com.example.GameHub.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/categories")
@Tag(name = "Category", description = "Managing Category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertCategory(@RequestBody @Valid CategoryRequest requestDTO) {
        return categoryService.insertCategory(requestDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCategory(@RequestBody @Valid CategoryRequest requestDTO, @PathVariable Long id) {
        return categoryService.updateCategory(id, requestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
