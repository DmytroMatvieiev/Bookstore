package org.dmdev.bookstore.mapper;

import org.dmdev.bookstore.domain.Book;
import org.dmdev.bookstore.domain.BookFile;
import org.dmdev.bookstore.dto.BookFileDTO;
import org.springframework.stereotype.Component;

@Component
public class BookFileMapper {

    public BookFile toBookFile(BookFileDTO dto) {
        return BookFile.builder()
                .id(dto.id())
                .bookId(dto.bookId())
                .format(dto.format())
                .filePath(dto.filepath())
                .build();
    }

    public BookFileDTO toBookFileDTO(BookFile file) {
        return BookFileDTO.builder()
                .id(file.getId())
                .bookId(file.getBookId())
                .format(file.getFormat())
                .filepath(file.getFilePath())
                .build();
    }
}
