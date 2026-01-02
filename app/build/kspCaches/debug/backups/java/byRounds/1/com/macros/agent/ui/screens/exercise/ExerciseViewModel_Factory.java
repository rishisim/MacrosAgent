package com.macros.agent.ui.screens.exercise;

import com.macros.agent.data.repository.ExerciseRepository;
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
public final class ExerciseViewModel_Factory implements Factory<ExerciseViewModel> {
  private final Provider<ExerciseRepository> exerciseRepositoryProvider;

  public ExerciseViewModel_Factory(Provider<ExerciseRepository> exerciseRepositoryProvider) {
    this.exerciseRepositoryProvider = exerciseRepositoryProvider;
  }

  @Override
  public ExerciseViewModel get() {
    return newInstance(exerciseRepositoryProvider.get());
  }

  public static ExerciseViewModel_Factory create(
      Provider<ExerciseRepository> exerciseRepositoryProvider) {
    return new ExerciseViewModel_Factory(exerciseRepositoryProvider);
  }

  public static ExerciseViewModel newInstance(ExerciseRepository exerciseRepository) {
    return new ExerciseViewModel(exerciseRepository);
  }
}
