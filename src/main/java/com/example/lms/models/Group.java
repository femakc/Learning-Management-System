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

    private String name;

    @ManyToMany(mappedBy = "groups")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Student> students = new HashSet<>();

}
