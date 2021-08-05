package by.tms.dao;

import by.tms.entity.Discipline;
import by.tms.entity.Teacher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DisciplineDao {
    Connection connection;

    public DisciplineDao(Connection connection) {
        this.connection = connection;
    }

    public void saveDiscipline(Discipline discipline){
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("insert into disciplines values (default, ?) returning id;");
            preparedStatement.setString(1, discipline.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            int disciplineId = resultSet.getInt(1);
            for (Teacher teacher : discipline.getTeachers()) {
                PreparedStatement preparedStatement1 = connection.prepareStatement("insert into teach_disc values (? ,?);");
                preparedStatement1.setInt(1, teacher.getId());
                preparedStatement1.setInt(2, disciplineId);
                preparedStatement1.execute();
            }
            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean exists(Discipline discipline){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select exists (select 1 from disciplines where id = ? )");
            preparedStatement.setInt(1, discipline.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getBoolean(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public void delete(int id){
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("delete from disciplines where id = ?");
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
            PreparedStatement preparedStatement1 = connection.prepareStatement("delete from teach_disc where disc_id = ?");
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
                connection.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public Optional<Discipline> getById(int id){
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("select * from disciplines where id=?");
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int discId = resultSet.getInt(1);
                String discName = resultSet.getString(2);
                List<Teacher> disciplineTeachers = new ArrayList<>();
                PreparedStatement preparedStatement1 = connection.prepareStatement("" +
                        "select * from teachers t" +
                        "join(select teach_id from teach_disc where teach_id =" + id + ") d on t.id=d.teach_id");
                ResultSet resultSet1 = preparedStatement1.executeQuery();
                while (resultSet1.next()){
                    int teacherId = resultSet1.getInt(1);
                    String teacherName = resultSet1.getString(2);
                    String teacherUsername = resultSet1.getString(3);
                    String teacherPassword = resultSet1.getString(4);
                    disciplineTeachers.add(new Teacher(teacherId, teacherName, teacherUsername, teacherPassword, List.of()));
                }
                Discipline discipline = new Discipline(discId, discName, disciplineTeachers);
                return Optional.of(discipline);
            } else {
                return Optional.empty();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Discipline> getByName(String name){
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select id from disciplines where name=?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int id = resultSet.getInt(1);
                return getById(id);
            } else {
                return Optional.empty();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Discipline> getAll(){
        List<Discipline> disciplines = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select id from disciplines");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt(1);
                Optional<Discipline> byId = getById(id);
                byId.ifPresent(disciplines::add);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return disciplines;
    }
}
