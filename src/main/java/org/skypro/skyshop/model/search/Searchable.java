package org.skypro.skyshop.model.search;

import java.util.UUID;

public interface Searchable {

    String getSearchTerm();

    String getContentType();

    String getSearchableName();

    default String getStringRepresentation() {
        return getSearchableName() + " - " + getContentType();
    }

    UUID getId();
}
