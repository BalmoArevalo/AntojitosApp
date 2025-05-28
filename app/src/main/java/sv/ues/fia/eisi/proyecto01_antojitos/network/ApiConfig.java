package sv.ues.fia.eisi.proyecto01_antojitos.network;

public class ApiConfig {
    // Dirección del servidor local (emulador usa 10.0.2.2)
    public static final String BASE_IP = "http://10.0.2.2";
    public static final String API_PATH = "/antojitos_api/";

    public static String getBaseUrl() {
        return BASE_IP + API_PATH;
    }
}
