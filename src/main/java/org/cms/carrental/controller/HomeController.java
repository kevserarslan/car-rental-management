package org.cms.carrental.controller;

import lombok.RequiredArgsConstructor;
import org.cms.carrental.entity.Car;
import org.cms.carrental.entity.Category;
import org.cms.carrental.repository.CarRepository;
import org.cms.carrental.repository.CategoryRepository;
import org.cms.carrental.service.CurrencyService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Thymeleaf Controller - Server-Side Rendered Pages
 * Hocanın istediği: En az bir sayfa Thymeleaf ile render edilmeli
 */
@Controller
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeController {

    private final CarRepository carRepository;
    private final CategoryRepository categoryRepository;
    private final CurrencyService currencyService;

    /**
     * Ana sayfa - Araç listesi ve bilgiler
     */
    @GetMapping("/")
    public String homePage(Model model) {
        List<Car> cars = carRepository.findAllWithCategory();
        List<Category> categories = categoryRepository.findAll();

        // Döviz kuru bilgisi (External API)
        Double usdToTry = currencyService.getExchangeRate("USD", "TRY");
        Double eurToTry = currencyService.getExchangeRate("EUR", "TRY");

        model.addAttribute("cars", cars);
        model.addAttribute("categories", categories);
        model.addAttribute("totalCars", cars.size());
        model.addAttribute("totalCategories", categories.size());
        model.addAttribute("usdToTry", usdToTry);
        model.addAttribute("eurToTry", eurToTry);
        model.addAttribute("appName", "Car Rental System");
        model.addAttribute("appVersion", "1.0.0");

        return "index";
    }


    /**
     * Araçlar sayfası - Filtreleme ile
     */
    @GetMapping("/cars-page")
    public String carsPage(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            Model model) {

        List<Car> cars;

        if (categoryId != null) {
            cars = carRepository.findByCategoryIdWithCategory(categoryId);
        } else if (status != null) {
            cars = carRepository.findByStatusWithCategory(Car.CarStatus.valueOf(status));
        } else {
            cars = carRepository.findAllWithCategory();
        }

        List<Category> categories = categoryRepository.findAll();

        // Döviz kuru
        Double usdToTry = currencyService.getExchangeRate("USD", "TRY");

        model.addAttribute("cars", cars);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("usdToTry", usdToTry);

        return "cars";
    }
}

