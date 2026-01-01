package com.macros.agent.di;

import com.macros.agent.data.local.MacrosDatabase;
import com.macros.agent.data.local.dao.GoalsDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideGoalsDaoFactory implements Factory<GoalsDao> {
  private final Provider<MacrosDatabase> databaseProvider;

  public DatabaseModule_ProvideGoalsDaoFactory(Provider<MacrosDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public GoalsDao get() {
    return provideGoalsDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideGoalsDaoFactory create(
      Provider<MacrosDatabase> databaseProvider) {
    return new DatabaseModule_ProvideGoalsDaoFactory(databaseProvider);
  }

  public static GoalsDao provideGoalsDao(MacrosDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGoalsDao(database));
  }
}
