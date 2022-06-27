package com.torkirion.eroam.microservice.cruise.endpoint.traveltek.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "traveltek_sid")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SidData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true)
    private Integer sid;
    private String site;
    private String username;
    private String password;
    private String currency;
    @Column(unique = true)
    private String country;
    @Column(unique = true)
    private String countryCode;
}
