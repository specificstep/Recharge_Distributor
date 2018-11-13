package specificstep.com.perfectrecharge_dist.interactors.usecases;

import com.google.common.base.Preconditions;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import specificstep.com.perfectrecharge_dist.Models.Product;
import specificstep.com.perfectrecharge_dist.interactors.executor.PostExecutionThread;
import specificstep.com.perfectrecharge_dist.interactors.executor.ThreadExecutor;
import specificstep.com.perfectrecharge_dist.interactors.repositories.UserRepository;

public class GetProductUseCase extends UseCase<List<Product>, GetProductUseCase.Params> {

    private UserRepository userRepository;

    @Inject
    protected GetProductUseCase(UserRepository userRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userRepository = userRepository;
    }

    @Override
    Observable<List<Product>> buildUseCaseObservable(Params params) {
        Preconditions.checkNotNull(params);
        return userRepository.getProducts(params.serviceType);
    }

    public static final class Params {
        private int serviceType;

        Params(int serviceType) {
            this.serviceType = serviceType;
        }

        public static Params toParams(int serviceType) {
            return new Params(serviceType);
        }
    }
}
