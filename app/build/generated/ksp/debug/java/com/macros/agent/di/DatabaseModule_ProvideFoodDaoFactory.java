package com.macros.agent.di;

import com.macros.agent.data.local.MacrosDatabase;
import com.macros.agent.data.local.dao.FoodDao;
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
public final class DatabaseModule_ProvideFoodDaoFactory implements Factory<FoodDao> {
  private final Provider<MacrosDatabase> databaseProvider;

  public DatabaseModule_ProvideFoodDaoFactory(Provider<MacrosDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FoodDao get() {
    return provideFoodDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideFoodDaoFactory create(
      Provider<MacrosDatabase> databaseProvider) {
    return new DatabaseModule_ProvideFoodDaoFactory(databaseProvider);
  }

  public static FoodDao provideFoodDao(MacrosDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideFoodDao(database));
  }
}
