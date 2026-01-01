package com.macros.agent.di;

import com.macros.agent.data.local.MacrosDatabase;
import com.macros.agent.data.local.dao.DiaryDao;
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
public final class DatabaseModule_ProvideDiaryDaoFactory implements Factory<DiaryDao> {
  private final Provider<MacrosDatabase> databaseProvider;

  public DatabaseModule_ProvideDiaryDaoFactory(Provider<MacrosDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DiaryDao get() {
    return provideDiaryDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideDiaryDaoFactory create(
      Provider<MacrosDatabase> databaseProvider) {
    return new DatabaseModule_ProvideDiaryDaoFactory(databaseProvider);
  }

  public static DiaryDao provideDiaryDao(MacrosDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideDiaryDao(database));
  }
}
