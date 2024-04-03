package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Coment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {
    public static Coment fromCommentDto(CommentDto commentDto, User commentator, Item item) {
        if (commentDto == null)
            return null;
        Coment comment = new Coment();
        comment.setText(commentDto.getText());
        comment.setAuthor(commentator);
        comment.setItem(item);
        return comment;
    }

    public static CommentDto toCommentDto(Coment comment) {
        if (comment == null)
            return null;
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreatedDate());
        return commentDto;
    }

    public static List<CommentDto> toCommentDto(List<Coment> comments) {
        List<CommentDto> dtos = new ArrayList<>();
        for (Coment comment : comments)
            dtos.add(toCommentDto(comment));
        return dtos;
    }
}
