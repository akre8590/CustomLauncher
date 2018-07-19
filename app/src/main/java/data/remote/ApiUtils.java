package data.remote;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "https://retrofit2androidmysqlphp.000webhostapp.com/";
    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
