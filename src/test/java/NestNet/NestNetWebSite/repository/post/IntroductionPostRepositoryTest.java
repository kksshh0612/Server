package NestNet.NestNetWebSite.repository.post;

import NestNet.NestNetWebSite.TestSupport;
import NestNet.NestNetWebSite.domain.attachedfile.AttachedFile;
import NestNet.NestNetWebSite.domain.member.Member;
import NestNet.NestNetWebSite.domain.post.Post;
import NestNet.NestNetWebSite.domain.post.introduction.IntroductionPost;
import NestNet.NestNetWebSite.repository.attachedfile.AttachedFileRepository;
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

class IntroductionPostRepositoryTest extends TestSupport {

    @Autowired
    private IntroductionPostRepository introductionPostRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AttachedFileRepository attachedFileRepository;

    @AfterEach
    void tearDown(){
        attachedFileRepository.deleteAllInBatch();
        introductionPostRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("자기소개 게시물의 PK를 통해 자기소개 게시물을 단건 조회한다.")
    @Test
    void findById() {
        // given
        Member member = memberRepository.save(new Member());

        IntroductionPost post = introductionPostRepository.save(
                createPost("test", "test", member, LocalDateTime.of(2024, 6, 12, 10, 0))
        );

        // when
        Optional<IntroductionPost> findPost = introductionPostRepository.findById(post.getId());

        // then
        Assertions.assertThat(findPost.get().getId()).isEqualTo(post.getId());
        Assertions.assertThat(findPost.get().getTitle()).isEqualTo(post.getTitle());
    }

    @DisplayName("자기소개 게시물을 페이징을 통해 조회한다. 이때, 연관된 첨부파일을 함께 조회한다.")
    @Test
    void findPostList() {
        // given
        Member member = memberRepository.save(new Member());

        LocalDateTime createdTime = LocalDateTime.of(2024, 6, 12, 10, 0);

        IntroductionPost post1 = createPost("test1", "test1", member, createdTime);
        IntroductionPost post2 = createPost("test2", "test2", member, createdTime);
        IntroductionPost post3 = createPost("test3", "test3", member, createdTime);

        introductionPostRepository.saveAll(List.of(post1, post2, post3));

        AttachedFile attachedFile1 = createAttachedFile(post1, "file1");
        AttachedFile attachedFile2 = createAttachedFile(post2, "file2");
        AttachedFile attachedFile3 = createAttachedFile(post3, "file3");

        attachedFileRepository.saveAll(List.of(attachedFile1, attachedFile2, attachedFile3));

        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "id"));

        // when
        List<IntroductionPost> findPost = introductionPostRepository.findPostList(pageRequest).getContent();

        // then
        Assertions.assertThat(findPost).hasSize(2)
                .extracting(post -> post.getTitle(), post -> post.getAttachedFileList().get(0).getOriginalFileName())
                .containsExactlyInAnyOrder(
                        Tuple.tuple("test1", "file1"),
                        Tuple.tuple("test2", "file2")
                );
    }

    private static AttachedFile createAttachedFile(Post post, String originalFileName){
        return AttachedFile.builder()
                .post(post)
                .originalFileName(originalFileName)
                .saveFileName("test")
                .saveFilePath("test")
                .build();
    }

    private static IntroductionPost createPost(String title, String bodyContent, Member member, LocalDateTime createdTime){
        return IntroductionPost.builder()
                .title(title)
                .bodyContent(bodyContent)
                .member(member)
                .viewCount(0L)
                .recommendationCount(0)
                .createdTime(createdTime)
                .build();
    }

}