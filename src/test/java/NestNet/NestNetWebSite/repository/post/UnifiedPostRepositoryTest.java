package NestNet.NestNetWebSite.repository.post;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.unified.UnifiedPost;
import NestNet.NestNetWebSite.domain.post.unified.UnifiedPostType;
import NestNet.NestNetWebSite.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

class UnifiedPostRepositoryTest extends TestSupport {

    @Autowired
    private UnifiedPostRepository unifiedPostRepository;

    @Autowired
    private MemberRepository memberRepository;

    @AfterEach
    void tearDown() {
        unifiedPostRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("통합 게시물 PK를 통해 통합 게시물을 단건 조회한다.")
    @Test
    void findById() {
        // given
        Member member = memberRepository.save(new Member());

        UnifiedPost post = unifiedPostRepository.save(createPost("test", UnifiedPostType.DEV, member));

        // when
        Optional<UnifiedPost> findPost = unifiedPostRepository.findById(post.getId());

        // then
        Assertions.assertThat(findPost).isPresent();
        Assertions.assertThat(findPost.get().getTitle()).isEqualTo("test");
        Assertions.assertThat(findPost.get().getUnifiedPostType()).isEqualTo(UnifiedPostType.DEV);
    }

    @DisplayName("타입(개발/자유/진로/취업정보)을 선택하지 않고 페이징을 통해 통합 게시물을 조회한다.")
    @Test
    void findByUnifiedPostTypeByPaging1() {
        // given
        Member member = memberRepository.save(new Member());

        UnifiedPost post1 = createPost("test1", UnifiedPostType.DEV, member);
        UnifiedPost post2 = createPost("test2", UnifiedPostType.DEV, member);
        UnifiedPost post3 = createPost("test3", UnifiedPostType.FREE, member);
        UnifiedPost post4 = createPost("test4", UnifiedPostType.FREE, member);
        UnifiedPost post5 = createPost("test5", UnifiedPostType.CAREER, member);
        UnifiedPost post6 = createPost("test6", UnifiedPostType.CAREER, member);
        UnifiedPost post7 = createPost("test7", UnifiedPostType.JOB_INFO, member);
        UnifiedPost post8 = createPost("test8", UnifiedPostType.JOB_INFO, member);

        unifiedPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8));

        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<UnifiedPost> findPostList = unifiedPostRepository.findByUnifiedPostTypeByPaging(null, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(5)
                .extracting("title", "unifiedPostType")
                .containsExactly(
                        Tuple.tuple("test1", UnifiedPostType.DEV),
                        Tuple.tuple("test2", UnifiedPostType.DEV),
                        Tuple.tuple("test3", UnifiedPostType.FREE),
                        Tuple.tuple("test4", UnifiedPostType.FREE),
                        Tuple.tuple("test5", UnifiedPostType.CAREER)
                );
    }

