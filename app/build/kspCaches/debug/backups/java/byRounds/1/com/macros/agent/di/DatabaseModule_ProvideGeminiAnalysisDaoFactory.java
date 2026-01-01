package com.macros.agent.di;

import com.macros.agent.data.local.MacrosDatabase;
import com.macros.agent.data.local.dao.GeminiAnalysisDao;
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
public final class DatabaseModule_ProvideGeminiAnalysisDaoFactory implements Factory<GeminiAnalysisDao> {
  private final Provider<MacrosDatabase> databaseProvider;

  public DatabaseModule_ProvideGeminiAnalysisDaoFactory(Provider<MacrosDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public GeminiAnalysisDao get() {
    return provideGeminiAnalysisDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideGeminiAnalysisDaoFactory create(
      Provider<MacrosDatabase> databaseProvider) {
    return new DatabaseModule_ProvideGeminiAnalysisDaoFactory(databaseProvider);
  }

  public static GeminiAnalysisDao provideGeminiAnalysisDao(MacrosDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideGeminiAnalysisDao(database));
  }
}
