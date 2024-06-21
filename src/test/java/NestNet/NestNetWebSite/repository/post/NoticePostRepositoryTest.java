package NestNet.NestNetWebSite.repository.post;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.notice.NoticePost;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class NoticePostRepositoryTest extends TestSupport {

    @Autowired
    private NoticePostRepository noticePostRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown(){
        noticePostRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("공지사항 게시물 PK를 통해 공지사항 게시물을 단건 조회한다.")
    @Test
    void findById() {
        // given
        Member member = memberRepository.save(new Member());

        NoticePost post = noticePostRepository.save(
                createPost("test", "test", member, LocalDateTime.of(2024, 6, 12, 10, 0))
        );

        // when
        Optional<NoticePost> findPost = noticePostRepository.findById(post.getId());

        // then
        Assertions.assertThat(findPost).isPresent();
        Assertions.assertThat(findPost.get().getTitle()).isEqualTo("test");
    }

    @DisplayName("공지사항 게시물을 페이징을 통해 조회한다.")
    @Test
    void findAll() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        NoticePost post1 = createPost("test1", "test1", member, createdTime);
        NoticePost post2 = createPost("test2", "test2", member, createdTime);
        NoticePost post3 = createPost("test3", "test3", member, createdTime);

        noticePostRepository.saveAll(List.of(post1, post2, post3));

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "id"));

        // when
        List<NoticePost> noticePostList = noticePostRepository.findAll(pageRequest).getContent();

        // then
        Assertions.assertThat(noticePostList).hasSize(2)
                .extracting("title")
                .containsExactly("test3", "test2");
    }

    private static NoticePost createPost(String title, String bodyContent, Member member, LocalDateTime createdTime){
        return NoticePost.builder()
                .title(title)
                .bodyContent(bodyContent)
                .member(member)
                .viewCount(0L)
                .recommendationCount(0)
                .createdTime(createdTime)
                .build();
    }

}