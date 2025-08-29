package org.example.eventsbooker.repositories.comment;

import org.example.eventsbooker.entites.Comment;

import java.util.List;

public interface CommentRepository {
    public Comment addComment(Comment comment);
    public List<Comment> getAllComments();
    public Comment findComment(Long commentId);
    public List<Comment> allCommentsForEvent(Long eventId);
}
