package kz.iitu.springlab.catalog;

public record BookPatchRequest(
        String title,
        String author,
        Integer year
) {
}