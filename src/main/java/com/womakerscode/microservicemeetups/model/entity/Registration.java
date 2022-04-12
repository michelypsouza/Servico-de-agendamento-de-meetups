package com.womakerscode.microservicemeetups.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Registration {

    @Id
    @Column(name = "registration_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "person_name")
    private String name;

    //@JsonFormat(pattern = DateUtil.DATE_PATTERN_DEFAULT)
    @Column(name = "date_of_registration")
    private LocalDate dateOfRegistration;

    @Column
    private String registration;

    @OneToMany(mappedBy = "registration")
    private List<Meetup> meetups;

}
