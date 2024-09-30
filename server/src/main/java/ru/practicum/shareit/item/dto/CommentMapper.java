package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;

@UtilityClass
public class CommentMapper {
    public CommentResponse toCommentResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
    }

    public Comment toComment(CommentRequest request, Item item, User author) {
        final Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        return comment;
    }
}
