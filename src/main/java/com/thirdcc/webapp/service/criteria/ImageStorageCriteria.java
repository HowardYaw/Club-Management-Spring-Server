package com.thirdcc.webapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.thirdcc.webapp.domain.ImageStorage} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.ImageStorageResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /image-storages?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ImageStorageCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter imageUrl;

  private StringFilter fileName;

  private StringFilter fileType;

  public ImageStorageCriteria() {}

  public ImageStorageCriteria(ImageStorageCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.imageUrl = other.imageUrl == null ? null : other.imageUrl.copy();
    this.fileName = other.fileName == null ? null : other.fileName.copy();
    this.fileType = other.fileType == null ? null : other.fileType.copy();
  }

  @Override
  public ImageStorageCriteria copy() {
    return new ImageStorageCriteria(this);
  }

  public LongFilter getId() {
    return id;
  }

  public LongFilter id() {
    if (id == null) {
      id = new LongFilter();
    }
    return id;
  }

  public void setId(LongFilter id) {
    this.id = id;
  }

  public StringFilter getImageUrl() {
    return imageUrl;
  }

  public StringFilter imageUrl() {
    if (imageUrl == null) {
      imageUrl = new StringFilter();
    }
    return imageUrl;
  }

  public void setImageUrl(StringFilter imageUrl) {
    this.imageUrl = imageUrl;
  }

  public StringFilter getFileName() {
    return fileName;
  }

  public StringFilter fileName() {
    if (fileName == null) {
      fileName = new StringFilter();
    }
    return fileName;
  }

  public void setFileName(StringFilter fileName) {
    this.fileName = fileName;
  }

  public StringFilter getFileType() {
    return fileType;
  }

  public StringFilter fileType() {
    if (fileType == null) {
      fileType = new StringFilter();
    }
    return fileType;
  }

  public void setFileType(StringFilter fileType) {
    this.fileType = fileType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final ImageStorageCriteria that = (ImageStorageCriteria) o;
    return (
      Objects.equals(id, that.id) &&
      Objects.equals(imageUrl, that.imageUrl) &&
      Objects.equals(fileName, that.fileName) &&
      Objects.equals(fileType, that.fileType)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, imageUrl, fileName, fileType);
  }

  // prettier-ignore
    @Override
    public String toString() {
        return "ImageStorageCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (imageUrl != null ? "imageUrl=" + imageUrl + ", " : "") +
            (fileName != null ? "fileName=" + fileName + ", " : "") +
            (fileType != null ? "fileType=" + fileType + ", " : "") +
            "}";
    }
}
