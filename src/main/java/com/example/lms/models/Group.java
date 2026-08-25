package com.example.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "groups")
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class Group extends BaseEntity {
    @Column(name = "name",nullable = false, unique = true, length = 255)
    private String name;

    @ManyToMany(mappedBy = "groups")
    private Set<Student> students = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "group_course",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses = new HashSet<>();

    public void addStudents(Student student) {
        students.add(student);
        student.getGroups().add(this);
    }

    public void removeStudents(Student student) {
        students.remove(student);
        student.getGroups().remove(this);
    }
}
