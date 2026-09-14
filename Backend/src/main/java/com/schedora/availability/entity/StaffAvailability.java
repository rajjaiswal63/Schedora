package com.schedora.availability.entity;

import com.schedora.branch.entity.Branch;
import com.schedora.common.persistence.AuditableEntity;
import com.schedora.salon.entity.Salon;
import com.schedora.staff.entity.Staff;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(
        name = "staff_availability",
        uniqueConstraints = @UniqueConstraint(name = "uk_staff_availability_staff_day", columnNames = {"staff_id", "day_of_week"}),
        indexes = @Index(name = "idx_staff_availability_salon", columnList = "salon_id")
)
public class StaffAvailability extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salon_id", nullable = false)
    private Salon salon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    private LocalTime availableFrom;

    private LocalTime availableTo;

    @Column(nullable = false)
    private boolean unavailable;

    protected StaffAvailability() {
    }

    public StaffAvailability(Salon salon, Branch branch, Staff staff, DayOfWeek dayOfWeek,
                             LocalTime availableFrom, LocalTime availableTo, boolean unavailable) {
        this.salon = salon;
        this.branch = branch;
        this.staff = staff;
        this.dayOfWeek = dayOfWeek;
        this.availableFrom = availableFrom;
        this.availableTo = availableTo;
        this.unavailable = unavailable;
    }

    public Long getId() {
        return id;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalTime availableFrom) {
        this.availableFrom = availableFrom;
    }

    public LocalTime getAvailableTo() {
        return availableTo;
    }

    public void setAvailableTo(LocalTime availableTo) {
        this.availableTo = availableTo;
    }

    public boolean isUnavailable() {
        return unavailable;
    }

    public void setUnavailable(boolean unavailable) {
        this.unavailable = unavailable;
    }
}
