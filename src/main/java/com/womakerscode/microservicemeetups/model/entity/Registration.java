package com.womakerscode.microservicemeetups.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
    private Long id;

//    @Column
//    private String description;

    //badge
    @Column(name = "name_tag")
    private String nameTag;

    @Column(name = "date_of_registration")
    private Date dateOfRegistration;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @JoinColumn(name = "participant_id")
    private Long participantId;

}
