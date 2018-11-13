package specificstep.com.perfectrecharge_dist.interactors.usecases;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.Models.ParentUser;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;

public class GetParentUserUseCase extends UseCase<ParentUser, Void> {

    private UserRepository userRepository;

    @Inject
    protected GetParentUserUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<ParentUser> buildUseCaseObservable(Void aVoid) {
        return userRepository.getParentUsers();
    }
}
