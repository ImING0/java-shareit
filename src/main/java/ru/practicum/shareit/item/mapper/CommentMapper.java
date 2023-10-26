package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {

    public CommentDtoOut toCommentDtoOut(Comment comment) {
        return CommentDtoOut.builder().id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated().toString())
                .build();
    }
}
