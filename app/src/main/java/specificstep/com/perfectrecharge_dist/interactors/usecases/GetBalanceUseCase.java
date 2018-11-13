package specificstep.com.perfectrecharge_dist.interactors.usecases;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.Models.Color;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;

public class GetBalanceUseCase extends UseCase<BigDecimal, Void> {

    private UserRepository userRepository;

    @Inject
    protected GetBalanceUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<BigDecimal> buildUseCaseObservable(Void aVoid) {
        return userRepository.getBalance();
    }
}
