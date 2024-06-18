package NestNet.NestNetWebSite.repository.comment;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.comment.Comment;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.Post;
import NestNet.NestNetWebSite.domain.post.notice.NoticePost;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import NestNet.NestNetWebSite.repository.post.NoticePostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

class CommentRepositoryTest extends TestSupport {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NoticePostRepository noticePostRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        commentRepository.deleteAllInBatch();
        noticePostRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("댓글 PK를 통해 댓글을 단건 조회한다.")
    @Test
    void findById() {
        // given
        NoticePost post = noticePostRepository.save(new NoticePost());
        Member member = memberRepository.save(new Member());

        Comment comment = createComment(post, member, "test", LocalDateTime.of(2024, 6, 12, 0, 0));

        Comment saveComment = commentRepository.save(comment);

        // when
        Optional<Comment> findComment = commentRepository.findById(saveComment.getId());

        // then
        Assertions.assertThat(findComment.get().getId()).isEqualTo(saveComment.getId());
        Assertions.assertThat(findComment.get().getContent()).isEqualTo("test");
    }

    private static Comment createComment(Post post, Member member, String content, LocalDateTime createdTime){
        return Comment.builder()
                .post(post)
                .member(member)
                .content(content)
                .createdTime(createdTime)
                .modifiedTime(null)
                .build();
    }
}