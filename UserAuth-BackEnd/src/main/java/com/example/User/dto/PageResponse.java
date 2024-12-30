package com.example.User.dto;


import java.util.List;


/**
 * Représente une réponse paginée pour les requêtes de données.
 * <p>
 * Cette classe est utilisée pour encapsuler les informations relatives à une page de résultats,
 * y compris le contenu de la page, le numéro de la page, la taille de la page, le nombre total
 * d'éléments, le nombre total de pages, et les indicateurs de première et dernière page.
 * </p>
 *
 * @param <T> le type des éléments contenus dans la réponse paginée
 * @author  CHALABI Hossam
 */
public class PageResponse<T> {

    /**
     * Le contenu de la page, une liste d'objets de type {@code T}.
     */
    private List<T> content;

    /**
     * Le numéro de la page actuelle.
     */
    private int number;

    /**
     * La taille de la page, c'est-à-dire le nombre d'éléments par page.
     */
    private int size;

    /**
     * Le nombre total d'éléments disponibles.
     */
    private long totalElements;

    /**
     * Le nombre total de pages disponibles.
     */
    private int totalPages;

    /**
     * Indique si la page actuelle est la première page.
     */
    private boolean first;

    /**
     * Indique si la page actuelle est la dernière page.
     */
    private boolean last;

    public PageResponse(List<T> content, int number, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public PageResponse() {
    }

    public static <T> PageResponseBuilder<T> builder() {
        return new PageResponseBuilder<T>();
    }

    public List<T> getContent() {
        return this.content;
    }

    public int getNumber() {
        return this.number;
    }

    public int getSize() {
        return this.size;
    }

    public long getTotalElements() {
        return this.totalElements;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public boolean isFirst() {
        return this.first;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public static class PageResponseBuilder<T> {
        private List<T> content;
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;

        PageResponseBuilder() {
        }

        public PageResponseBuilder<T> content(List<T> content) {
            this.content = content;
            return this;
        }

        public PageResponseBuilder<T> number(int number) {
            this.number = number;
            return this;
        }

        public PageResponseBuilder<T> size(int size) {
            this.size = size;
            return this;
        }

        public PageResponseBuilder<T> totalElements(long totalElements) {
            this.totalElements = totalElements;
            return this;
        }

        public PageResponseBuilder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PageResponseBuilder<T> first(boolean first) {
            this.first = first;
            return this;
        }

        public PageResponseBuilder<T> last(boolean last) {
            this.last = last;
            return this;
        }

        public PageResponse<T> build() {
            return new PageResponse<T>(this.content, this.number, this.size, this.totalElements, this.totalPages, this.first, this.last);
        }

        public String toString() {
            return "PageResponse.PageResponseBuilder(content=" + this.content + ", number=" + this.number + ", size=" + this.size + ", totalElements=" + this.totalElements + ", totalPages=" + this.totalPages + ", first=" + this.first + ", last=" + this.last + ")";
        }
    }
}