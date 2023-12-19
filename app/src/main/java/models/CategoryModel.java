package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryModel {

    @SerializedName("ecat_id")
    @Expose
    private Integer ecatId;
    @SerializedName("ecat_name")
    @Expose
    private String ecatName;
    @SerializedName("lang_id")
    @Expose
    private Integer langId;
    @SerializedName("ecat_name_ar")
    @Expose
    private String ecat_name_ar;

    public String getEcat_name_ar() {
        return ecat_name_ar;
    }

    public void setEcat_name_ar(String ecat_name_ar) {
        this.ecat_name_ar = ecat_name_ar;
    }

    public Integer getEcatId() {
        return ecatId;
    }

    public void setEcatId(Integer ecatId) {
        this.ecatId = ecatId;
    }

    public String getEcatName() {
        return ecatName;
    }

    public void setEcatName(String ecatName) {
        this.ecatName = ecatName;
    }

    public Integer getLangId() {
        return langId;
    }

    public void setLangId(Integer langId) {
        this.langId = langId;
    }

}