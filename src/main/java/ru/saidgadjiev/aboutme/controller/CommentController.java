package ru.saidgadjiev.aboutme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.saidgadjiev.aboutme.model.CommentDetails;
import ru.saidgadjiev.aboutme.service.BlogService;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping(value = "/api/comment")
public class CommentController {

    @Autowired
    private BlogService blogService;

    @GetMapping(value = "/{id}/comments")
    public ResponseEntity<Page<CommentDetails>> getCommentsByPost(
            @PathVariable("id") Integer id,
            @PageableDefault(page = 0, size = 10, sort = "createdDate", direction = Sort.Direction.DESC) Pageable page
    ) throws SQLException {
        Page<CommentDetails> commentDetails = blogService.getCommentsByPostId(id, page);

        return ResponseEntity.ok(commentDetails);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{id}/create")
    public ResponseEntity createCommentOfPost(
            @PathVariable("id") Integer id,
            @RequestBody @Valid CommentDetails commentDetails,
            BindingResult bindingResult
    ) throws SQLException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        blogService.createCommentOfPost(id, commentDetails);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("@authorization.canEditComment(#id)")
    @PostMapping(value = "/update/{id}")
    public ResponseEntity<CommentDetails> updateComment(
            @PathVariable("id") Integer id,
            @RequestBody CommentDetails commentDetails
    ) throws SQLException {
        blogService.updateComment(id, commentDetails);

        return ResponseEntity.ok(commentDetails);
    }

    @PreAuthorize("@authorization.canDeleteComment(#id)")
    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Integer id) throws SQLException {
        int deleted = blogService.deleteCommentById(id);

        if (deleted == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }
}
