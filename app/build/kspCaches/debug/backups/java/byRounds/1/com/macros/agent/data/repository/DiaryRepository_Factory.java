package com.macros.agent.data.repository;

import com.macros.agent.data.local.dao.DiaryDao;
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
public final class DiaryRepository_Factory implements Factory<DiaryRepository> {
  private final Provider<DiaryDao> diaryDaoProvider;

  public DiaryRepository_Factory(Provider<DiaryDao> diaryDaoProvider) {
    this.diaryDaoProvider = diaryDaoProvider;
  }

  @Override
  public DiaryRepository get() {
    return newInstance(diaryDaoProvider.get());
  }

  public static DiaryRepository_Factory create(Provider<DiaryDao> diaryDaoProvider) {
    return new DiaryRepository_Factory(diaryDaoProvider);
  }

  public static DiaryRepository newInstance(DiaryDao diaryDao) {
    return new DiaryRepository(diaryDao);
  }
}
