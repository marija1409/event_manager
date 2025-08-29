package org.example.eventsbooker.repositories.category;

import org.example.eventsbooker.entites.Category;
import org.example.eventsbooker.entites.dtos.CategoryDTO;
import org.example.eventsbooker.repositories.MySqlAbstractRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlCategoryRepository extends MySqlAbstractRepository implements CategoryRepository {

    private boolean hasEvent(Long categoryId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean exists = false;

        try {
            connection = this.newConnection();

            String sql = "SELECT 1 FROM events WHERE category = ? LIMIT 1";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, categoryId);

            resultSet = preparedStatement.executeQuery();

            // If resultSet has at least one row, event exists
            exists = resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeResultSet(resultSet);
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return exists;
    }



    @Override
    public Category addCategory(Category category) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            String[] generatedColumns = {"category_id"};

            preparedStatement = connection.prepareStatement("INSERT INTO categories (name, description) VALUES(?, ?)", generatedColumns);
            preparedStatement.setString(1, category.getName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();

            if (resultSet.next()) {
                category.setCategoryId((long) resultSet.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(preparedStatement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }

        return category;
    }

    @Override
    public Category findCategoryByName(String name) {
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

    @Override
    public Category findCategoryById(Long id) {
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

    @Override
    public boolean deleteCategory(Category category) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        Long id = category.getCategoryId();

        if(hasEvent(id)) {
            return false;
        }

        try {
            connection = this.newConnection();

            String sql = "DELETE FROM categories WHERE category_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, category.getCategoryId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }
        return true;
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = findCategoryById(id);
        if (existingCategory == null) return null;

        String name = categoryDTO.getName() != null ? categoryDTO.getName() : existingCategory.getName();
        String description = categoryDTO.getDescription() != null ? categoryDTO.getDescription() : existingCategory.getDescription();

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.newConnection();

            String sql = "UPDATE categories SET name = ?, description = ? WHERE category_id = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setLong(3, id);

            int updated = preparedStatement.executeUpdate();
            if (updated == 0) return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            this.closeStatement(preparedStatement);
            this.closeConnection(connection);
        }

        return findCategoryById(id); // return updated version
    }

    @Override
    public List<Category> getAllCategories(Integer page, Integer limit) {
        if (page == null || page < 1) page = 1;
        if (limit == null || limit < 1) limit = 10;

        int offset = (page - 1) * limit;
        List<Category> categories = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = this.newConnection();

            statement = connection.prepareStatement("SELECT * FROM categories LIMIT ? OFFSET ?");
            statement.setInt(1, limit);
            statement.setInt(2, offset);

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                categories.add(new Category(resultSet.getLong("category_id"), resultSet.getString("name"), resultSet.getString("description")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.closeStatement(statement);
            this.closeResultSet(resultSet);
            this.closeConnection(connection);
        }
        return categories;
    }
}
