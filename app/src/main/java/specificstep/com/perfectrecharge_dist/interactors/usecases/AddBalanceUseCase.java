package specificstep.com.perfectrecharge_dist.interactors.usecases;

import com.google.common.base.Preconditions;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;

public class AddBalanceUseCase extends UseCase<String, AddBalanceUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected AddBalanceUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<String> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.addBalance(params.userId, params.amount);
    }

    public static final class Params {
        private String userId;
        private String amount;

        Params(String userId, String amount) {
            this.userId = userId;
            this.amount = amount;
        }

        public static Params toParams(String userName, String amount) {
            return new Params(userName, amount);
        }
    }
}
