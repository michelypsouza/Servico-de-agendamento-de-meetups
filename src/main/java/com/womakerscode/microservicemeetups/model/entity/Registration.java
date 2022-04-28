package com.womakerscode.microservicemeetups.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

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
    private Long id;

    // badge / name tag
    @Column(name = "name_tag")
    private String nameTag;

    @Column(name = "date_of_registration")
    private Date dateOfRegistration;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    // participant user
    @Column(name = "participant_id")
    private Long participantId;

}
