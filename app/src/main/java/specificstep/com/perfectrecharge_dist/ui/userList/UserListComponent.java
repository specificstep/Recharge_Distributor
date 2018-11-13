package specificstep.com.perfectrecharge_dist.ui.userList;

import dagger.Component;
import specificstep.com.perfectrecharge_dist.di.FragmentScoped;
import specificstep.com.perfectrecharge_dist.di.components.ApplicationComponent;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = UserListModule.class)
public interface UserListComponent {
    void inject(UserListFragment fragment);
}
