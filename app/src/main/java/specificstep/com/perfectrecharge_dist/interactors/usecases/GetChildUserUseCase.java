package specificstep.com.perfectrecharge_dist.interactors.usecases;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.Models.UserList;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;

public class GetChildUserUseCase extends UseCase<List<UserList>, Void> {

    private UserRepository userRepository;

    @Inject
    protected GetChildUserUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<UserList>> buildUseCaseObservable(Void aVoid) {
        return userRepository.getChildUsers();
    }
}
