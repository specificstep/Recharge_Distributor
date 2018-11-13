package specificstep.com.perfectrecharge_dist.data.net;

import java.math.BigDecimal;
import java.util.List;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.Models.CashSummaryModel;
import specificstep.com.perfectrecharge_dist.Models.ChildUserModel;
import specificstep.com.perfectrecharge_dist.Models.ParentUser;
import specificstep.com.perfectrecharge_dist.data.entity.BaseResponse;
import specificstep.com.perfectrecharge_dist.data.entity.CashSummaryEntity;
import specificstep.com.perfectrecharge_dist.data.entity.CompanyEntity;
import specificstep.com.perfectrecharge_dist.data.entity.ForgotPasswordResponse;
import specificstep.com.perfectrecharge_dist.data.entity.OtpVerifyResponse;
import specificstep.com.perfectrecharge_dist.data.entity.ParentUserResponse;
import specificstep.com.perfectrecharge_dist.data.entity.ProductEntity;
import specificstep.com.perfectrecharge_dist.data.entity.SettingEntity;
import specificstep.com.perfectrecharge_dist.data.entity.SignInResponse;
import specificstep.com.perfectrecharge_dist.data.entity.StateEntity;
import specificstep.com.perfectrecharge_dist.data.entity.UserEntity;

public interface RestApi {

    Observable<BaseResponse> signUp(String userName, int userType);

    Observable<OtpVerifyResponse> verifyOTP(String userName, String otp, int userType);

    Observable<ForgotPasswordResponse> verifyForgotOTP(String username, String otp_code, String mac_address, String app, String forgot_otp);

    Observable<List<CompanyEntity>> getCompanyList(int service);

    Observable<List<ProductEntity>> getProducts(int service);

    Observable<List<StateEntity>> getStates(int service);

    Observable<SettingEntity> getSettings();

    Observable<List<UserEntity>> getChildUsers();

    Observable<SignInResponse> login(String userName, String password, int userType, String mac_address, String otp_code, String app);

    Observable<BaseResponse> forgotpassword(String userName, String mac_address, String otp_code, String app);

    Observable<BigDecimal> getBalance();

    Observable<List<UserEntity>> getUserByEmail(String email);

    Observable<String> addBalance(String userId, String amount);

    Observable<List<CashSummaryEntity>> getCashSummary(String userId, int userType, String fromDate, String toDate);

    Observable<BaseResponse> changePassword(String password, String oldPassword);

    Observable<BaseResponse> forgotchnagepassword(String username, String otp_code, String mac_address, String app, String forgot_otp, String password, String oldPassword);

    Observable<ParentUserResponse> getParentUser();
}
