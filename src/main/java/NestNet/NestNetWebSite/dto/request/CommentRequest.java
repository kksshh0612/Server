package NestNet.NestNetWebSite.dto.request;

import NestNet.NestNetWebSite.domain.comment.Comment;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentRequest {

    private String content;

    //== DTO ---> Entity ==//
    public Comment toEntity(Post post, Member member, LocalDateTime createdTime){

        return new Comment(post, member, this.content, createdTime, null);
    }
}
