package com.slack.persistence;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

/**
 * Created by sneha.dontireddy on 8/5/18.
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "events", schema = "DeploymentDb")
public class EventEntity {
    @Id
    @Column(name = "id")
    long id;

    @Column(name = "sha", nullable = false)
    String sha;

    @Column(name = "date")
    long date;

    @Column(name = "action")
    String action;

    @Column(name = "engineer")
    String engineer;
}
