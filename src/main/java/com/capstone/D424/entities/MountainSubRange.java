package com.capstone.D424.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.Set;
@Entity
@Table(name="mountain_sub_range")
@Getter
@Setter
@AllArgsConstructor
public class MountainSubRange {

    @Id //for primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subrange_id",unique=true, nullable = false, updatable = false)
    private Long subrangeId;
    @Column(name = "home_range_uri")
    private String homeRangeUri;
    @Column(name = "range_name", nullable = false)
    private String rangeName;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "create_date")
    @CreationTimestamp
    private Date create_date;
    @Column(name = "last_update")
    @UpdateTimestamp
    private Date last_update;
    @JsonManagedReference("subrange-peaks")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "homeSubrange")
    private Set<MountainPeak> peaks;

    @ManyToOne
    @JoinColumn(name="range_id")
    @JsonBackReference("subrange-homerange")
    private MountainRange homeRange;

    public MountainSubRange(String rangeName, String uri, String homeRangeUri)  {
        this.rangeName = rangeName;
        this.uri = uri;
        this.homeRangeUri = homeRangeUri;

    }
    public MountainSubRange() {
    }
}
