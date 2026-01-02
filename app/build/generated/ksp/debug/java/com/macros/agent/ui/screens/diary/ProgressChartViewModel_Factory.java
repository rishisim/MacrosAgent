package com.macros.agent.ui.screens.diary;

import com.macros.agent.data.repository.DiaryRepository;
import com.macros.agent.data.repository.GoalsRepository;
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
public final class ProgressChartViewModel_Factory implements Factory<ProgressChartViewModel> {
  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<GoalsRepository> goalsRepositoryProvider;

  public ProgressChartViewModel_Factory(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GoalsRepository> goalsRepositoryProvider) {
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.goalsRepositoryProvider = goalsRepositoryProvider;
  }

  @Override
  public ProgressChartViewModel get() {
    return newInstance(diaryRepositoryProvider.get(), goalsRepositoryProvider.get());
  }

  public static ProgressChartViewModel_Factory create(
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GoalsRepository> goalsRepositoryProvider) {
    return new ProgressChartViewModel_Factory(diaryRepositoryProvider, goalsRepositoryProvider);
  }

  public static ProgressChartViewModel newInstance(DiaryRepository diaryRepository,
      GoalsRepository goalsRepository) {
    return new ProgressChartViewModel(diaryRepository, goalsRepository);
  }
}
