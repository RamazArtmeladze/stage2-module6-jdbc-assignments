package jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String CREATE_USER_SQL = "INSERT INTO myusers(firstname, lastname, age) VALUES (?,?,?);";
    private static final String UPDATE_USER_SQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?;";
    private static final String DELETE_USER = "DELETE FROM myusers WHERE id = ?;";
    private static final String FIND_USER_BY_ID_SQL = "SELECT * FROM myusers WHERE id = ?;";
    private static final String FIND_USER_BY_NAME_SQL = "SELECT * FROM myusers WHERE firstname = ?;";
    private static final String FIND_ALL_USER_SQL = "SELECT * FROM myusers;";

    public Long createUser(User user) {
        Long userId = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(CREATE_USER_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.execute();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    userId = rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    public User findUserById(Long userId) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_USER_BY_ID_SQL)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong(1);
                    String firstname = rs.getString(2);
                    String lastname = rs.getString(3);
                    int age = rs.getInt(4);
                    user = new User(id, firstname, lastname, age);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(FIND_USER_BY_NAME_SQL)) {
            ps.setString(1, userName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Long id = rs.getLong(1);
                    String firstname = rs.getString(2);
                    String lastname = rs.getString(3);
                    int age = rs.getInt(4);
                    user = new User(id, firstname, lastname, age);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL_USER_SQL)) {
            while (rs.next()) {
                Long id = rs.getLong(1);
                String firstname = rs.getString(2);
                String lastname = rs.getString(3);
                int age = rs.getInt(4);
                users.add(new User(id, firstname, lastname, age));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public User updateUser(User user) {
        try (Connection connection = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_USER_SQL)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return findUserById(user.getId());
    }

    public void deleteUser(Long userId) {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_USER)) {
            ps.setLong(1, userId);
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}