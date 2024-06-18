package NestNet.NestNetWebSite.domain.comment;

import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 댓글 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;                                                // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;                                              // 댓글이 포함된 게시물

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;                                          // 댓글 작성한 회원

    @Column(columnDefinition = "TEXT")
    private String content;                                         // 댓글 내용

    private LocalDateTime createdTime;                              // 댓글 쓴 시각

    private LocalDateTime modifiedTime;                             // 댓글 수정한 시각

    /*
    생성자
     */
    @Builder
    public Comment(Post post, Member member, String content, LocalDateTime createdTime, LocalDateTime modifiedTime){
        this.post = post;
        this.member = member;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    //== setter ==//
    public void injectPost(Post post){
        this.post = post;
    }

    //== 비지니스 로직 ==//
    /*
    댓글 수정
     */
    public void modifyContent(String content) {

        this.content = content;
        this.modifiedTime = LocalDateTime.now();
    }


}
