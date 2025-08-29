package org.example.eventsbooker.repositories.comment;

import org.example.eventsbooker.entites.Comment;
import org.example.eventsbooker.entites.enums.Engagement;
import org.example.eventsbooker.repositories.MySqlAbstractRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlCommentRepository extends MySqlAbstractRepository implements CommentRepository {
    @Override
    public Comment addComment(Comment comment) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Comment savedComment = null;

        try {
            connection = this.newConnection();

            String[] generatedColumns = {"comment_id"};
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO comments (author, created_at, event_id, content) VALUES (?, ?, ?, ?)",
                    generatedColumns
            );

            LocalDateTime createdAt = LocalDateTime.now();

            preparedStatement.setString(1, comment.getAuthor());
            preparedStatement.setObject(2, createdAt);
            preparedStatement.setLong(3, comment.getEventId());
            preparedStatement.setString(4, comment.getContent());
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                Long newId = resultSet.getLong(1);
                // fetch the full saved comment with all fields populated
                savedComment = findComment(newId);
                if (savedComment != null) {
                    savedComment.setActivity(getCommentEngagement(newId));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return savedComment;
    }


    @Override
    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            statement = connection.createStatement();
            resultSet = statement.executeQuery("select * from comments");
            while (resultSet.next()) {
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                comments.add(new Comment(
                        resultSet.getLong("comment_id"),
                        resultSet.getString("author"),
                        resultSet.getString("content"),
                        createdAt,
                        resultSet.getLong("event_id")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return comments;
    }

    @Override
    public Comment findComment(Long commentId) {
        Comment comment = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM comments where comment_id = ?");
            preparedStatement.setLong(1, commentId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                comment = new Comment(
                        resultSet.getLong("comment_id"),
                        resultSet.getString("author"),
                        resultSet.getString("content"),
                        createdAt,
                        resultSet.getLong("event_id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return comment;
    }

    @Override
    public List<Comment> allCommentsForEvent(Long eventId) {
        List<Comment> comments = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM comments WHERE event_id = ? ORDER BY created_at DESC");
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                Comment comment = new Comment(
                        resultSet.getLong("comment_id"),
                        resultSet.getString("author"),
                        resultSet.getString("content"),
                        createdAt,
                        resultSet.getLong("event_id")
                );
                comment.setActivity(getCommentEngagement(comment.getCommentId()));
                comments.add(comment);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return comments;
    }

    private Map<Engagement, Long> getCommentEngagement(Long commentId) {
        Map<Engagement, Long> engagement = new HashMap<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            // Count likes
            preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM comments_engagement WHERE comment_id = ? AND `like` = true"
            );
            preparedStatement.setLong(1, commentId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                engagement.put(Engagement.like, resultSet.getLong(1));
            }

            // Count dislikes
            preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM comments_engagement WHERE comment_id = ? AND `like` = false"
            );
            preparedStatement.setLong(1, commentId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                engagement.put(Engagement.dislike, resultSet.getLong(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return engagement;
    }
}
