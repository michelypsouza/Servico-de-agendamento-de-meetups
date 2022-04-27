package com.womakerscode.microservicemeetups.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Registration {

    @Id
    @Column(name = "registration_id")
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column(name = "person_name")
    private String name;

    @Column(name = "date_of_registration")
    private Date dateOfRegistration;

    @Column
    private String registration;

    @OneToMany(mappedBy = "registration")
    private List<Meetup> meetups;

}
