package org.example.eventsbooker.repositories.user;

import org.apache.commons.codec.digest.DigestUtils;
import org.example.eventsbooker.entites.User;
import org.example.eventsbooker.entites.dtos.UserDTO;
import org.example.eventsbooker.entites.enums.UserType;
import org.example.eventsbooker.repositories.MySqlAbstractRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlUserRepository extends MySqlAbstractRepository implements UserRepository {

    @Override
    public User addUser(User user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String[] generatedColumns = {"user_id"};

            preparedStatement = connection.prepareStatement("INSERT INTO users (name, last_name, email, type, active, password) VALUES(?, ?, ?, ?, ?, ?)", generatedColumns);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getType().toString());
            preparedStatement.setBoolean(5, user.isActive());
            preparedStatement.setString(6, user.getPassword());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                user.setUserId((long) resultSet.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return user;
    }

    @Override
    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = findUserById(id);
        if (existingUser == null) return null;

        String name = userDTO.getName() != null ? userDTO.getName() : existingUser.getName();
        String lastName = userDTO.getLastName() != null ? userDTO.getLastName() : existingUser.getLastName();
        String email = userDTO.getEmail() != null ? userDTO.getEmail() : existingUser.getEmail();
        UserType type = userDTO.getType() != null ? userDTO.getType() : existingUser.getType();
        boolean active = userDTO.getActive() != null ? userDTO.getActive() : existingUser.isActive();

        String password;
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            password = DigestUtils.sha256Hex(userDTO.getPassword());
        } else {
            password = existingUser.getPassword();
        }

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();

            String sql = "UPDATE users SET name = ?, last_name = ?, email = ?, type = ?, active = ?, password = ? WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, type.toString());
            preparedStatement.setBoolean(5, active);
            preparedStatement.setString(6, password);
            preparedStatement.setLong(7, id);

            int updated = preparedStatement.executeUpdate();
            if (updated == 0) return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return findUserById(id); // return updated version
    }


    @Override
    public void delete(User user) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();

            String sql = "DELETE FROM users WHERE user_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, user.getUserId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
    }


    @Override
    public List<User> getAllUsers(Integer page, Integer limit) {
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;

        int offset = (page - 1) * limit;
        List<User> users = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            statement = connection.prepareStatement("SELECT * FROM users LIMIT ? OFFSET ?");
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                UserType role = null;
                if (resultSet.getString("type").equalsIgnoreCase("ADMIN")) {
                    role = UserType.ADMIN;
                }else {
                    role = UserType.EVENT_CREATOR;
                }

                users.add(new User(resultSet.getLong("user_id"), resultSet.getString("email"), resultSet.getString("name"), resultSet.getString("last_name"), role, resultSet.getString("password"), resultSet.getBoolean("active")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return users;
    }

    @Override
    public User findUserById(long id) {
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

    @Override
    public User findUserByEmail(String email) {
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

}
