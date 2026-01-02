package com.macros.agent.ui.screens.diary;

import com.macros.agent.data.repository.DiaryRepository;
import com.macros.agent.data.repository.ExerciseRepository;
import com.macros.agent.data.repository.GoalsRepository;
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
public final class DiaryViewModel_Factory implements Factory<DiaryViewModel> {
  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<GoalsRepository> goalsRepositoryProvider;

  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  private final Provider<UserMealRepository> userMealRepositoryProvider;

  public DiaryViewModel_Factory(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GoalsRepository> goalsRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<UserMealRepository> userMealRepositoryProvider) {
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.goalsRepositoryProvider = goalsRepositoryProvider;
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
    this.userMealRepositoryProvider = userMealRepositoryProvider;
  }

  @Override
  public DiaryViewModel get() {
    return newInstance(diaryRepositoryProvider.get(), goalsRepositoryProvider.get(), exerciseRepositoryProvider.get(), userMealRepositoryProvider.get());
  }

  public static DiaryViewModel_Factory create(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GoalsRepository> goalsRepositoryProvider,
      Provider<ExerciseRepository> exerciseRepositoryProvider,
      Provider<UserMealRepository> userMealRepositoryProvider) {
    return new DiaryViewModel_Factory(diaryRepositoryProvider, goalsRepositoryProvider, exerciseRepositoryProvider, userMealRepositoryProvider);
  }

  public static DiaryViewModel newInstance(DiaryRepository diaryRepository,
      GoalsRepository goalsRepository, ExerciseRepository exerciseRepository,
      UserMealRepository userMealRepository) {
    return new DiaryViewModel(diaryRepository, goalsRepository, exerciseRepository, userMealRepository);
  }
}
