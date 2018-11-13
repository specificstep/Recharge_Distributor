package specificstep.com.perfectrecharge_dist.data.net;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Response;
import specificstep.com.perfectrecharge_dist.GlobalClasses.Constants;
import specificstep.com.perfectrecharge_dist.GlobalClasses.EncryptionUtil;
import specificstep.com.perfectrecharge_dist.data.entity.BalanceResponse;
import specificstep.com.perfectrecharge_dist.data.entity.BaseResponse;
import specificstep.com.perfectrecharge_dist.data.entity.CashSummaryEntity;
import specificstep.com.perfectrecharge_dist.data.entity.ChildUserResponse;
import specificstep.com.perfectrecharge_dist.data.entity.CompanyEntity;
import specificstep.com.perfectrecharge_dist.data.entity.EncryptedResponse;
import specificstep.com.perfectrecharge_dist.data.entity.ForgotPasswordResponse;
import specificstep.com.perfectrecharge_dist.data.entity.OtpVerifyResponse;
import specificstep.com.perfectrecharge_dist.data.entity.ParentUserResponse;
import specificstep.com.perfectrecharge_dist.data.entity.ProductEntity;
import specificstep.com.perfectrecharge_dist.data.entity.SettingEntity;
import specificstep.com.perfectrecharge_dist.data.entity.SignInResponse;
import specificstep.com.perfectrecharge_dist.data.entity.StateEntity;
import specificstep.com.perfectrecharge_dist.data.entity.UserEntity;
import specificstep.com.perfectrecharge_dist.data.exceptions.DataNotFoundException;
import specificstep.com.perfectrecharge_dist.data.exceptions.InValidDetailsException;
import specificstep.com.perfectrecharge_dist.data.exceptions.InternalServerException;
import specificstep.com.perfectrecharge_dist.data.exceptions.InvalidOTPException;
import specificstep.com.perfectrecharge_dist.data.exceptions.InvalidOldPasswordException;
import specificstep.com.perfectrecharge_dist.data.exceptions.InvalidUserNameException;
import specificstep.com.perfectrecharge_dist.data.exceptions.NetworkConnectionException;
import specificstep.com.perfectrecharge_dist.data.exceptions.RechargeFailedException;
import specificstep.com.perfectrecharge_dist.data.exceptions.SignInException;
import specificstep.com.perfectrecharge_dist.data.mapper.UserJsonDataMapper;
import specificstep.com.perfectrecharge_dist.data.net.retrofit.RechargeService;
import specificstep.com.perfectrecharge_dist.utility.ConnectionUtil;

@Singleton
public class RestApiImpl implements RestApi {

    private static final String TAG = RestApiImpl.class.getSimpleName();
    private final RechargeService rechargeService;
    private final UserJsonDataMapper userJsonDataMapper;
    private final ConnectionUtil connectionUtil;
    private final EncryptionUtil encryptionUtil;
    private final Context context;

    @Inject
    public RestApiImpl(RechargeService rechargeService,
                       UserJsonDataMapper userJsonDataMapper,
                       ConnectionUtil connectionUtil,
                       EncryptionUtil encryptionUtil, Context context) {
        this.rechargeService = rechargeService;
        this.userJsonDataMapper = userJsonDataMapper;
        this.connectionUtil = connectionUtil;
        this.encryptionUtil = encryptionUtil;
        this.context = context;
    }

