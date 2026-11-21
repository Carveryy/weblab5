package org.example.weblab5.controller;

import org.example.weblab5.model.Category;
import org.example.weblab5.model.Drink;
import org.example.weblab5.repository.CategoryRepository;
import org.example.weblab5.repository.DrinkRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final DrinkRepository drinkRepository;

    public CategoryController(CategoryRepository categoryRepository, DrinkRepository drinkRepository) {
        this.categoryRepository = categoryRepository;
        this.drinkRepository = drinkRepository;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "categories";
    }

    @PostMapping("/add")
    public String addCategory(@RequestParam String name) {
        Category cat = new Category();
        cat.setName(name);
        categoryRepository.save(cat);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryRepository.findById(id).orElseThrow());
        return "edit-category";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id, @RequestParam String name) {
        Category cat = categoryRepository.findById(id).orElseThrow();
        cat.setName(name);
        categoryRepository.save(cat);
        return "redirect:/categories";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteCategory(@PathVariable Long id) {
        Category cat = categoryRepository.findById(id).orElseThrow();
        for (Drink d : cat.getDrinks()) {
            d.setCategory(null);
            drinkRepository.save(d);
        }
        categoryRepository.delete(cat);
        return "redirect:/categories";
    }
}