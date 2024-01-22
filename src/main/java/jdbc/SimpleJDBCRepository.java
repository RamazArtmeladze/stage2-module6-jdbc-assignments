package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
    private static final String deleteUserSQL = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ? OR lastname = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            // Handle exceptions
        }
        return null;
    }


    public User findUserById(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByIdSQL)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(rs.getLong("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getInt("age"));
            }
        } catch (SQLException e) {
            // Handle exceptions
        }
        return null;
    }


    public User findUserByName(String userName) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findUserByNameSQL)) {

            ps.setString(1, userName);
            ps.setString(2, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(rs.getLong("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getInt("age"));
            }
        } catch (SQLException e) {
            // Handle exceptions
        }
        return null;
    }


    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(findAllUserSQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(rs.getLong("id"), rs.getString("firstname"), rs.getString("lastname"), rs.getInt("age")));
            }
        } catch (SQLException e) {
            // Handle exceptions
        }
        return users;
    }


    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(updateUserSQL)) {

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return user;
            }
        } catch (SQLException e) {
            // Handle exceptions
        }
        return null;
    }


    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(deleteUserSQL)) {

            ps.setLong(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Handle exceptions
        }
    }

}
