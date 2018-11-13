package specificstep.com.perfectrecharge_dist.ui.signup;

import dagger.Module;
import dagger.Provides;

@Module
public class SignUpPresenterModule {

    private final SignUpContract.View view;

    public SignUpPresenterModule(SignUpContract.View view) {
        this.view = view;
    }

    @Provides
    SignUpContract.View provideSignUpContractView() {
        return view;
    }
}
