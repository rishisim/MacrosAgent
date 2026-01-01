package com.macros.agent.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("javax.inject.Named")
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
public final class NetworkModule_ProvideUsdaApiKeyFactory implements Factory<String> {
  @Override
  public String get() {
    return provideUsdaApiKey();
  }

  public static NetworkModule_ProvideUsdaApiKeyFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provideUsdaApiKey() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideUsdaApiKey());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideUsdaApiKeyFactory INSTANCE = new NetworkModule_ProvideUsdaApiKeyFactory();
  }
}
