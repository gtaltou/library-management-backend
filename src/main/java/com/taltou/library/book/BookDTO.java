package com.taltou.library.book;

import com.taltou.library.category.CategoryDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDate;



@ApiModel(value = "Book Model")
public class BookDTO implements Comparable<BookDTO>{

    @ApiModelProperty(value = "Book id")
    private Integer id;

    @ApiModelProperty(value = "Book title")
    private String title;

    @ApiModelProperty(value = "Book isbn")
    private String isbn;

    @ApiModelProperty(value = "Book release date by the editor")
    private LocalDate releaseDate;

    @ApiModelProperty(value = "Book register date in the library")
    private LocalDate registerDate;

    @ApiModelProperty(value = "Book total examplaries")
    private Integer totalExamplaries;

    @ApiModelProperty(value = "Book author")
    private String author;

    @ApiModelProperty(value = "Book category")
    private CategoryDTO category;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getTotalExamplaries() {
        return totalExamplaries;
    }

    public void setTotalExamplaries(Integer totalExamplaries) {
        this.totalExamplaries = totalExamplaries;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @Override
    public int compareTo(BookDTO o) {
        return title.compareToIgnoreCase(o.getTitle());
    }


}
