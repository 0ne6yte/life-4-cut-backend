package com.onebyte.life4cut.picture.service;

import com.onebyte.life4cut.album.domain.Album;
import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.exception.AlbumDoesNotHaveSlotException;
import com.onebyte.life4cut.album.exception.AlbumNotFoundException;
import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.AlbumRepository;
import com.onebyte.life4cut.album.repository.UserAlbumRepository;
import com.onebyte.life4cut.common.constants.S3Env;
import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.exception.PictureNotFoundException;
import com.onebyte.life4cut.picture.repository.PictureRepository;
import com.onebyte.life4cut.picture.repository.dto.PictureDetailResult;
import com.onebyte.life4cut.picture.service.dto.PictureDetailInSlot;
import com.onebyte.life4cut.pictureTag.domain.PictureTag;
import com.onebyte.life4cut.pictureTag.domain.vo.PictureTags;
import com.onebyte.life4cut.pictureTag.repository.PictureTagRepository;
import com.onebyte.life4cut.slot.domain.Slot;
import com.onebyte.life4cut.slot.exception.SlotNotFoundException;
import com.onebyte.life4cut.slot.repository.SlotRepository;
import com.onebyte.life4cut.support.fileUpload.FileUploadResponse;
import com.onebyte.life4cut.support.fileUpload.FileUploader;
import com.onebyte.life4cut.support.fileUpload.MultipartFileUploadRequest;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class PictureService {

  private final SlotRepository slotRepository;
  private final AlbumRepository albumRepository;
  private final UserAlbumRepository userAlbumRepository;
  private final PictureTagRepository pictureTagRepository;
  private final PictureRepository pictureRepository;
  private final FileUploader fileUploader;
  private final S3Env s3Env;

  public Long createInSlot(
      @Nonnull Long authorId,
      @Nonnull Long albumId,
      @Nonnull Long slotId,
      @Nonnull String content,
      @Nonnull LocalDateTime picturedAt,
      @Nonnull List<String> tags,
      @Nonnull MultipartFile image) {
    Album album = albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);
    UserAlbum userAlbum =
        userAlbumRepository
            .findByUserIdAndAlbumId(authorId, albumId)
            .orElseThrow(UserAlbumRolePermissionException::new);
    if (userAlbum.isGuest()) {
      throw new UserAlbumRolePermissionException();
    }

    Slot slot = slotRepository.findById(slotId).orElseThrow(SlotNotFoundException::new);
    if (!slot.isIn(album)) {
      throw new AlbumDoesNotHaveSlotException();
    }

    PictureTags existingPictureTags = pictureTagRepository.findByNames(albumId, tags);
    List<PictureTag> newPictureTags =
        tags.stream()
            .filter(tag -> !existingPictureTags.has(tag))
            .map(tag -> PictureTag.create(albumId, authorId, tag))
            .toList();

    FileUploadResponse response =
        fileUploader.upload(MultipartFileUploadRequest.of(image, s3Env.bucket()));

    pictureTagRepository.saveAll(newPictureTags);
    existingPictureTags.getTags().forEach(PictureTag::restoreIfRequired);

    Picture picture =
        Picture.create(
            authorId,
            albumId,
            response.key(),
            content,
            picturedAt,
            existingPictureTags.addAll(newPictureTags));

    pictureRepository.save(picture);
    slot.addPicture(picture.getId());

    return picture.getId();
  }

  public void updatePicture(
      @Nonnull final Long authorId,
      @Nonnull final Long albumId,
      @Nonnull final Long pictureId,
      @Nullable final String content,
      @Nullable final List<String> tags,
      @Nullable final LocalDateTime picturedAt,
      @Nullable final MultipartFile image) {
    Picture picture =
        pictureRepository.findById(pictureId).orElseThrow(PictureNotFoundException::new);

    if (!picture.isIn(albumId)) {
      throw new PictureNotFoundException();
    }

    albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);
    UserAlbum userAlbum =
        userAlbumRepository
            .findByUserIdAndAlbumId(authorId, albumId)
            .orElseThrow(UserAlbumRolePermissionException::new);
    if (userAlbum.isGuest()) {
      throw new UserAlbumRolePermissionException();
    }

    String key = uploadImageIfRequired(image);

    picture.updateIfRequired(content, picturedAt, key);

    if (tags != null) {
      PictureTags existingPictureTags = pictureTagRepository.findByNames(albumId, tags);
      List<PictureTag> newPictureTags =
          tags.stream()
              .filter(tag -> !existingPictureTags.has(tag))
              .map(tag -> PictureTag.create(albumId, authorId, tag))
              .toList();

      pictureTagRepository.saveAll(newPictureTags);
      existingPictureTags.getTags().forEach(PictureTag::restoreIfRequired);

      picture.updateTags(existingPictureTags.addAll(newPictureTags));
    }
  }

  @Nonnull
  @Transactional(readOnly = true)
  public List<PictureDetailInSlot> getPicturesInSlotByAlbum(
      @Nonnull final Long userId, @Nonnull final Long albumId) {
    albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);
    userAlbumRepository
        .findByUserIdAndAlbumId(userId, albumId)
        .orElseThrow(UserAlbumRolePermissionException::new);

    List<Slot> slots = slotRepository.findByAlbumId(albumId);
    if (slots.isEmpty()) {
      return Collections.emptyList();
    }

    List<Long> pictureIds =
        slots.stream().map(Slot::getPictureId).filter(Objects::nonNull).toList();

    List<PictureDetailResult> pictureDetailResults = pictureRepository.findDetailByIds(pictureIds);

    return slots.stream()
        .map(
            slot -> {
              Optional<PictureDetailResult> pictureDetailResult =
                  pictureDetailResults.stream()
                      .filter(
                          pictureDetail -> pictureDetail.pictureId().equals(slot.getPictureId()))
                      .findFirst();

              return new PictureDetailInSlot(
                  slot.getId(),
                  slot.getPage(),
                  slot.getLayout(),
                  slot.getLocation(),
                  pictureDetailResult);
            })
        .toList();
  }

  @Nullable
  private String uploadImageIfRequired(@Nullable MultipartFile image) {
    if (image == null) {
      return null;
    }

    return fileUploader.upload(MultipartFileUploadRequest.of(image, s3Env.bucket())).key();
  }
}
