package com.zajel.app;
import android.app.Application;
import com.zajel.app.core.AppContainer;
public final class ZajelApplication extends Application { private AppContainer container; @Override public void onCreate(){super.onCreate();container=new AppContainer(this);} public AppContainer container(){return container;} }
