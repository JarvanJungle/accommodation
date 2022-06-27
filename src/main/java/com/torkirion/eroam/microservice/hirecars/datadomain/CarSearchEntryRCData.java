package com.torkirion.eroam.microservice.hirecars.datadomain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "carsearchentryrc")
public class CarSearchEntryRCData {
    @Id
    @Column(name = "vehicle_id", length = 128)
    private String vehicleId;

    @Column(name = "car_search_entry_json", columnDefinition = "TEXT")
    private String carSearchEntryJson;

    @Column(name = "date_created", columnDefinition = "date")
    private Date dateCreated;
}
