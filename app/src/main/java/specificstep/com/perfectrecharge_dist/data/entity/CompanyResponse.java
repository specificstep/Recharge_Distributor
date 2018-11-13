package specificstep.com.perfectrecharge_dist.data.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompanyResponse {

    @SerializedName("company")
    List<CompanyEntity> companyList;

    public List<CompanyEntity> getCompanyList() {
        return companyList;
    }
}
