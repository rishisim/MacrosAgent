package com.macros.agent.data.repository;

import com.macros.agent.data.local.dao.GoalsDao;
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
public final class GoalsRepository_Factory implements Factory<GoalsRepository> {
  private final Provider<GoalsDao> goalsDaoProvider;

  public GoalsRepository_Factory(Provider<GoalsDao> goalsDaoProvider) {
    this.goalsDaoProvider = goalsDaoProvider;
  }

  @Override
  public GoalsRepository get() {
    return newInstance(goalsDaoProvider.get());
  }

  public static GoalsRepository_Factory create(Provider<GoalsDao> goalsDaoProvider) {
    return new GoalsRepository_Factory(goalsDaoProvider);
  }

  public static GoalsRepository newInstance(GoalsDao goalsDao) {
    return new GoalsRepository(goalsDao);
  }
}
