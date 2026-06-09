package com.projekat.biblioteka.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String isbn;

    private String title;
    private String author;

    @Column(length = 2000)
    private String description;

    private Integer pageCount;
    private Integer availableCopies;
    private Integer totalCopies;
}
