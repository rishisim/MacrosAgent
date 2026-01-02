package com.macros.agent.data.remote.api;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class GoogleFitService_Factory implements Factory<GoogleFitService> {
  private final Provider<Context> contextProvider;

  public GoogleFitService_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public GoogleFitService get() {
    return newInstance(contextProvider.get());
  }

  public static GoogleFitService_Factory create(Provider<Context> contextProvider) {
    return new GoogleFitService_Factory(contextProvider);
  }

  public static GoogleFitService newInstance(Context context) {
    return new GoogleFitService(context);
  }
}
