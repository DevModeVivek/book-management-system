package com.vivek.bookservice.mapper;

import com.vivek.bookservice.dto.BookDTO;
import com.vivek.bookservice.entity.Book;
import com.vivek.commons.mapper.IMapper;

/**
 * Streamlined Book mapper interface
 */
public interface IBookMapper extends IMapper<Book, BookDTO> {
}