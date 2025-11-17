package com.vivek.bookms.mapper;

import com.vivek.bookms.dto.BookDTO;
import com.vivek.bookms.entity.Book;

/**
 * Book mapper interface extending generic IMapper
 */
public interface IBookMapper extends IMapper<Book, BookDTO> {
    // Inherits all methods from IMapper<Book, BookDTO>
}