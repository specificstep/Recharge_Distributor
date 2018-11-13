package specificstep.com.perfectrecharge_dist.data.source;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.Models.CashSummaryModel;
import specificstep.com.perfectrecharge_dist.Models.ChildUserModel;
import specificstep.com.perfectrecharge_dist.Models.Color;
import specificstep.com.perfectrecharge_dist.Models.Company;
import specificstep.com.perfectrecharge_dist.Models.ParentUser;
import specificstep.com.perfectrecharge_dist.Models.Product;
import specificstep.com.perfectrecharge_dist.Models.State;
import specificstep.com.perfectrecharge_dist.Models.UserList;
import specificstep.com.perfectrecharge_dist.data.entity.BaseResponse;
import specificstep.com.perfectrecharge_dist.data.entity.ForgotPasswordResponse;
import specificstep.com.perfectrecharge_dist.data.entity.SignInResponse;

public interface UserDataStore {

    Observable<BaseResponse> signUp(String userName, int userType);

    Observable<SignInResponse> login(String userName, String password, int userType, boolean rememberMe, String mac_address, String otp_code, String app);

    Observable<BaseResponse> forgotpassword(String userName, String mac_address, String otp_code, String app);

    Observable<BaseResponse> forgotchnagepassword(String username, String otp_code, String mac_address, String app, String forgot_otp, String password, String oldPassword);

    Observable<BaseResponse> verifyOTP(String userName, String otp, int userType);

    Observable<ForgotPasswordResponse> verifyForgotOTP(String username, String otp_code, String mac_address, String app, String forgot_otp);

    Observable<List<Company>> getCompanyList(int service);

    Observable<List<Product>> getProducts(int service);

    Observable<List<State>> getStates(int service);

    Observable<List<Color>> getSettings();

    Observable<List<UserList>> getChildUsers();

    Observable<BigDecimal> getBalance();

    Observable<ChildUserModel> getUser(String email);

    Observable<String> addBalance(String userId, String amount);

    Observable<List<CashSummaryModel>> getCashSummary(String userId, int userType, String fromDate, String toDate);

    Observable<BaseResponse> changePassword(String password, String oldPassword);

    Observable<ParentUser> getParentUser();
}
