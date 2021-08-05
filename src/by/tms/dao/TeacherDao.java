package by.tms.dao;

import by.tms.entity.Discipline;
import by.tms.entity.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeacherDao {
    private final Connection connection;

    public TeacherDao(Connection connection) throws SQLException {
        this.connection = connection;
        try {
            connection.prepareStatement("set search_path to university");
        } catch (SQLException throwables) {
            throw new SQLException("Failed to set search_path");
        }
    }

    public void save(Teacher teacher){
        String name = teacher.getName();
        String username = teacher.getUsername();
        String password = teacher.getPassword();
        List<Discipline> disc = teacher.getDisc();

        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into teachers values (default, ?, ?, ?) returning id;");
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int teacherId = resultSet.getInt(1);

            PreparedStatement preparedStatement1 = connection.prepareStatement("insert into teach_disc values (?, ?)");
            for (Discipline discipline : disc) {
                preparedStatement1.setInt(1, teacherId);
                preparedStatement1.setInt(2, discipline.getId());
            }

            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throwables.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void updateName(int id, String newName){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update teachers set name = ? where id = ?");
            preparedStatement.setString(1, newName);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean exists(String username){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select exists(select * from teachers where username=?)");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public void deleteById(int id){
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("delete from teachers where id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            PreparedStatement preparedStatement1 = connection.prepareStatement("delete from teach_disc where teach_id=?");
            preparedStatement1.setInt(1, id);
            preparedStatement1.execute();
            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(false);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public List<Teacher> getAll() {
        List<Teacher> teachers = new ArrayList<>();

        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("select * from teachers");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String username = resultSet.getString(3);
                String password = resultSet.getString(4);

                PreparedStatement preparedStatement1 = connection.prepareStatement(
                        "select d.* from disciplines d" +
                        "    join (select disc_id from teach_disc where teach_id="+id+") t on d.id=t.disc_id");
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                List<Discipline> userDisciplines = new ArrayList<>();
                while (resultSet1.next()){
                    int discId = resultSet1.getInt(1);
                    String discName = resultSet1.getString(1);
                    userDisciplines.add(new Discipline(discId, discName, List.of()));
                }

                Teacher teacher = new Teacher(id, name, username, password, userDisciplines);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return teachers;
    }

}
