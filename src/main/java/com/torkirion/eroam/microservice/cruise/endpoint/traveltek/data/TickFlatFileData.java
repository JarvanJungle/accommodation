package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "traveltek_tick_flat_file")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TickFlatFileData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Date createDate;
}
