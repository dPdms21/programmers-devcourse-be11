package com.example.weatherapi.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Body {
    private Items items;
    private int pageNo;
    private int numOfRows;
    private int totalCount;
}
