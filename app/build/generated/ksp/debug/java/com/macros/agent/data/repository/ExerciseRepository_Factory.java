package com.macros.agent.data.repository;

import com.macros.agent.data.local.dao.ExerciseDao;
import com.macros.agent.data.remote.api.GoogleFitService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ExerciseRepository_Factory implements Factory<ExerciseRepository> {
  private final Provider<ExerciseDao> exerciseDaoProvider;

  private final Provider<GoogleFitService> googleFitServiceProvider;

  public ExerciseRepository_Factory(Provider<ExerciseDao> exerciseDaoProvider,
      Provider<GoogleFitService> googleFitServiceProvider) {
    this.exerciseDaoProvider = exerciseDaoProvider;
    this.googleFitServiceProvider = googleFitServiceProvider;
  }

  @Override
  public ExerciseRepository get() {
    ExerciseRepository instance = newInstance(exerciseDaoProvider.get());
    ExerciseRepository_MembersInjector.injectGoogleFitService(instance, googleFitServiceProvider.get());
    return instance;
  }

  public static ExerciseRepository_Factory create(Provider<ExerciseDao> exerciseDaoProvider,
      Provider<GoogleFitService> googleFitServiceProvider) {
    return new ExerciseRepository_Factory(exerciseDaoProvider, googleFitServiceProvider);
  }

  public static ExerciseRepository newInstance(ExerciseDao exerciseDao) {
    return new ExerciseRepository(exerciseDao);
  }
}
