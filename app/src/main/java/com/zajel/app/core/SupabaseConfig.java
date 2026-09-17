package com.zajel.app.core;
import com.zajel.app.BuildConfig;
public final class SupabaseConfig { private SupabaseConfig(){} public static boolean isConfigured(){return !BuildConfig.SUPABASE_URL.isEmpty()&&!BuildConfig.SUPABASE_ANON_KEY.isEmpty();} public static String url(){return BuildConfig.SUPABASE_URL;} public static String key(){return BuildConfig.SUPABASE_ANON_KEY;} }
