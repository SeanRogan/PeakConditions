package com.capstone.D424.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;


@Entity
@Builder
@Table(name = "mountain_peak")
@Getter
@Setter
@AllArgsConstructor

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "peakId" // This is the primary key field of MountainPeak
)
public class MountainPeak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "peak_id", unique = true, nullable = false, updatable = false)
    private Long peakId;
    @Column(name = "peak_name", nullable = false)
    private String peakName;
    @Column(name = "homerange_id")
    private Long homerangeId;
    @Column(name = "uri", nullable = false)
    private String uri;
    @CreationTimestamp
    @Column(name = "create_date")
    private Date create_date;
    @UpdateTimestamp
    @Column(name = "last_update")
    private Date last_update;
    @ManyToOne
    @JoinColumn(name = "subrange_id")
    @JsonBackReference("subrange-peaks")
    private MountainSubRange homeSubrange;
    @ManyToOne
    @JsonBackReference("homerange-peaks")
    @JoinColumn(name = "range_id")
    private MountainRange homeRange;
    @OneToMany(mappedBy = "mountainPeak", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("peak-report")
    private Set<WeatherReport> weatherReports;

    @ManyToMany(mappedBy = "favoritePeaks")
    //@JsonBackReference("profile-peaks")
    private Set<UserProfile> users;

    public MountainPeak() {
    }

    public MountainPeak(String peakName, String uri) {
        this.peakName = peakName;
        this.uri = uri;
    }
    public void setHomeRange(MountainRange homeRange) {
        this.homeRange = homeRange;
        this.homerangeId = homeRange.getRangeId();
    }
}
