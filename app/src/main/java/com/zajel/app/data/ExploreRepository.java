package com.zajel.app.data;

import com.zajel.app.domain.*;
import java.util.List;

public interface ExploreRepository {
    void search(String query, Callback<List<ExploreItem>> callback);
    void suggestions(Callback<List<PublicSuggestion>> callback);
    void publicAds(Callback<List<PublicAd>> callback);
    interface Callback<T> { void success(T value); void error(Exception error); }
}
