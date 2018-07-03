package ru.saidgadjiev.aboutme.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.saidgadjiev.aboutme.model.CategoryDetails;
import ru.saidgadjiev.aboutme.service.BlogService;

import java.sql.SQLException;

/**
 * Created by said on 12.02.2018.
 */
@RestController
@RequestMapping(value = "/api/category")
public class CategoryController {

    private static final Logger LOGGER = Logger.getLogger(CategoryController.class);

    @Autowired
    private BlogService blogService;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody CategoryDetails categoryDetails) throws SQLException {
        LOGGER.debug("create(Category: " + categoryDetails.toString() + ")");

        blogService.createCategory(categoryDetails);

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseEntity<CategoryDetails> update(@RequestBody CategoryDetails categoryDetails) throws SQLException {
        LOGGER.debug("update(Category: " + categoryDetails.toString() + ")");

        int count = blogService.updateCategory(categoryDetails);

        LOGGER.debug("Update count " + count);

        return ResponseEntity.ok(categoryDetails);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<Page<CategoryDetails>> getCategories(
            @PageableDefault(page = 0, size = 10, sort = "title", direction = Sort.Direction.DESC) Pageable page
    ) throws SQLException {
        LOGGER.debug("getCategories()");
        Page<CategoryDetails> categories = blogService.getCategories(page);

        return ResponseEntity.ok(categories);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<CategoryDetails> getCategory(@PathVariable("id") Integer id) throws SQLException {
        LOGGER.debug("getPostId()" + id);
        CategoryDetails categoryDetails = blogService.getCategoryById(id);

        return new ResponseEntity<>(categoryDetails, HttpStatus.OK);
    }
}