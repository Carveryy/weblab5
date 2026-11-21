package org.example.weblab5.controller;

import org.example.weblab5.model.Category;
import org.example.weblab5.model.Drink;
import org.example.weblab5.repository.CategoryRepository;
import org.example.weblab5.repository.DrinkRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DrinkController {

    private final DrinkRepository drinkRepository;
    private final CategoryRepository categoryRepository;

    public DrinkController(DrinkRepository drinkRepository, CategoryRepository categoryRepository) {
        this.drinkRepository = drinkRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping({"/", "/drinks"})
    public String listDrinks(@RequestParam(required = false) String name,
                             @RequestParam(required = false) Long categoryId,
                             @RequestParam(required = false) Double minVolume,
                             @RequestParam(required = false) Double maxVolume,
                             @RequestParam(required = false) Double minPrice,
                             @RequestParam(required = false) Double maxPrice,
                             Model model) {

        String searchName = name == null ? "" : name.trim();

        model.addAttribute("drinks", drinkRepository.search(
                searchName, categoryId, minVolume, maxVolume, minPrice, maxPrice));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("newDrink", new Drink());
        model.addAttribute("filterName", name);
        model.addAttribute("filterCategoryId", categoryId);
        model.addAttribute("filterMinVolume", minVolume);
        model.addAttribute("filterMaxVolume", maxVolume);
        model.addAttribute("filterMinPrice", minPrice);
        model.addAttribute("filterMaxPrice", maxPrice);
        return "drinks";
    }

    @PostMapping("/drinks/add")
    public String addDrink(@ModelAttribute Drink drink, @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            drink.setCategory(category);
        }
        drinkRepository.save(drink);
        return "redirect:/drinks";
    }

    @GetMapping("/drinks/edit/{id}")
    public String editDrinkForm(@PathVariable Long id, Model model) {
        model.addAttribute("drink", drinkRepository.findById(id).orElseThrow());
        model.addAttribute("categories", categoryRepository.findAll());
        return "edit-drink";
    }

    @PostMapping("/drinks/update/{id}")
    public String updateDrink(@PathVariable Long id,
                              @ModelAttribute Drink updatedDrink,
                              @RequestParam(value = "categoryId", required = false) Long categoryId) {

        Drink existingDrink = drinkRepository.findById(id).orElseThrow();

        existingDrink.setName(updatedDrink.getName());
        existingDrink.setVolume(updatedDrink.getVolume());
        existingDrink.setPrice(updatedDrink.getPrice());

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            existingDrink.setCategory(category);
        } else {
            existingDrink.setCategory(null);
        }

        drinkRepository.save(existingDrink);
        return "redirect:/drinks";
    }

    @PostMapping("/drinks/delete/{id}")
    public String deleteDrink(@PathVariable Long id) {
        drinkRepository.deleteById(id);
        return "redirect:/drinks";
    }
}