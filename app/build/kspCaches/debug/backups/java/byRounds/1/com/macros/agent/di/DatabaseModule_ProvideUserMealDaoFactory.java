package com.macros.agent.di;

import com.macros.agent.data.local.MacrosDatabase;
import com.macros.agent.data.local.dao.UserMealDao;
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
public final class DatabaseModule_ProvideUserMealDaoFactory implements Factory<UserMealDao> {
  private final Provider<MacrosDatabase> databaseProvider;

  public DatabaseModule_ProvideUserMealDaoFactory(Provider<MacrosDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public UserMealDao get() {
    return provideUserMealDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideUserMealDaoFactory create(
      Provider<MacrosDatabase> databaseProvider) {
    return new DatabaseModule_ProvideUserMealDaoFactory(databaseProvider);
  }

  public static UserMealDao provideUserMealDao(MacrosDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUserMealDao(database));
  }
}
