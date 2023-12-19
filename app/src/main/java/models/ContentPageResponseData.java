package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ContentPageResponseData {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("page_title")
    @Expose
    private String pageTitle;
    @SerializedName("page_sef")
    @Expose
    private String pageSef;
    @SerializedName("page_content")
    @Expose
    private String pageContent;
    @SerializedName("page_content_ar")
    @Expose
    private String pageContentAr;
    @SerializedName("page_meta_title")
    @Expose
    private String pageMetaTitle;
    @SerializedName("page_meta_keywords")
    @Expose
    private String pageMetaKeywords;
    @SerializedName("page_meta_description")
    @Expose
    private String pageMetaDescription;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageSef() {
        return pageSef;
    }

    public void setPageSef(String pageSef) {
        this.pageSef = pageSef;
    }

    public String getPageContent() {
        return pageContent;
    }

    public void setPageContent(String pageContent) {
        this.pageContent = pageContent;
    }

    public String getPageContentAr() {
        return pageContentAr;
    }

    public void setPageContentAr(String pageContentAr) {
        this.pageContentAr = pageContentAr;
    }

    public String getPageMetaTitle() {
        return pageMetaTitle;
    }

    public void setPageMetaTitle(String pageMetaTitle) {
        this.pageMetaTitle = pageMetaTitle;
    }

    public String getPageMetaKeywords() {
        return pageMetaKeywords;
    }

    public void setPageMetaKeywords(String pageMetaKeywords) {
        this.pageMetaKeywords = pageMetaKeywords;
    }

    public String getPageMetaDescription() {
        return pageMetaDescription;
    }

    public void setPageMetaDescription(String pageMetaDescription) {
        this.pageMetaDescription = pageMetaDescription;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}

