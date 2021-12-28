package com.taltou.library.book;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.taltou.library.category.Category;
import com.taltou.library.category.CategoryDTO;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/rest/book/api")
@Api(value = "Book Rest Controller: contains all operations for managing books")
public class BookRestController {

    public static final Logger LOGGER = LoggerFactory.getLogger(BookRestController.class);

    @Autowired
    private BookServiceImpl bookService;

    @PostMapping("/addBook")
    @ApiOperation(value = "Add a new Book in the Library", response = BookDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 409, message = "Conflict: the book already exist"),
            @ApiResponse(code = 201, message = "Created: the book is successfully inserted"),
            @ApiResponse(code = 304, message = "Not Modified: the book is unsuccessfully inserted") })
    public ResponseEntity<BookDTO> createNewBook(@RequestBody BookDTO bookDTORequest) {
        //, UriComponentsBuilder uriComponentBuilder
        Book existingBook = bookService.findBookByIsbn(bookDTORequest.getIsbn());
        if (existingBook != null) {
            return new ResponseEntity<BookDTO>(HttpStatus.CONFLICT);
        }
        Book bookRequest = mapBookDTOToBook(bookDTORequest);
        Book book = bookService.saveBook(bookRequest);
        if (book != null && book.getId() != null) {
            BookDTO bookDTO = mapBookToBookDTO(book);
            return new ResponseEntity<BookDTO>(bookDTO, HttpStatus.CREATED);
        }
        return new ResponseEntity<BookDTO>(HttpStatus.NOT_MODIFIED);

    }

    @PutMapping("/updateBook")
    @ApiOperation(value = "Update/Modify an existing Book in the Library", response = BookDTO.class)
    @ApiResponses(value = { @ApiResponse(code = 404, message = "Not Found : the book does not exist"),
            @ApiResponse(code = 200, message = "Ok: the book is successfully updated"),
            @ApiResponse(code = 304, message = "Not Modified: the book is unsuccessfully updated") })
    public ResponseEntity<BookDTO> updateBook(@RequestBody BookDTO bookDTORequest) {
        //, UriComponentsBuilder uriComponentBuilder
        if (!bookService.checkIfIdExists(bookDTORequest.getId())) {
            return new ResponseEntity<BookDTO>(HttpStatus.NOT_FOUND);
        }
        Book bookRequest = mapBookDTOToBook(bookDTORequest);
        Book book = bookService.updateBook(bookRequest);
        if (book != null) {
            BookDTO bookDTO = mapBookToBookDTO(book);
            return new ResponseEntity<BookDTO>(bookDTO, HttpStatus.OK);
        }
        return new ResponseEntity<BookDTO>(HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping("/deleteBook/{bookId}")
    @ApiOperation(value = "Delete a Book in the Library, if the book does not exist, nothing is done", response = String.class)
    @ApiResponse(code = 204, message = "No Content: Book sucessfully deleted")
    public ResponseEntity<String> deleteBook(@PathVariable Integer bookId) {
        bookService.deleteBook(bookId);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/searchByTitle")
    @ApiOperation(value="Search Books in the Library by title", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfull research"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<List<BookDTO>> searchBookByTitle(@RequestParam("title") String title,
                                                           UriComponentsBuilder uriComponentBuilder) {
        List<Book> books = bookService.findBooksByTitleOrPartTitle(title);
        if (!CollectionUtils.isEmpty(books)) {
            // on retire tous les élts null que peut contenir cette liste => pour éviter les
            // NPE par la suite
            books.removeAll(Collections.singleton(null));
            List<BookDTO> bookDTOs = books.stream().map(book -> {
                return mapBookToBookDTO(book);
            }).collect(Collectors.toList());
            return new ResponseEntity<List<BookDTO>>(bookDTOs, HttpStatus.OK);
        }
        return new ResponseEntity<List<BookDTO>>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/searchByIsbn")
    @ApiOperation(value="Search a Book in the Library by its isbn", response = BookDTO.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfull research"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<BookDTO> searchBookByIsbn(@RequestParam("isbn") String isbn,
                                                    UriComponentsBuilder uriComponentBuilder) {
        Book book = bookService.findBookByIsbn(isbn);
        if (book != null) {
            BookDTO bookDTO = mapBookToBookDTO(book);
            return new ResponseEntity<BookDTO>(bookDTO, HttpStatus.OK);
        }
        return new ResponseEntity<BookDTO>(HttpStatus.NO_CONTENT);
    }

    /**
     * Transforme un Book en BookDTO
     *
     * @param book
     * @return
     */
    private BookDTO mapBookToBookDTO(Book book) {
        ModelMapper mapper = new ModelMapper();
        BookDTO bookDTO = mapper.map(book, BookDTO.class);
        if (book.getCategory() != null) {
            bookDTO.setCategory(new CategoryDTO(book.getCategory().getCode(), book.getCategory().getLabel()));
        }
        return bookDTO;
    }

    /**
     * Transforme un BookDTO en Book
     *
     * @param bookDTO
     * @return
     */
    private Book mapBookDTOToBook(BookDTO bookDTO) {
        ModelMapper mapper = new ModelMapper();
        Book book = mapper.map(bookDTO, Book.class);
        book.setCategory(new Category(bookDTO.getCategory().getCode(), ""));
        book.setRegisterDate(LocalDate.now());
        return book;
    }

}

