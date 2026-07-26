package com.example.lms.models;

import jakarta.annotation.Nullable;
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
@Table(name = "students")
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class Student extends BaseEntity{

    @Column(name = "first_name", length = 255, nullable = false)
    private String firstName;

    @Column(name = "last_name",  length = 255, nullable = false)
    private String lastName;

    @ManyToMany
    @Column(nullable = false)
    @JoinTable(
            name = "student_group",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups = new HashSet<>();
}
