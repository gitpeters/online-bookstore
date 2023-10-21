package com.peters.bookservice.dto;

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
