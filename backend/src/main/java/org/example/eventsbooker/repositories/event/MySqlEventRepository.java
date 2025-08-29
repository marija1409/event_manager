package org.example.eventsbooker.repositories.event;

import org.example.eventsbooker.entites.*;
import org.example.eventsbooker.entites.dtos.EventDTO;
import org.example.eventsbooker.entites.dtos.EventDetailsDTO;
import org.example.eventsbooker.entites.dtos.RsvpResponseDTO;
import org.example.eventsbooker.entites.enums.Engagement;
import org.example.eventsbooker.entites.enums.UserType;
import org.example.eventsbooker.repositories.MySqlAbstractRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlEventRepository extends MySqlAbstractRepository implements EventRepository {

    @Override
    public Tag getTag(String name){
        Tag tag = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM tags where tag_name = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                tag = new Tag(resultSet.getLong("tag_id"), resultSet.getString("tag_name"));
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
        return tag;
    }

    private void addTagEvent(Long eventId, Long tagId){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("INSERT INTO tags_events (tag_id, event_id) VALUES(?, ?)");
            preparedStatement.setLong(1, tagId);
            preparedStatement.setLong(2, eventId);
            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public Tag addTag(String name){
        Tag tag = new Tag();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String[] generatedColumns = {"tag_id"};

            preparedStatement = connection.prepareStatement("INSERT INTO tags (tag_name) VALUES(?)", generatedColumns);
            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                tag.setTagId((long) resultSet.getLong(1));
                tag.setName(name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return tag;
    }

    @Override
    public List<Event> getEventsByCategory(Long categoryId, Integer page, Integer limit) {
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;

        int offset = (page - 1) * limit;
        List<Event> events = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            statement = connection.prepareStatement("SELECT * FROM events WHERE category = ? ORDER BY created_at DESC LIMIT ? OFFSET ?");
            statement.setLong(1, categoryId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long eventId = resultSet.getLong("event_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                LocalDateTime startingAt = resultSet.getObject("starting_at", LocalDateTime.class);
                String location = resultSet.getString("location");
                Integer maxCapacity = resultSet.getInt("max_capacity");
                Integer views = resultSet.getInt("views");
                User author = findUserById(resultSet.getLong("author"));
                Category category = findCategoryById(resultSet.getLong("category"));
                List<Tag> tags = getTagsForEvent(eventId);

                Event event = new Event(eventId, title, description, createdAt, startingAt, location, maxCapacity, views, author, category, tags);

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return events;
    }

    private Integer getViews(Long eventId, String cookie) {
        Integer views = 0;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("INSERT IGNORE INTO views (session_id, event_id) VALUES(?,?)");
            preparedStatement.setString(1, cookie);
            preparedStatement.setLong(2, eventId);
            preparedStatement.executeUpdate();

            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM views WHERE event_id = ?");
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                views = resultSet.getInt(1);
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

        return views;
    }

    private Map<Engagement, Long> engagement(Long eventId){
        Map<Engagement, Long> engagement = new HashMap<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM event_engagement  WHERE event_id = ? and `like` = true");
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                engagement.put(Engagement.like, resultSet.getLong(1));
            }

            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM event_engagement  WHERE event_id = ? and `like` = false");
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                engagement.put(Engagement.dislike, resultSet.getLong(1));
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

    private Map<Engagement, Long> commentsEngagement(Long commentId){
        Map<Engagement, Long> engagement = new HashMap<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM comments_engagement WHERE comment_id = ? and `like` = true");
            preparedStatement.setLong(1, commentId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                engagement.put(Engagement.like, resultSet.getLong(1));
            }

            preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM comments_engagement WHERE comment_id = ? and `like` = false");
            preparedStatement.setLong(1, commentId);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                engagement.put(Engagement.dislike, resultSet.getLong(1));
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

    private List<Comment> eventComments(Long eventId){
        List<Comment> comments = new ArrayList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM comments WHERE event_id = ?");
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long id = resultSet.getLong("comment_id");
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);

                Comment comment = new Comment(
                        id,
                        resultSet.getString("author"),
                        resultSet.getString("content"),
                        createdAt,
                        eventId
                );
                comment.setActivity(commentsEngagement(id));
                comments.add(comment);
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

        return comments;
    }



    @Override
    public EventDetailsDTO eventDetails(Long eventId, String cookie) {
        EventDetailsDTO event = new EventDetailsDTO();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try{
            connection = this.newConnection();
            Integer views = getViews(eventId, cookie);
            System.out.println("Views" + views);
            Map<Engagement, Long> engagement = engagement(eventId);
            System.out.println("engagement: " + engagement);
            List<Comment> comments = eventComments(eventId);

            preparedStatement = connection.prepareStatement("SELECT * FROM events where event_id = ?");
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                LocalDateTime startingAt = resultSet.getObject("starting_at", LocalDateTime.class);
                String location = resultSet.getString("location");
                User author = findUserById(resultSet.getLong("author"));
                Category category = findCategoryById(resultSet.getLong("category"));
                int maxCapacity = resultSet.getInt("max_capacity");
                int currentCapacity = 0;

                preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM events join rsvps on events.event_id = rsvps.event_id where events.event_id = ?");
                preparedStatement.setLong(1, eventId);
                resultSet = preparedStatement.executeQuery();
                if(resultSet.next()) {
                    currentCapacity = maxCapacity - resultSet.getInt(1);
                }

                event = new EventDetailsDTO(
                        eventId,
                        title,
                        description,
                        createdAt,
                        startingAt,
                        location,
                        author,
                        category,
                        new ArrayList<>(),
                        maxCapacity,
                        currentCapacity,
                        views,
                        engagement,
                        comments
                );

            }

            List<Tag> eventTags = getTagsForEvent(eventId);
            event.setTags(eventTags);

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

        return event;
    }

    @Override
    public List<Event> topEvents() {
        List<Event> topEvents = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();


            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT e.event_id, COUNT(*) AS top\n" +
                    "FROM events e\n" +
                    "JOIN views v ON e.event_id = v.event_id\n" +
                    "GROUP BY e.event_id\n" +
                    "ORDER BY top DESC\n" +
                    "LIMIT 10;\n");
            System.out.println(resultSet);

            while (resultSet.next()) {
                Event event = findEventById(resultSet.getLong("event_id"));
                topEvents.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }


        return topEvents;

    }

    @Override
    public RsvpResponseDTO rsvp(Long eventId, String email) {
        LocalDateTime createdAt = LocalDateTime.now();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean success = false;
        int currentCount = 0;

        try {
            connection = this.newConnection();

            String[] generatedColumns = {"rsvp_id"};
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO rsvps (user_email, event_id, created_at) VALUES(?, ?, ?)",
                    generatedColumns
            );
            preparedStatement.setString(1, email);
            preparedStatement.setLong(2, eventId);
            preparedStatement.setObject(3, createdAt);
            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                success = true;
            }

            this.closeStatement(preparedStatement);

            preparedStatement = connection.prepareStatement(
                    "SELECT COUNT(*) FROM rsvps WHERE event_id = ?"
            );
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                currentCount = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return new RsvpResponseDTO(success, currentCount);
    }

    @Override
    public List<Event> allEventsTag(Integer page, Integer limit, String tag) {
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;

        int offset = (page - 1) * limit;
        List<Event> events = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String sql = " SELECT e.* FROM events e JOIN tags_events te ON e.event_id = te.event_id JOIN tags t ON te.tag_id = t.tag_id WHERE t.tag_name = ? ORDER BY e.created_at DESC LIMIT ? OFFSET ? ";

            statement = connection.prepareStatement(sql);
            statement.setString(1, tag);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long eventId = resultSet.getLong("event_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                LocalDateTime startingAt = resultSet.getObject("starting_at", LocalDateTime.class);
                String location = resultSet.getString("location");
                Integer maxCapacity = resultSet.getInt("max_capacity");
                Integer views = resultSet.getInt("views");
                User author = findUserById(resultSet.getLong("author"));
                Category category = findCategoryById(resultSet.getLong("category"));
                List<Tag> tags = getTagsForEvent(eventId);

                Event event = new Event(eventId, title, description, createdAt, startingAt,
                        location, maxCapacity, views, author, category, tags);

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return events;
    }

    @Override
    public List<Event> relatedEvents(Long eventId, List<String> tags) {
        List<Event> events = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();


            for (int i=0; i<tags.size() && events.size()!=3; i++) {
                Tag tag = getTag(tags.get(i));
                statement = connection.createStatement();
                resultSet = statement.executeQuery("select events.event_id, title from events join tags_events on events.event_id = tags_events.event_id where tag_id = " + tag.getTagId());

                while (resultSet.next() && events.size()!=3) {
                    long currentEventId = resultSet.getLong("event_id");
                    if (currentEventId == eventId) {
                        continue;
                    }
                    Event event = findEventById(currentEventId);
                    if (!events.contains(event)) events.add(event);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return events;
    }

    @Override
    public List<Event> top3Events() {
        List<Event> events = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("select events.event_id, COUNT(*) as popular from events join event_engagement ed on events.event_id = ed.event_id GROUP BY (events.event_id) order by popular desc limit 3");
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                long currentEventId = resultSet.getLong("event_id");
                Event event = findEventById(currentEventId);
                events.add(event);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return events;
    }


    @Override
    public Event addEvent(EventDTO eventDTO) {
        Event event = new Event();
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setCreatedAt(LocalDateTime.now());
        event.setStartingAt(eventDTO.getTime());
        event.setLocation(eventDTO.getLocation());
        event.setViews(0);

        User author = findUserByEmail(eventDTO.getAuthor());
        Category category = findCategoryByName(eventDTO.getCategory());
        event.setAuthor(author);
        event.setCategory(category);

        if(eventDTO.getMaxCapacity() != null){
            event.setMaxCapacity(eventDTO.getMaxCapacity());
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String[] generatedColumns = {"event_id"};

            preparedStatement = connection.prepareStatement("INSERT INTO events (title, description, created_at, starting_at, location, views, author, category, max_capacity) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)", generatedColumns);
            preparedStatement.setString(1, event.getTitle());
            preparedStatement.setString(2, event.getDescription());
            preparedStatement.setObject(3, event.getCreatedAt());
            preparedStatement.setObject(4, event.getStartingAt());
            preparedStatement.setString(5, event.getLocation());
            preparedStatement.setInt(6, event.getViews());
            preparedStatement.setLong(7, author.getUserId());
            preparedStatement.setLong(8, category.getCategoryId());
            if (event.getMaxCapacity() > 0) {
                preparedStatement.setInt(9, event.getMaxCapacity());
            } else {
                preparedStatement.setNull(9, java.sql.Types.INTEGER);
            }

            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                event.setEventId((long) resultSet.getLong(1));
            }

            String input = eventDTO.getTags();

            String[] tags = input.split(",");

            List<Tag> tagList = new ArrayList<>();

            for (String tag : tags) {
                Tag newTag = null;
                Tag t = getTag(tag);
                if( t != null){
                    newTag = t;
                    addTagEvent(event.getEventId(), t.getTagId());
                }else{
                    newTag = addTag(tag);
                    addTagEvent(event.getEventId(), newTag.getTagId());
                }
                tagList.add(newTag);
            }

            event.setTags(tagList);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return event;

    }

    private List<Tag> getTagsForEvent(Long eventId) {
        List<Tag> tags = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            statement = connection.prepareStatement(
                    "SELECT t.tag_id, t.tag_name " +
                            "FROM tags t " +
                            "JOIN tags_events te ON t.tag_id = te.tag_id " +
                            "WHERE te.event_id = ?"
            );
            statement.setLong(1, eventId);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Tag tag = new Tag(resultSet.getLong("tag_id"), resultSet.getString("tag_name"));
                tags.add(tag);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return tags;
    }


    @Override
    public List<Event> getAllEvents(Integer page, Integer limit) {
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;

        int offset = (page - 1) * limit;
        List<Event> events = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            statement = connection.prepareStatement("SELECT * FROM events ORDER BY created_at DESC LIMIT ? OFFSET ?");
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long eventId = resultSet.getLong("event_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                LocalDateTime startingAt = resultSet.getObject("starting_at", LocalDateTime.class);
                String location = resultSet.getString("location");
                Integer maxCapacity = resultSet.getInt("max_capacity");
                Integer views = resultSet.getInt("views");
                User author = findUserById(resultSet.getLong("author"));
                Category category = findCategoryById(resultSet.getLong("category"));
                List<Tag> tags = getTagsForEvent(eventId);

                Event event = new Event(eventId, title, description, createdAt, startingAt, location, maxCapacity, views, author, category, tags);

                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return events;
    }

    private void deleteTagsForEvent(Long eventId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("DELETE FROM tags_events WHERE event_id = ?");
            preparedStatement.setLong(1, eventId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }


    @Override
    public Event updateEvent(Event event) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();

            String sql = "UPDATE events SET title = ?, description = ?, starting_at = ?, location = ?, max_capacity = ?, category = ? WHERE event_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, event.getTitle());
            preparedStatement.setString(2, event.getDescription());
            preparedStatement.setObject(3, event.getStartingAt());
            preparedStatement.setString(4, event.getLocation());
            preparedStatement.setInt(5, event.getMaxCapacity());
            preparedStatement.setLong(6, event.getCategory().getCategoryId());
            preparedStatement.setLong(7, event.getEventId());

            int updated = preparedStatement.executeUpdate();
            if (updated == 0) return null;

            deleteTagsForEvent(event.getEventId());

            for (Tag tag : event.getTags()) {
                Tag existingTag = getTag(tag.getName());
                if (existingTag == null) {
                    existingTag = addTag(tag.getName());
                }
                addTagEvent(event.getEventId(), existingTag.getTagId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return findEventById(event.getEventId());
    }

    private void deleteCommentsForEvent(Long eventId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try{
            connection = this.newConnection();
            String sql = "DELETE FROM comments WHERE event_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, eventId);
            preparedStatement.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }


    @Override
    public void deleteEvent(Long id) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();
            String sql = "DELETE FROM events WHERE event_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();

            deleteCommentsForEvent(id);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }

    @Override
    public List<Event> searchEvents(String query, Integer page, Integer limit) {
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;
        int offset = (page - 1) * limit;

        List<Event> events = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = this.newConnection();

            String sql = "SELECT * FROM events " +
                    "WHERE LOWER(title) LIKE ? OR LOWER(description) LIKE ? " +
                    "ORDER BY created_at DESC " +
                    "LIMIT ? OFFSET ?";

            statement = connection.prepareStatement(sql);
            String likeQuery = "%" + query.toLowerCase() + "%";

            statement.setString(1, likeQuery);
            statement.setString(2, likeQuery);
            statement.setInt(3, limit);
            statement.setInt(4, offset);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long eventId = resultSet.getLong("event_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                LocalDateTime startingAt = resultSet.getObject("starting_at", LocalDateTime.class);
                String location = resultSet.getString("location");
                Integer maxCapacity = resultSet.getInt("max_capacity");
                Integer views = resultSet.getInt("views");
                User author = findUserById(resultSet.getLong("author"));
                Category category = findCategoryById(resultSet.getLong("category"));
                List<Tag> tags = getTagsForEvent(eventId);

                events.add(new Event(eventId, title, description, createdAt, startingAt, location, maxCapacity, views, author, category, tags));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return events;
    }



    public Event findEventById(Long eventId) {
        Event event = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM events where event_id = ?");
            preparedStatement.setLong(1, eventId);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                LocalDateTime createdAt = resultSet.getObject("created_at", LocalDateTime.class);
                LocalDateTime startingAt = resultSet.getObject("starting_at", LocalDateTime.class);
                String location = resultSet.getString("location");
                Integer maxCapacity = resultSet.getInt("max_capacity");
                Integer views = resultSet.getInt("views");
                User author = findUserById(resultSet.getLong("author"));
                Category category = findCategoryById(resultSet.getLong("category"));
                List<Tag> tags = getTagsForEvent(eventId);
                event = new Event(eventId, title, description, createdAt, startingAt, location, maxCapacity, views, author, category,tags);
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

        return event;
    }



    private User findUserByEmail(String email) {
        User user = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM users where email = ?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                UserType role = null;
                if (resultSet.getString("type").equalsIgnoreCase("ADMIN")) {
                    role = UserType.ADMIN;
                }else {
                    role = UserType.EVENT_CREATOR;
                }

                user = new User(resultSet.getLong("user_id"), resultSet.getString("email"), resultSet.getString("name"), resultSet.getString("last_name"), role, resultSet.getString("password"), resultSet.getBoolean("active"));
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

        return user;
    }

    private User findUserById(long id) {
        User user = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM users where user_id = ?");
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                UserType role = null;
                if (resultSet.getString("type").equalsIgnoreCase("ADMIN")) {
                    role = UserType.ADMIN;
                }else {
                    role = UserType.EVENT_CREATOR;
                }

                user = new User(resultSet.getLong("user_id"), resultSet.getString("email"), resultSet.getString("name"), resultSet.getString("last_name"), role, resultSet.getString("password"), resultSet.getBoolean("active"));
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

        return user;
    }

    private Category findCategoryById(Long id) {
        Category category = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM categories where category_id = ?");
            preparedStatement.setLong(1, id);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                category = new Category(resultSet.getLong("category_id"), resultSet.getString("name"), resultSet.getString("description"));
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

        return category;
    }

    private Category findCategoryByName(String name) {
        Category category = null;

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            preparedStatement = connection.prepareStatement("SELECT * FROM categories where name = ?");
            preparedStatement.setString(1, name);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                category = new Category(resultSet.getLong("category_id"), resultSet.getString("name"), resultSet.getString("description"));
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

        return category;
    }


}
