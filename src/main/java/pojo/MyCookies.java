package pojo;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyCookies implements CookieJar {
    HashMap<String, List<Cookie>> cookieStore;
    public MyCookies() {
        super();
    }
    public MyCookies(HashMap<String, List<Cookie>> cookieStore) {
        this.cookieStore = cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        if (cookieStore.get(httpUrl.host()) == null) {
//            List<Cookie>是由Array.asList转化而来，不能直接add,remove
            ArrayList<Cookie> arrayList = new ArrayList<>(list);
            cookieStore.put(httpUrl.host(), arrayList);
        } else {
            for (Cookie cookie : list) {
                cookieStore.get(httpUrl.host()).add(cookie);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        return cookies != null ? cookies : new ArrayList<Cookie>();
    }
}
