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

}
