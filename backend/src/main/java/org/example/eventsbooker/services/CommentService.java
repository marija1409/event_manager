package org.example.eventsbooker.services;

import org.example.eventsbooker.entites.Comment;
import org.example.eventsbooker.repositories.comment.CommentRepository;

import javax.inject.Inject;
import java.util.List;

public class CommentService {
    @Inject
    private CommentRepository commentRepository;

    public Comment addComment(Comment comment) {
        return this.commentRepository.addComment(comment);
    }

    public List<Comment> getAllComments() {
        return this.commentRepository.getAllComments();
    }

    public Comment findComment(Long commentId) {
        return this.commentRepository.findComment(commentId);
    }

    public List<Comment> allCommentsForEvent(Long eventId) {
        return this.commentRepository.allCommentsForEvent(eventId);
    }
}
