package org.example.eventsbooker.repositories.engagement;

import org.example.eventsbooker.entites.enums.Engagement;
import org.example.eventsbooker.repositories.MySqlAbstractRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySqlEngagementRepository extends MySqlAbstractRepository implements EngagementRepository {

    @Override
    public Engagement engage(Long id, String type, Boolean like, String cookie) {
        Engagement engagement = like ? Engagement.like : Engagement.dislike;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            if (type.equalsIgnoreCase("event")) {
                // Check if user already engaged with this event
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM event_engagement WHERE session_id = ? AND event_id = ?");
                preparedStatement.setString(1, cookie);
                preparedStatement.setLong(2, id);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // User already engaged - update the engagement
                    preparedStatement = connection.prepareStatement("UPDATE event_engagement SET `like` = ? WHERE session_id = ? AND event_id = ?");
                    preparedStatement.setBoolean(1, like);
                    preparedStatement.setString(2, cookie);
                    preparedStatement.setLong(3, id);
                    preparedStatement.executeUpdate();
                } else {
                    // User hasn't engaged - insert new engagement
                    preparedStatement = connection.prepareStatement("INSERT INTO event_engagement (session_id, event_id, `like`) VALUES(?, ?, ?)");
                    preparedStatement.setString(1, cookie);
                    preparedStatement.setLong(2, id);
                    preparedStatement.setBoolean(3, like);
                    preparedStatement.executeUpdate();
                }
            } else {
                // Similar logic for comments
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM comments_engagement WHERE session_id = ? AND comment_id = ?");
                preparedStatement.setString(1, cookie);
                preparedStatement.setLong(2, id);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Update existing comment engagement
                    preparedStatement = connection.prepareStatement("UPDATE comments_engagement SET `like` = ? WHERE session_id = ? AND comment_id = ?");
                    preparedStatement.setBoolean(1, like);
                    preparedStatement.setString(2, cookie);
                    preparedStatement.setLong(3, id);
                    preparedStatement.executeUpdate();
                } else {
                    // Insert new comment engagement
                    preparedStatement = connection.prepareStatement("INSERT INTO comments_engagement (session_id, comment_id, `like`) VALUES(?, ?, ?)");
                    preparedStatement.setString(1, cookie);
                    preparedStatement.setLong(2, id);
                    preparedStatement.setBoolean(3, like);
                    preparedStatement.executeUpdate();
                }
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

    @Override
    public Integer countEngagements(Long id, String type) {
        int count = 0;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            if (type.equalsIgnoreCase("event")) {
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM event_engagement WHERE event_id = ?");
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            } else {
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM comments_engagement WHERE comment_id = ?");
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return count;
    }

    @Override
    public Map<Engagement, Integer> countEngagementSeparate(Long id, String type) {
        Map<Engagement, Integer> engagement = new HashMap<>();

        engagement.put(Engagement.like, 0);
        engagement.put(Engagement.dislike, 0);

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            if (type.equalsIgnoreCase("event")) {
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM event_engagement WHERE event_id = ? AND `like` = true");
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    engagement.put(Engagement.like, resultSet.getInt(1));
                }

                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM event_engagement WHERE event_id = ? AND `like` = false");
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    engagement.put(Engagement.dislike, resultSet.getInt(1));
                }
            } else {
                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM comments_engagement WHERE comment_id = ? AND `like` = true");
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    engagement.put(Engagement.like, resultSet.getInt(1));
                }

                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM comments_engagement WHERE comment_id = ? AND `like` = false");
                preparedStatement.setLong(1, id);
                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    engagement.put(Engagement.dislike, resultSet.getInt(1));
                }
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
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