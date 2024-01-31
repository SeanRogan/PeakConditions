package com.capstone.D424.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "weather_report")
public class WeatherReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weather_report_id", nullable = false, insertable = false, updatable = false)
    private Long weatherReportId;

    @Column(name = "extended")
    private boolean extendedReport;

    @Column(columnDefinition = "varchar", name = "weather_report_content", updatable = false)
    private String weatherReportContent;
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    private Date create_date;
    @UpdateTimestamp
    @Column(name = "last_update", nullable = false, updatable = false)
    private Date last_update;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peak_id", referencedColumnName = "peak_id")
    @JsonBackReference("peak-report")
    private MountainPeak mountainPeak;
}
