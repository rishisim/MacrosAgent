package com.macros.agent.ui.screens.settings;

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
public final class GoalsViewModel_Factory implements Factory<GoalsViewModel> {
  private final Provider<GoalsRepository> goalsRepositoryProvider;

  public GoalsViewModel_Factory(Provider<GoalsRepository> goalsRepositoryProvider) {
    this.goalsRepositoryProvider = goalsRepositoryProvider;
  }

  @Override
  public GoalsViewModel get() {
    return newInstance(goalsRepositoryProvider.get());
  }

  public static GoalsViewModel_Factory create(Provider<GoalsRepository> goalsRepositoryProvider) {
    return new GoalsViewModel_Factory(goalsRepositoryProvider);
  }

  public static GoalsViewModel newInstance(GoalsRepository goalsRepository) {
    return new GoalsViewModel(goalsRepository);
  }
}
