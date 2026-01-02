package com.macros.agent.ui.screens.search;

import com.macros.agent.data.repository.DiaryRepository;
import com.macros.agent.data.repository.FoodRepository;
import com.macros.agent.data.repository.UserMealRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<FoodRepository> foodRepositoryProvider;

  private final Provider<UserMealRepository> userMealRepositoryProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  public SearchViewModel_Factory(Provider<FoodRepository> foodRepositoryProvider,
      Provider<UserMealRepository> userMealRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    this.foodRepositoryProvider = foodRepositoryProvider;
    this.userMealRepositoryProvider = userMealRepositoryProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(foodRepositoryProvider.get(), userMealRepositoryProvider.get(), diaryRepositoryProvider.get());
  }

  public static SearchViewModel_Factory create(Provider<FoodRepository> foodRepositoryProvider,
      Provider<UserMealRepository> userMealRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    return new SearchViewModel_Factory(foodRepositoryProvider, userMealRepositoryProvider, diaryRepositoryProvider);
  }

  public static SearchViewModel newInstance(FoodRepository foodRepository,
      UserMealRepository userMealRepository, DiaryRepository diaryRepository) {
    return new SearchViewModel(foodRepository, userMealRepository, diaryRepository);
  }
}
