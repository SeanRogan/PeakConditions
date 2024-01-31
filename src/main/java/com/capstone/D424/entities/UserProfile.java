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
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
@Entity
@Builder
@Table(name="user_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "profileId" // This is the primary key field of MountainPeak
)
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="profile_id",updatable = false)
    private Long profileId;
    @Column(name = "max_temp")
    private int maxTemp;
    @Column(name = "min_temp")
    private int minTemp;
    @Column(name = "max_wind")
    private int maxWind;
    @Column(name = "prefer_light_rain")
    private boolean preferLightRain;
    @Column(name = "prefer_rain_showers")
    private boolean preferRainShowers;
    @Column(name = "prefer_mod_rain")
    private boolean preferModRain;
    @Column(name = "prefer_risk_tstorm")
    private boolean preferRiskTstorm;
    @Column(name = "prefer_light_snow")
    private boolean preferLightSnow;
    @Column(name = "prefer_snow_showers")
    private boolean preferSnowShowers;
    @Column(name = "prefer_heavy_snow")
    private boolean preferHeavySnow;
    @Column(name = "prefer_clear")
    private boolean preferClear;
    @Column(name = "prefer_some_clouds")
    private boolean preferSomeClouds;
    @Column(name = "prefer_cloudy")
    private boolean preferCloudy;
    @Column(name = "create_date")
    @CreationTimestamp
    private Date create_date;
    @Column(name = "last_update")
    @UpdateTimestamp
    private Date last_update;

    @OneToOne(mappedBy = "profile")
    @JsonBackReference("user-profile")
    private User profileOwner;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "user_favorite_peaks",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "peak_id"))
    //@JsonManagedReference("profile-peaks")
    private Set<MountainPeak> favoritePeaks;
}
