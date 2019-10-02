package com.pineapple.weatherapp.weatherapp.model;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="env")
@Data
public class EnvModel {
    private String wheatherapiid;

}
