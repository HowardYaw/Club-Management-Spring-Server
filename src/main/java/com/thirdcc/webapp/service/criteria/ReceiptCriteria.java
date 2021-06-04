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
 * Criteria class for the {@link com.thirdcc.webapp.domain.Receipt} entity. This class is used
 * in {@link com.thirdcc.webapp.web.rest.ReceiptResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /receipts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ReceiptCriteria implements Serializable, Criteria {

  private static final long serialVersionUID = 1L;

  private LongFilter id;

  private StringFilter receiptUrl;

  private StringFilter fileName;

  private StringFilter fileType;

  private StringFilter receiptUniqueId;

  public ReceiptCriteria() {}

  public ReceiptCriteria(ReceiptCriteria other) {
    this.id = other.id == null ? null : other.id.copy();
    this.receiptUrl = other.receiptUrl == null ? null : other.receiptUrl.copy();
    this.fileName = other.fileName == null ? null : other.fileName.copy();
    this.fileType = other.fileType == null ? null : other.fileType.copy();
    this.receiptUniqueId = other.receiptUniqueId == null ? null : other.receiptUniqueId.copy();
  }

  @Override
  public ReceiptCriteria copy() {
    return new ReceiptCriteria(this);
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

  public StringFilter getReceiptUrl() {
    return receiptUrl;
  }

  public StringFilter receiptUrl() {
    if (receiptUrl == null) {
      receiptUrl = new StringFilter();
    }
    return receiptUrl;
  }

  public void setReceiptUrl(StringFilter receiptUrl) {
    this.receiptUrl = receiptUrl;
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

  public StringFilter getReceiptUniqueId() {
    return receiptUniqueId;
  }

  public StringFilter receiptUniqueId() {
    if (receiptUniqueId == null) {
        receiptUniqueId = new StringFilter();
    }
    return receiptUniqueId;
  }

  public void setReceiptUniqueId(StringFilter receiptUniqueId) {
    this.receiptUniqueId = receiptUniqueId;
  }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReceiptCriteria that = (ReceiptCriteria) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(receiptUrl, that.receiptUrl) &&
            Objects.equals(fileName, that.fileName) &&
            Objects.equals(fileType, that.fileType) &&
            Objects.equals(receiptUniqueId, that.receiptUniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, receiptUrl, fileName, fileType, receiptUniqueId);
    }

    // prettier-ignore

    @Override
    public String toString() {
        return "ReceiptCriteria{" +
            "id=" + id +
            ", receiptUrl=" + receiptUrl +
            ", fileName=" + fileName +
            ", fileType=" + fileType +
            ", receiptUniqueId=" + receiptUniqueId +
            '}';
    }
}
