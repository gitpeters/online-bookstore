package com.peters.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class BookRequest {
    private String title;
    private String author;
    private Double price;
    private LocalDate publishedDate;
}
