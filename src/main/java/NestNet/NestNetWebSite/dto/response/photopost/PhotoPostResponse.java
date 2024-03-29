package NestNet.NestNetWebSite.dto.response.photopost;

import NestNet.NestNetWebSite.domain.attachedfile.AttachedFile;
import NestNet.NestNetWebSite.dto.response.AttachedFileDto;
import NestNet.NestNetWebSite.dto.response.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PhotoPostResponse {

    private PhotoPostDto photoPostDto;
    private List<AttachedFileDto> fileDtoList;
    private List<CommentDto> commentDtoList;
    private boolean isMemberLiked;
}
