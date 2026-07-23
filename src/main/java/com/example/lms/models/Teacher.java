package com.example.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@Table(name = "teachers")
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class Teacher extends BaseEntity{
    @Column(name = "first_name", length = 255)
    private String firstName;

    @Column(name = "last_name",  length = 255)
    private String lastName;
}
