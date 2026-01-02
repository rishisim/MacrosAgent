package com.macros.agent;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.macros.agent.data.local.MacrosDatabase;
import com.macros.agent.data.local.dao.DiaryDao;
import com.macros.agent.data.local.dao.ExerciseDao;
import com.macros.agent.data.local.dao.FoodDao;
import com.macros.agent.data.local.dao.GeminiAnalysisDao;
import com.macros.agent.data.local.dao.GoalsDao;
import com.macros.agent.data.local.dao.UserMealDao;
import com.macros.agent.data.remote.api.GeminiService;
import com.macros.agent.data.remote.api.GoogleFitService;
import com.macros.agent.data.remote.api.UsdaApiService;
import com.macros.agent.data.repository.DiaryRepository;
import com.macros.agent.data.repository.ExerciseRepository;
import com.macros.agent.data.repository.ExerciseRepository_Factory;
import com.macros.agent.data.repository.ExerciseRepository_MembersInjector;
import com.macros.agent.data.repository.FoodRepository;
import com.macros.agent.data.repository.GoalsRepository;
import com.macros.agent.data.repository.UserMealRepository;
import com.macros.agent.di.DatabaseModule_ProvideDatabaseFactory;
import com.macros.agent.di.DatabaseModule_ProvideDiaryDaoFactory;
import com.macros.agent.di.DatabaseModule_ProvideExerciseDaoFactory;
import com.macros.agent.di.DatabaseModule_ProvideFoodDaoFactory;
import com.macros.agent.di.DatabaseModule_ProvideGeminiAnalysisDaoFactory;
import com.macros.agent.di.DatabaseModule_ProvideGoalsDaoFactory;
import com.macros.agent.di.DatabaseModule_ProvideUserMealDaoFactory;
import com.macros.agent.di.NetworkModule_ProvideOkHttpClientFactory;
import com.macros.agent.di.NetworkModule_ProvideUsdaApiKeyFactory;
import com.macros.agent.di.NetworkModule_ProvideUsdaApiServiceFactory;
import com.macros.agent.di.NetworkModule_ProvideUsdaRetrofitFactory;
import com.macros.agent.ui.screens.diary.DiaryViewModel;
import com.macros.agent.ui.screens.diary.DiaryViewModel_HiltModules;
import com.macros.agent.ui.screens.diary.DiaryViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.macros.agent.ui.screens.diary.DiaryViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.macros.agent.ui.screens.diary.ProgressChartViewModel;
import com.macros.agent.ui.screens.diary.ProgressChartViewModel_HiltModules;
import com.macros.agent.ui.screens.diary.ProgressChartViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.macros.agent.ui.screens.diary.ProgressChartViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.macros.agent.ui.screens.exercise.ExerciseViewModel;
import com.macros.agent.ui.screens.exercise.ExerciseViewModel_HiltModules;
import com.macros.agent.ui.screens.exercise.ExerciseViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.macros.agent.ui.screens.exercise.ExerciseViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.macros.agent.ui.screens.food.FoodDetailViewModel;
import com.macros.agent.ui.screens.food.FoodDetailViewModel_HiltModules;
import com.macros.agent.ui.screens.food.FoodDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.macros.agent.ui.screens.food.FoodDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.macros.agent.ui.screens.photo.PhotoViewModel;
import com.macros.agent.ui.screens.photo.PhotoViewModel_HiltModules;
import com.macros.agent.ui.screens.photo.PhotoViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.macros.agent.ui.screens.photo.PhotoViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.macros.agent.ui.screens.search.SearchViewModel;
import com.macros.agent.ui.screens.search.SearchViewModel_HiltModules;
import com.macros.agent.ui.screens.search.SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.macros.agent.ui.screens.search.SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.macros.agent.ui.screens.settings.GoalsViewModel;
import com.macros.agent.ui.screens.settings.GoalsViewModel_HiltModules;
import com.macros.agent.ui.screens.settings.GoalsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.macros.agent.ui.screens.settings.GoalsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DaggerMacrosApp_HiltComponents_SingletonC {
  private DaggerMacrosApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MacrosApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MacrosApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MacrosApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MacrosApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MacrosApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MacrosApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MacrosApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MacrosApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MacrosApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MacrosApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MacrosApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MacrosApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MacrosApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MacrosApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MacrosApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MacrosApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MacrosApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MacrosApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MacrosApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(ImmutableMap.<String, Boolean>builderWithExpectedSize(7).put(DiaryViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DiaryViewModel_HiltModules.KeyModule.provide()).put(ExerciseViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ExerciseViewModel_HiltModules.KeyModule.provide()).put(FoodDetailViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, FoodDetailViewModel_HiltModules.KeyModule.provide()).put(GoalsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, GoalsViewModel_HiltModules.KeyModule.provide()).put(PhotoViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PhotoViewModel_HiltModules.KeyModule.provide()).put(ProgressChartViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ProgressChartViewModel_HiltModules.KeyModule.provide()).put(SearchViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SearchViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends MacrosApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<DiaryViewModel> diaryViewModelProvider;

    private Provider<ExerciseViewModel> exerciseViewModelProvider;

    private Provider<FoodDetailViewModel> foodDetailViewModelProvider;

    private Provider<GoalsViewModel> goalsViewModelProvider;

    private Provider<PhotoViewModel> photoViewModelProvider;

    private Provider<ProgressChartViewModel> progressChartViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.diaryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.exerciseViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.foodDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.goalsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.photoViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.progressChartViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(ImmutableMap.<String, javax.inject.Provider<ViewModel>>builderWithExpectedSize(7).put(DiaryViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) diaryViewModelProvider)).put(ExerciseViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) exerciseViewModelProvider)).put(FoodDetailViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) foodDetailViewModelProvider)).put(GoalsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) goalsViewModelProvider)).put(PhotoViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) photoViewModelProvider)).put(ProgressChartViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) progressChartViewModelProvider)).put(SearchViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) searchViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return ImmutableMap.<Class<?>, Object>of();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.macros.agent.ui.screens.diary.DiaryViewModel 
          return (T) new DiaryViewModel(singletonCImpl.diaryRepositoryProvider.get(), singletonCImpl.goalsRepositoryProvider.get(), singletonCImpl.exerciseRepositoryProvider.get(), singletonCImpl.userMealRepositoryProvider.get());

          case 1: // com.macros.agent.ui.screens.exercise.ExerciseViewModel 
          return (T) new ExerciseViewModel(singletonCImpl.exerciseRepositoryProvider.get());

          case 2: // com.macros.agent.ui.screens.food.FoodDetailViewModel 
          return (T) new FoodDetailViewModel(singletonCImpl.foodRepositoryProvider.get(), singletonCImpl.diaryRepositoryProvider.get(), viewModelCImpl.savedStateHandle);

          case 3: // com.macros.agent.ui.screens.settings.GoalsViewModel 
          return (T) new GoalsViewModel(singletonCImpl.goalsRepositoryProvider.get());

          case 4: // com.macros.agent.ui.screens.photo.PhotoViewModel 
          return (T) new PhotoViewModel(singletonCImpl.geminiServiceProvider.get(), singletonCImpl.diaryRepositoryProvider.get(), singletonCImpl.geminiAnalysisDao());

          case 5: // com.macros.agent.ui.screens.diary.ProgressChartViewModel 
          return (T) new ProgressChartViewModel(singletonCImpl.diaryRepositoryProvider.get(), singletonCImpl.goalsRepositoryProvider.get());

          case 6: // com.macros.agent.ui.screens.search.SearchViewModel 
          return (T) new SearchViewModel(singletonCImpl.foodRepositoryProvider.get(), singletonCImpl.userMealRepositoryProvider.get(), singletonCImpl.diaryRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MacrosApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MacrosApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends MacrosApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<MacrosDatabase> provideDatabaseProvider;

    private Provider<DiaryRepository> diaryRepositoryProvider;

    private Provider<GoalsRepository> goalsRepositoryProvider;

    private Provider<GoogleFitService> googleFitServiceProvider;

    private Provider<ExerciseRepository> exerciseRepositoryProvider;

    private Provider<UserMealRepository> userMealRepositoryProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideUsdaRetrofitProvider;

    private Provider<UsdaApiService> provideUsdaApiServiceProvider;

    private Provider<FoodRepository> foodRepositoryProvider;

    private Provider<GeminiService> geminiServiceProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private DiaryDao diaryDao() {
      return DatabaseModule_ProvideDiaryDaoFactory.provideDiaryDao(provideDatabaseProvider.get());
    }

    private GoalsDao goalsDao() {
      return DatabaseModule_ProvideGoalsDaoFactory.provideGoalsDao(provideDatabaseProvider.get());
    }

    private ExerciseDao exerciseDao() {
      return DatabaseModule_ProvideExerciseDaoFactory.provideExerciseDao(provideDatabaseProvider.get());
    }

    private UserMealDao userMealDao() {
      return DatabaseModule_ProvideUserMealDaoFactory.provideUserMealDao(provideDatabaseProvider.get());
    }

    private FoodDao foodDao() {
      return DatabaseModule_ProvideFoodDaoFactory.provideFoodDao(provideDatabaseProvider.get());
    }

    private GeminiAnalysisDao geminiAnalysisDao() {
      return DatabaseModule_ProvideGeminiAnalysisDaoFactory.provideGeminiAnalysisDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<MacrosDatabase>(singletonCImpl, 1));
      this.diaryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DiaryRepository>(singletonCImpl, 0));
      this.goalsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<GoalsRepository>(singletonCImpl, 2));
      this.googleFitServiceProvider = DoubleCheck.provider(new SwitchingProvider<GoogleFitService>(singletonCImpl, 4));
      this.exerciseRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ExerciseRepository>(singletonCImpl, 3));
      this.userMealRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserMealRepository>(singletonCImpl, 5));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 9));
      this.provideUsdaRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 8));
      this.provideUsdaApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<UsdaApiService>(singletonCImpl, 7));
      this.foodRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FoodRepository>(singletonCImpl, 6));
      this.geminiServiceProvider = DoubleCheck.provider(new SwitchingProvider<GeminiService>(singletonCImpl, 10));
    }

    @Override
    public void injectMacrosApp(MacrosApp macrosApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return ImmutableSet.<Boolean>of();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private ExerciseRepository injectExerciseRepository(ExerciseRepository instance) {
      ExerciseRepository_MembersInjector.injectGoogleFitService(instance, googleFitServiceProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.macros.agent.data.repository.DiaryRepository 
          return (T) new DiaryRepository(singletonCImpl.diaryDao());

          case 1: // com.macros.agent.data.local.MacrosDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.macros.agent.data.repository.GoalsRepository 
          return (T) new GoalsRepository(singletonCImpl.goalsDao());

          case 3: // com.macros.agent.data.repository.ExerciseRepository 
          return (T) singletonCImpl.injectExerciseRepository(ExerciseRepository_Factory.newInstance(singletonCImpl.exerciseDao()));

          case 4: // com.macros.agent.data.remote.api.GoogleFitService 
          return (T) new GoogleFitService(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // com.macros.agent.data.repository.UserMealRepository 
          return (T) new UserMealRepository(singletonCImpl.userMealDao());

          case 6: // com.macros.agent.data.repository.FoodRepository 
          return (T) new FoodRepository(singletonCImpl.foodDao(), singletonCImpl.provideUsdaApiServiceProvider.get(), NetworkModule_ProvideUsdaApiKeyFactory.provideUsdaApiKey());

          case 7: // com.macros.agent.data.remote.api.UsdaApiService 
          return (T) NetworkModule_ProvideUsdaApiServiceFactory.provideUsdaApiService(singletonCImpl.provideUsdaRetrofitProvider.get());

          case 8: // @javax.inject.Named("usda") retrofit2.Retrofit 
          return (T) NetworkModule_ProvideUsdaRetrofitFactory.provideUsdaRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 9: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 10: // com.macros.agent.data.remote.api.GeminiService 
          return (T) new GeminiService();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
