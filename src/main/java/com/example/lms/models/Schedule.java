package com.example.lms.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "schedules")
@NoArgsConstructor
@SQLRestriction("deleted = false")
public class Schedule extends BaseEntity {

    @NotNull(message = "Группа обязательна для заполнения")
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @NotNull(message = "Время начала обязательно для заполнения")
    @Column(name = "start_time",  nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "Время окончания занятий обязательно для заполнения")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

}
