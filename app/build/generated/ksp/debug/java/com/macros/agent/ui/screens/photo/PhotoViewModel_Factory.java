package com.macros.agent.ui.screens.photo;

import com.macros.agent.data.local.dao.GeminiAnalysisDao;
import com.macros.agent.data.remote.api.GeminiService;
import com.macros.agent.data.repository.DiaryRepository;
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
public final class PhotoViewModel_Factory implements Factory<PhotoViewModel> {
  private final Provider<GeminiService> geminiServiceProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<GeminiAnalysisDao> geminiAnalysisDaoProvider;

  public PhotoViewModel_Factory(Provider<GeminiService> geminiServiceProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GeminiAnalysisDao> geminiAnalysisDaoProvider) {
    this.geminiServiceProvider = geminiServiceProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.geminiAnalysisDaoProvider = geminiAnalysisDaoProvider;
  }

  @Override
  public PhotoViewModel get() {
    return newInstance(geminiServiceProvider.get(), diaryRepositoryProvider.get(), geminiAnalysisDaoProvider.get());
  }

  public static PhotoViewModel_Factory create(Provider<GeminiService> geminiServiceProvider,
      Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<GeminiAnalysisDao> geminiAnalysisDaoProvider) {
    return new PhotoViewModel_Factory(geminiServiceProvider, diaryRepositoryProvider, geminiAnalysisDaoProvider);
  }

  public static PhotoViewModel newInstance(GeminiService geminiService,
      DiaryRepository diaryRepository, GeminiAnalysisDao geminiAnalysisDao) {
    return new PhotoViewModel(geminiService, diaryRepository, geminiAnalysisDao);
  }
}
