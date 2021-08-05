package by.tms.entity;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Discipline {
    private int id;
    private String name;
    private List<Teacher> teachers;

    public Discipline() {
    }

    public Discipline(String name, List<Teacher> teachers) {
        this.name = name;
        this.teachers = teachers;
    }

    public Discipline(int id, String name, List<Teacher> teachers) {
        this.id = id;
        this.name = name;
        this.teachers = teachers;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discipline that = (Discipline) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                compareTeachers(teachers, that.getTeachers());
    }

    private boolean compareTeachers(List<Teacher> thisTeachers, List<Teacher> anotherTeachers){
        List<String> thisTeachersNames = getTeachersNames(thisTeachers);
        List<String> anotherTeachersNames = getTeachersNames(anotherTeachers);
        return thisTeachersNames.containsAll(anotherTeachersNames) && thisTeachers.size()==anotherTeachers.size();
    }

    private List<String> getTeachersNames(List<Teacher> teachers){
        return teachers.stream().map(t -> {
                int id = t.getId();
                String name = t.getName();
                String username = t.getUsername();
                return String.format("%d %s %s", id, name, username);
            }).collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, getTeachersNames(teachers));
    }
}
