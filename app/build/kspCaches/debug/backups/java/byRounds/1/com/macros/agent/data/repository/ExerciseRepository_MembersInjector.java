package com.macros.agent.data.repository;

import com.macros.agent.data.remote.api.GoogleFitService;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class ExerciseRepository_MembersInjector implements MembersInjector<ExerciseRepository> {
  private final Provider<GoogleFitService> googleFitServiceProvider;

  public ExerciseRepository_MembersInjector(Provider<GoogleFitService> googleFitServiceProvider) {
    this.googleFitServiceProvider = googleFitServiceProvider;
  }

  public static MembersInjector<ExerciseRepository> create(
      Provider<GoogleFitService> googleFitServiceProvider) {
    return new ExerciseRepository_MembersInjector(googleFitServiceProvider);
  }

  @Override
  public void injectMembers(ExerciseRepository instance) {
    injectGoogleFitService(instance, googleFitServiceProvider.get());
  }

  @InjectedFieldSignature("com.macros.agent.data.repository.ExerciseRepository.googleFitService")
  public static void injectGoogleFitService(ExerciseRepository instance,
      GoogleFitService googleFitService) {
    instance.googleFitService = googleFitService;
  }
}
