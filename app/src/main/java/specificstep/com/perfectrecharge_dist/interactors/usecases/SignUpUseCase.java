package specificstep.com.perfectrecharge_dist.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.data.entity.BaseResponse;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;

public class SignUpUseCase extends UseCase<BaseResponse, SignUpUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected SignUpUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BaseResponse> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.signUp(params.userName, params.userType);
    }

    public static final class Params {

        private String userName;
        private int userType;

        Params(String userName, int userType) {
            this.userName = userName;
            this.userType = userType;
        }

        public static Params toParams(String userName, int userType) {
            return new Params(userName, userType);
        }
    }
}