    @DisplayName("타입(개발)을 선택하여 페이징을 통해 통합 게시물을 조회한다.")
    @Test
    void findByUnifiedPostTypeByPaging2() {
        // given
        Member member = memberRepository.save(new Member());

        UnifiedPost post1 = createPost("test1", UnifiedPostType.DEV, member);
        UnifiedPost post2 = createPost("test2", UnifiedPostType.DEV, member);
        UnifiedPost post3 = createPost("test3", UnifiedPostType.FREE, member);
        UnifiedPost post4 = createPost("test4", UnifiedPostType.FREE, member);
        UnifiedPost post5 = createPost("test5", UnifiedPostType.CAREER, member);
        UnifiedPost post6 = createPost("test6", UnifiedPostType.CAREER, member);
        UnifiedPost post7 = createPost("test7", UnifiedPostType.JOB_INFO, member);
        UnifiedPost post8 = createPost("test8", UnifiedPostType.JOB_INFO, member);

        unifiedPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8));

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<UnifiedPost> findPostList = unifiedPostRepository.findByUnifiedPostTypeByPaging(UnifiedPostType.DEV, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(1)
                .extracting("title", "unifiedPostType")
                .containsExactly(
                        Tuple.tuple("test1", UnifiedPostType.DEV)
                );
    }

    @DisplayName("타입(자유)을 선택하여 페이징을 통해 통합 게시물을 조회한다.")
    @Test
    void findByUnifiedPostTypeByPaging3() {
        // given
        Member member = memberRepository.save(new Member());

        UnifiedPost post1 = createPost("test1", UnifiedPostType.DEV, member);
        UnifiedPost post2 = createPost("test2", UnifiedPostType.DEV, member);
        UnifiedPost post3 = createPost("test3", UnifiedPostType.FREE, member);
        UnifiedPost post4 = createPost("test4", UnifiedPostType.FREE, member);
        UnifiedPost post5 = createPost("test5", UnifiedPostType.CAREER, member);
        UnifiedPost post6 = createPost("test6", UnifiedPostType.CAREER, member);
        UnifiedPost post7 = createPost("test7", UnifiedPostType.JOB_INFO, member);
        UnifiedPost post8 = createPost("test8", UnifiedPostType.JOB_INFO, member);

        unifiedPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8));

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<UnifiedPost> findPostList = unifiedPostRepository.findByUnifiedPostTypeByPaging(UnifiedPostType.FREE, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(1)
                .extracting("title", "unifiedPostType")
                .containsExactly(
                        Tuple.tuple("test3", UnifiedPostType.FREE)
                );
    }

    @DisplayName("타입(진로)을 선택하여 페이징을 통해 통합 게시물을 조회한다.")
    @Test
    void findByUnifiedPostTypeByPaging4() {
        // given
        Member member = memberRepository.save(new Member());

        UnifiedPost post1 = createPost("test1", UnifiedPostType.DEV, member);
        UnifiedPost post2 = createPost("test2", UnifiedPostType.DEV, member);
        UnifiedPost post3 = createPost("test3", UnifiedPostType.FREE, member);
        UnifiedPost post4 = createPost("test4", UnifiedPostType.FREE, member);
        UnifiedPost post5 = createPost("test5", UnifiedPostType.CAREER, member);
        UnifiedPost post6 = createPost("test6", UnifiedPostType.CAREER, member);
        UnifiedPost post7 = createPost("test7", UnifiedPostType.JOB_INFO, member);
        UnifiedPost post8 = createPost("test8", UnifiedPostType.JOB_INFO, member);

        unifiedPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8));

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<UnifiedPost> findPostList = unifiedPostRepository.findByUnifiedPostTypeByPaging(UnifiedPostType.CAREER, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(1)
                .extracting("title", "unifiedPostType")
                .containsExactly(
                        Tuple.tuple("test5", UnifiedPostType.CAREER)
                );
    }

    @DisplayName("타입(취업정보)을 선택하여 페이징을 통해 통합 게시물을 조회한다.")
    @Test
    void findByUnifiedPostTypeByPaging5() {
        // given
        Member member = memberRepository.save(new Member());

        UnifiedPost post1 = createPost("test1", UnifiedPostType.DEV, member);
        UnifiedPost post2 = createPost("test2", UnifiedPostType.DEV, member);
        UnifiedPost post3 = createPost("test3", UnifiedPostType.FREE, member);
        UnifiedPost post4 = createPost("test4", UnifiedPostType.FREE, member);
        UnifiedPost post5 = createPost("test5", UnifiedPostType.CAREER, member);
        UnifiedPost post6 = createPost("test6", UnifiedPostType.CAREER, member);
        UnifiedPost post7 = createPost("test7", UnifiedPostType.JOB_INFO, member);
        UnifiedPost post8 = createPost("test8", UnifiedPostType.JOB_INFO, member);

        unifiedPostRepository.saveAll(List.of(post1, post2, post3, post4, post5, post6, post7, post8));

        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<UnifiedPost> findPostList = unifiedPostRepository.findByUnifiedPostTypeByPaging(UnifiedPostType.JOB_INFO, pageRequest).getContent();

        // then
        Assertions.assertThat(findPostList).hasSize(1)
                .extracting("title", "unifiedPostType")
                .containsExactly(
                        Tuple.tuple("test7", UnifiedPostType.JOB_INFO)
                );
    }

    private static UnifiedPost createPost(String title, UnifiedPostType type, Member member) {
        return UnifiedPost.builder()
                .title(title)
                .bodyContent("test")
                .member(member)
                .viewCount(0L)
                .recommendationCount(0)
                .createdTime(LocalDateTime.of(2024, 6, 12, 10, 0))
                .unifiedPostType(type)
                .build();
    }
}