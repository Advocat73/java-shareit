package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Comment fromCommentDto(CommentDto commentDto, User commentator, Item item) {
        if (commentDto == null)
            return null;
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setAuthor(commentator);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null)
            return null;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreatedDate());
        return commentDto;
    }

    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment comment : comments)
            dtos.add(toCommentDto(comment));
        return dtos;
    }
}
