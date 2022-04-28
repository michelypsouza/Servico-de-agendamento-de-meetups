package com.womakerscode.microservicemeetups.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
public class Event {

    @Id
    @Column(name = "event_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(name = "code_event")
//    private String codeEvent;

    @Column
    private String title;

    @Column
    private String description;

    @Column(name = "start_event")
    private Date eventStart;

    @Column(name = "end_event")
    private Date eventEnd;

    @Column(name = "organizer_id")
    private Long organizerId;

    @OneToMany
    private List<Registration> registrations;

}
