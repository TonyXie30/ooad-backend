package com.dormitory.backend.service;

import com.dormitory.backend.api.CommentRepository;
import com.dormitory.backend.pojo.CommentResponseDTO;
import com.dormitory.backend.pojo.comment;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentRepository commentRepository;

    public comment findById(int id){
        return commentRepository.findById(id);
    }
    public List<comment> getAllChildComments(comment parent){
        List<comment> comments = commentRepository.findByParent(parent);
        List<comment> result = new ArrayList<>();
        for (comment comment : comments) {
            if (!result.contains(comment))
                result.add(comment);
            if (comment!=parent)
                result.addAll(getAllChildComments(comment));
        }
        return result;
    }

    @Transactional
    public void deleteCommentAndChildrenRecursively(comment comment) {
        List<comment> children = commentRepository.findByParent(comment);
        for (comment child : children) {
            if (child!=comment)
                deleteCommentAndChildrenRecursively(child);
        }
        commentRepository.delete(comment);
    }
    public CommentResponseDTO convertToDTO(comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setAuthor(comment.getUser().getUsername());
        dto.setContent(comment.getContent());
        dto.setCreate_time(comment.getCreate_time());
        dto.setId(comment.getId());
        dto.setParent_id(comment.getParent().getId());
        List<CommentResponseDTO> replies = getAllChildComments(comment)
                .stream()
                .filter(childComment -> !childComment.equals(comment))
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        dto.setReplies(replies);

        return dto;
    }
}
