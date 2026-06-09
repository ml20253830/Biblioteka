package com.projekat.biblioteka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryResponse {

    private String title;

    @JsonProperty("number_of_pages")
    private Integer numberOfPages;

    private List<AuthorEntry> authors;
    private List<DescriptionEntry> subjects;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AuthorEntry {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DescriptionEntry {
        private String name;
    }

    public String getFirstAuthor() {
        if (authors != null && !authors.isEmpty()) {
            return authors.get(0).getName();
        }
        return "Nepoznat autor";
    }
}
