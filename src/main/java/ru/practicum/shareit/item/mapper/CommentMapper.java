package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    public CommentDtoOut toCommentDtoOut(Comment comment) {
        return CommentDtoOut.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem()
                        .getId())
                .authorId(comment.getAuthor()
                        .getId())
                .authorName(comment.getAuthor()
                        .getName())
                .created(comment.getCreated())
                .build();
    }
}
