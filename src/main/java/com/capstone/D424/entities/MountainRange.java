package com.capstone.D424.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name="mountain_range")
@Getter
@Setter
@AllArgsConstructor
public class MountainRange {
    @Id //for primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "range_id", unique=true, nullable = false, updatable = false)
    private Long rangeId;
    @Column(name = "range_name", unique=true, nullable = false)
    private String rangeName;
    @Column(name = "uri", unique=true, nullable = false)
    private String uri;
    @Column(name = "create_date")
    @CreationTimestamp
    private Date create_date;
    @Column(name = "last_update")
    @UpdateTimestamp
    private Date last_update;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "homeRange")
    @JsonManagedReference("subrange-homerange")
    private Set<MountainSubRange> subRanges;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL}, mappedBy = "homeRange")
    @JsonManagedReference("homerange-peaks")
    private Set<MountainPeak> peaks;

    public MountainRange(String rangeName, String uri) {
        this.rangeName = rangeName;
        this.uri = uri;
    }

    public MountainRange() {

    }
}