    @Override
    public Observable<BaseResponse> signUp(final String userName, final int userType) {
        return Observable.create(new ObservableOnSubscribe<BaseResponse>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", userName);
                    params.put("user_type", String.valueOf(userType));
                    try {
                        Response<String> response = rechargeService.signUp(params).execute();
                        System.out.println("Sign up response: " + response.body());
                        BaseResponse baseResponse;
                        try {
                            baseResponse = userJsonDataMapper.parseBaseResponse(response.body());
                            Log.d(TAG, "SignUp Response: " + response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (baseResponse.getStatus() == 0) {
                                sub.onNext(baseResponse);
                                sub.onComplete();
                                return;
                            } else if (baseResponse.getStatus() == 2) {
                                sub.onError(new InvalidUserNameException(baseResponse.getMessage()));
                                return;
                            }
                        }
                        sub.onError(new Exception(baseResponse.getMessage()));
                    } catch (IOException e) {
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<OtpVerifyResponse> verifyOTP(final String userName, final String otp, final int userType) {
        return Observable.create(new ObservableOnSubscribe<OtpVerifyResponse>() {
            @Override
            public void subscribe(ObservableEmitter<OtpVerifyResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", userName);
                    params.put("otp_code", otp);
                    params.put("user_type", String.valueOf(userType));
                    try {
                        Response<String> response = rechargeService.verifyOtp(params).execute();
                        OtpVerifyResponse baseResponse;
                        try {
                            baseResponse = userJsonDataMapper.parseOtpVerificationResponse(response.body());
                            Log.d(TAG, "VerifyOtp Response: " + response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (baseResponse.getStatus() == 1) {
                                sub.onNext(baseResponse);
                                sub.onComplete();
                                return;
                            } else if (baseResponse.getStatus() == 0) {
                                sub.onError(new InvalidOTPException(baseResponse.getMessage()));
                                return;
                            }
                        }
                        sub.onError(new Exception(baseResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<ForgotPasswordResponse> verifyForgotOTP(final String username, final String otp_code, final String mac_address, final String app, final String forgot_otp) {
        return Observable.create(new ObservableOnSubscribe<ForgotPasswordResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ForgotPasswordResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("otp_code", otp_code);
                    params.put("mac_address", mac_address);
                    params.put("app", app);
                    params.put("forgot_otp", forgot_otp);
                    try {
                        Response<String> response = rechargeService.verifyForgotOtp(params).execute();
                        ForgotPasswordResponse baseResponse;
                        try {
                            baseResponse = userJsonDataMapper.parseBaseForgotResponse(response.body());
                            Log.d(TAG, "VerifyForgotOtp Response: " + response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (baseResponse.getStatus() == 1) {
                                sub.onNext(baseResponse);
                                sub.onComplete();
                                return;
                            } else if (baseResponse.getStatus() == 2) {
                                sub.onError(new InvalidOTPException(baseResponse.getMessage()));
                                return;
                            }
                        }
                        sub.onError(new Exception(baseResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<List<CompanyEntity>> getCompanyList(final int service) {
        return Observable.create(new ObservableOnSubscribe<List<CompanyEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CompanyEntity>> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getCompanyList(service).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                List<CompanyEntity> companyResponse = userJsonDataMapper.parseCompanyResponse(decryptAPI(encryptedResponse.getData()));
                                sub.onNext(companyResponse);
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    private void sessionExpired() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.ACTION_INVALID_ACCESS_TOKEN));
    }

    @Override
    public Observable<List<ProductEntity>> getProducts(final int service) {
        return Observable.create(new ObservableOnSubscribe<List<ProductEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ProductEntity>> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getProducts(service).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                List<ProductEntity> productEntities = userJsonDataMapper.parseProductResponse(decryptAPI(encryptedResponse.getData()));
                                sub.onNext(productEntities);
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<List<StateEntity>> getStates(final int service) {
        return Observable.create(new ObservableOnSubscribe<List<StateEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<StateEntity>> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getStates(service).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                List<StateEntity> stateEntities = userJsonDataMapper.parseStateResponse(decryptAPI(encryptedResponse.getData()));
                                sub.onNext(stateEntities);
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<SettingEntity> getSettings() {
        return Observable.create(new ObservableOnSubscribe<SettingEntity>() {
            @Override
            public void subscribe(ObservableEmitter<SettingEntity> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getSettings(1).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                String decryptedResponse = decryptAPI(encryptedResponse.getData2());
                                Log.d(TAG, "Settings Response=" + decryptedResponse);
                                SettingEntity settingEntity = userJsonDataMapper.parseSettingResponse(decryptedResponse);
                                sub.onNext(settingEntity);
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<List<UserEntity>> getChildUsers() {
        return Observable.create(new ObservableOnSubscribe<List<UserEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserEntity>> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getChildUsers(1).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                String decryptedResponse = decryptAPI(encryptedResponse.getData());
                                Log.d(TAG, "ChildUser Response: " + decryptedResponse);
                                ChildUserResponse childUserResponse = userJsonDataMapper.parseChildUserResponse(decryptedResponse);
                                sub.onNext(childUserResponse.getUserEntities());
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<SignInResponse> login(final String userName, final String password, final int userType, final String mac_address, final String otp_code, final String app) {
        return Observable.create(new ObservableOnSubscribe<SignInResponse>() {
            @Override
            public void subscribe(ObservableEmitter<SignInResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", userName);
                    params.put("password", encrypt(password));
                    params.put("user_type", String.valueOf(userType));
                    params.put("mac_address", String.valueOf(mac_address));
                    params.put("otp_code", String.valueOf(otp_code));
                    params.put("app", String.valueOf(app));
                    System.out.println("Login Param : " + params);
                    try {
                        Response<String> response = rechargeService.login(params).execute();
                        System.out.println("Login Response : " + response.body());
                        SignInResponse signInResponse = null;
                        EncryptedResponse baseResponse = userJsonDataMapper.transformEncryptedResponse(response.body());

                        if (response.isSuccessful()) {
                            if (baseResponse.getStatus() == 1) {
                                signInResponse = userJsonDataMapper.transformSignInResponse(decryptAPI(baseResponse.getData()));
                                sub.onNext(signInResponse);
                                sub.onComplete();
                                return;
                            } else {
                                sub.onError(new SignInException(baseResponse.getMessage()));
                            }
                        }
                        //sub.onError(new SignInException(signInResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new SignInException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<BaseResponse> forgotpassword(final String userName, final String mac_address, final String otp_code, final String app) {
        return Observable.create(new ObservableOnSubscribe<BaseResponse>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", userName);
                    params.put("mac_address", String.valueOf(mac_address));
                    params.put("otp_code", String.valueOf(otp_code));
                    params.put("app", String.valueOf(app));
                    System.out.println("Login Param : " + params);
                    try {
                        Response<BaseResponse> response = rechargeService.forgotpassword(params).execute();
                        System.out.println("forgot Response : " + response.body());
                        //SignInResponse signInResponse = null;
                        //EncryptedResponse baseResponse = userJsonDataMapper.transformEncryptedResponse(response.body());



                        /*if (response.isSuccessful()) {
                            if (baseResponse.getStatus() == 1) {
                                signInResponse = userJsonDataMapper.transformSignInResponse(decryptAPI(baseResponse.getData()));
                                sub.onNext(signInResponse);
                                sub.onComplete();
                                return;
                            } else {
                                sub.onError(new SignInException(baseResponse.getMessage()));
                            }
                        }*/
                        //sub.onError(new SignInException(signInResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new SignInException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<BigDecimal> getBalance() {
        return Observable.create(new ObservableOnSubscribe<BigDecimal>() {
            @Override
            public void subscribe(ObservableEmitter<BigDecimal> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getBalance(1).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                BalanceResponse balanceResponse = userJsonDataMapper.parseBalanceResponse(decryptAPI(encryptedResponse.getData()));
                                sub.onNext(balanceResponse.getBalance());
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<List<UserEntity>> getUserByEmail(final String email) {
        return Observable.create(new ObservableOnSubscribe<List<UserEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<UserEntity>> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getUser(email).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                ChildUserResponse childUserResponse = userJsonDataMapper.parseChildUserResponse(decryptAPI(encryptedResponse.getData()));
                                sub.onNext(childUserResponse.getUserEntities());
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<String> addBalance(final String userId, final String amount) {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.addBalance(encrypt(userId), encrypt(amount)).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                Log.d(TAG, "AddBalance response=" + decryptAPI(encryptedResponse.getData()));
                                sub.onNext(userJsonDataMapper.fetchPaymentId(decryptAPI(encryptedResponse.getData())));
                                sub.onComplete();
                            } else if (encryptedResponse.getStatus() == 2) {
                                if (Constants.INVALID_DETAILS.equals(encryptedResponse.getMessage())) {
                                    sessionExpired();
                                } else {
                                    sub.onError(new RechargeFailedException(encryptedResponse.getMessage()));
                                }
                            } else {
                                sub.onError(new RechargeFailedException(encryptedResponse.getMessage()));
                            }
                        } else {
                            sub.onError(new RechargeFailedException(encryptedResponse.getMessage()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<List<CashSummaryEntity>> getCashSummary(final String userId, final int userType, final String fromDate, final String toDate) {
        return Observable.create(new ObservableOnSubscribe<List<CashSummaryEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CashSummaryEntity>> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getCashSummary(userId, userType, fromDate, toDate).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                List<CashSummaryEntity> summaryEntities = userJsonDataMapper.parseCashSummaryResponse(decryptAPI(encryptedResponse.getData()));
                                sub.onNext(summaryEntities);
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new InValidDetailsException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<BaseResponse> changePassword(final String password, final String oldPassword) {
        return Observable.create(new ObservableOnSubscribe<BaseResponse>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("new_password", encrypt(password));
                    params.put("old_password", encrypt(oldPassword));
                    Log.d(TAG, "Token=" + FirebaseInstanceId.getInstance().getToken());
                    Log.d(TAG, "Original new password=" + password);
                    Log.d(TAG, "Encrypted new password=" + encrypt(password));
                    Log.d(TAG, "Original old password=" + oldPassword);
                    Log.d(TAG, "Encrypted old password=" + encrypt(oldPassword));

                    try {
                        Response<String> response = rechargeService.changePassword(params).execute();
                        BaseResponse baseResponse;
                        try {
                            baseResponse = userJsonDataMapper.parseBaseResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (baseResponse.getStatus() == 1) {
                                sub.onNext(baseResponse);
                                sub.onComplete();
                                return;
                            } else if (baseResponse.getStatus() == 2) {
                                sub.onError(new InvalidOldPasswordException(baseResponse.getMessage()));
                                return;
                            }
                        }
                        sub.onError(new InvalidOldPasswordException(baseResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<BaseResponse> forgotchnagepassword(final String username, final String otp_code, final String mac_address, final String app,final String forgot_otp, final String password, final String oldPassword) {
        return Observable.create(new ObservableOnSubscribe<BaseResponse>() {
            @Override
            public void subscribe(ObservableEmitter<BaseResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("mac_address", mac_address);
                    params.put("otp_code", otp_code);
                    params.put("app", app);
                    params.put("forgot_otp", forgot_otp);
                    params.put("new_pass", encrypt(password));
                    params.put("confirm_pass", encrypt(oldPassword));
                    Log.d(TAG, "forgotchangepassword param: " + params);

                    try {
                        Response<String> response = rechargeService.forgotChangePassword(params).execute();
                        BaseResponse baseResponse;
                        try {
                            baseResponse = userJsonDataMapper.parseBaseResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (baseResponse.getStatus() == 1) {
                                sub.onNext(baseResponse);
                                sub.onComplete();
                                return;
                            } else if (baseResponse.getStatus() == 2) {
                                sub.onError(new InvalidOldPasswordException(baseResponse.getMessage()));
                                return;
                            }
                        }
                        sub.onError(new InvalidOldPasswordException(baseResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    @Override
    public Observable<ParentUserResponse> getParentUser() {
        return Observable.create(new ObservableOnSubscribe<ParentUserResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ParentUserResponse> sub) throws Exception {
                if (connectionUtil.isInternetConnected() && !Strings.isNullOrEmpty(FirebaseInstanceId.getInstance().getToken())) {
                    try {
                        Response<String> response = rechargeService.getParentUsers(1).execute();
                        EncryptedResponse encryptedResponse;
                        try {
                            encryptedResponse = userJsonDataMapper.transformEncryptedResponse(response.body());
                        } catch (JsonSyntaxException e) {
                            sub.onError(new InternalServerException());
                            return;
                        }
                        if (response.isSuccessful()) {
                            if (encryptedResponse.getStatus() == 1) {
                                String decryptedResponse = decryptAPI(encryptedResponse.getData());
                                Log.d(TAG, "ParentUser Response: " + decryptedResponse);
                                ParentUserResponse childUserResponse = userJsonDataMapper.parseParentUserResponse(decryptedResponse);
                                sub.onNext(childUserResponse);
                                sub.onComplete();
                                return;
                            } else if (encryptedResponse.getStatus() == 2) {
                                sessionExpired();
                                return;
                            }
                        }
                        sub.onError(new DataNotFoundException(encryptedResponse.getMessage()));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sub.onError(new NetworkConnectionException(e.getMessage()));
                    }
                } else {
                    sub.onError(new NetworkConnectionException());
                }
            }
        });
    }

    private String decryptAPI(String response) {
        return encryptionUtil.decrypt(response);
    }

    private String encrypt(String text) {
        return encryptionUtil.encrypt(text);
    }
}
