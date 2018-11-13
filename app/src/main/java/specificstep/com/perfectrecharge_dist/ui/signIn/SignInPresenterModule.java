package specificstep.com.perfectrecharge_dist.ui.signIn;

import dagger.Module;
import dagger.Provides;

@Module
public class SignInPresenterModule {

    private SignInContract.View view;

    public SignInPresenterModule(SignInContract.View view) {
        this.view = view;
    }

    @Provides
    SignInContract.View providesSignInView() {
        return view;
    }
}
