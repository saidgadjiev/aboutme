package ru.saidgadjiev.aboutme.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.saidgadjiev.aboutme.model.LikeDetails;
import ru.saidgadjiev.aboutme.service.LikeService;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/like/post", method = RequestMethod.POST)
    public ResponseEntity<LikeDetails> like(
            @RequestBody @Valid LikeDetails likeDetails,
            BindingResult bindingResult
    ) throws SQLException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        likeService.create(likeDetails);
        likeDetails.setLikesCount((int) likeService.postLikes(likeDetails.getPostId()));
        likeDetails.setLiked(true);

        return ResponseEntity.ok(likeDetails);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/dislike/post", method = RequestMethod.POST)
    public ResponseEntity<LikeDetails> dislike(
            @RequestBody @Valid LikeDetails likeDetails,
            BindingResult bindingResult
    ) throws SQLException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }
        likeService.remove(likeDetails);
        likeDetails.setLikesCount((int) likeService.postLikes(likeDetails.getPostId()));
        likeDetails.setLiked(false);

        return ResponseEntity.ok(likeDetails);
    }
}
