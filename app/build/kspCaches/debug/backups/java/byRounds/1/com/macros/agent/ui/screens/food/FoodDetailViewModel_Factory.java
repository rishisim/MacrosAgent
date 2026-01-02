package com.macros.agent.ui.screens.food;

import androidx.lifecycle.SavedStateHandle;
import com.macros.agent.data.repository.DiaryRepository;
import com.macros.agent.data.repository.FoodRepository;
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
public final class FoodDetailViewModel_Factory implements Factory<FoodDetailViewModel> {
  private final Provider<FoodRepository> foodRepositoryProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public FoodDetailViewModel_Factory(Provider<FoodRepository> foodRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.foodRepositoryProvider = foodRepositoryProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public FoodDetailViewModel get() {
    return newInstance(foodRepositoryProvider.get(), diaryRepositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static FoodDetailViewModel_Factory create(Provider<FoodRepository> foodRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new FoodDetailViewModel_Factory(foodRepositoryProvider, diaryRepositoryProvider, savedStateHandleProvider);
  }

  public static FoodDetailViewModel newInstance(FoodRepository foodRepository,
      DiaryRepository diaryRepository, SavedStateHandle savedStateHandle) {
    return new FoodDetailViewModel(foodRepository, diaryRepository, savedStateHandle);
  }
}
