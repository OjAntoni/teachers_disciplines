package by.tms.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Teacher {
    private int id;
    private String name;
    private String username;
    private String password;
    private List<Discipline> disc;

    public Teacher() {
    }

    public Teacher(String name, String username, String password, List<Discipline> disc) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.disc = disc;
    }

    public Teacher(int id, String name, String username, String password, List<Discipline> disc) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.disc = disc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Discipline> getDisc() {
        return disc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return id == teacher.id &&
                Objects.equals(name, teacher.name) &&
                Objects.equals(username, teacher.username) &&
                Objects.equals(password, teacher.password) &&
                compareDisciplines(disc, teacher.disc);
    }

    private boolean compareDisciplines(List<Discipline> thisDisc, List<Discipline> anotherDisc){
        List<String> thisDiscNames = getDisciplineNames(thisDisc);
        List<String> anotherDiscNames = getDisciplineNames(anotherDisc);
        return thisDiscNames.containsAll(anotherDiscNames) && thisDisc.size()==anotherDisc.size();
    }

    private List<String> getDisciplineNames(List<Discipline> disc){
        return disc.stream().map(Discipline::getName).collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, username, password, getDisciplineNames(disc));
    }


}
