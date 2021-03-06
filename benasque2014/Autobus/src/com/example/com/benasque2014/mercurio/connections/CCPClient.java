package com.example.com.benasque2014.mercurio.connections;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;

import android.util.Log;

import com.example.com.benasque2014.mercurio.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class CCPClient {

	private static AsyncHttpClient client = new AsyncHttpClient();
	public static final String BASE_URL = "http://estilonline.com";
	//public static final String BASE_URL = "http://develop.cuantocomic.com";
	public static final String FOLDER_IMG = "/img/";
	public static final String API_URL = BASE_URL + "/experiments/mibus";

	private static int requestIndex = 0;
	private static Map<Integer, RequestHandle> requests = new HashMap<Integer, RequestHandle>();
	private static Gson gson;

	/* init */
	static {
		gson = new Gson();
	}

	public static void logout() {
		//String url = "/logout";
		//post(url, null, null);
		//KeyStoreController.getKeyStore().setUserStatus(0);
		//myCookieStore.clear();
		//client.setCookieStore(myCookieStore);
	}

	/** Client methods **/

	/* GCM */

	

	public static int getRutas(final CCPClientHandle handle) {
		String url = "/ws.php";
		RequestParams params = new RequestParams();
		params.add("tipo", "get_rutas");
		return post(url, params, handle);
	}

	public static int delRuta(String codigo, final CCPClientHandle handle) {
		String url = "/ws.php";
		RequestParams params = new RequestParams();
		params.add("tipo", "eliminar_ruta");
		params.add("Codigo", codigo + "");
		return post(url, params, handle);
	}
	
	public static int hacer_ruta(String idTelefono,String codigo, String mensaje,double latitud, double longitud, final CCPClientHandle handle) {
		String url = "/ws.php";
		RequestParams params = new RequestParams();
		params.add("tipo", "hacer_ruta");
		
		params.add("IdTelefono", idTelefono + "");
		params.add("Nombre", "Bus");
		params.add("Codigo", codigo + "");
		params.add("Mensaje", mensaje + "");
		params.add("Latitud", latitud + "");
		params.add("Longitud", longitud + "");
		
		return post(url, params, handle);
	}
	
	public static int guardarRuta(String nombre, String codigo, String clase,
			String horaInicio, String horaFin, String frecuenciaPaso, String periodicidad,
			String listaParadas, String incidencias, final CCPClientHandle handle) {
		String url = "/ws.php";
		RequestParams params = new RequestParams();
		params.add("tipo", "guardar_ruta");
		params.add("Nombre", nombre + "");
		params.add("Codigo", codigo + "");
		params.add("Clase", clase + "");
		params.add("HoraInicio", horaInicio + "");
		params.add("HoraFin", horaFin + "");
		params.add("FrecuenciaDePaso", frecuenciaPaso + "");
		params.add("Periodicidad", periodicidad + "");
		params.add("ListaDeParadas", listaParadas + "");
		params.add("Indicendias", incidencias);
		return post(url, params, handle);
	}
	
	/* USERS */

	public static int dejarRutaBuses(String IdTelefono, final CCPClientHandle handle) {
		String url = "/ws.php";
		RequestParams params = new RequestParams();
		params.add("tipo", "dejar_ruta");
		params.add("IdTelefono", IdTelefono + "");
		return post(url, params, handle);
	}
	
	public static int verBuses(String codigo, final CCPClientHandle handle) {
		String url = "/ws.php";
		RequestParams params = new RequestParams();
		params.add("tipo", "ver_buses");
		params.add("Codigo", codigo + "");
		return post(url, params, handle);
	}

	
	public static int customPost(String url, RequestParams params,
			final CCPClientHandle handle) {
		return post(url, params, handle);
	}

	public static void cancelRequest(int requestId) {
		cancelRequest(requestId, true);
	}

	public static void cancelRequest(int requestId, boolean force) {
		RequestHandle rh = requests.get(requestId);
		if (rh != null) {
			requests.remove(requestId).cancel(force);
		}
	}

	private static int addRequest(RequestHandle handle) {
		int temp = requestIndex;
		requests.put(requestIndex++, handle);
		return temp;
	}

	/**
	 * @return
	 ***********/

	private static int post(String url, RequestParams params,
			final CCPClientHandle handle) {

		return addRequest(client.post(API_URL + url, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						if (handle != null)
							handle.result(true, null);
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						String json = new String(arg2);
						if (BuildConfig.DEBUG)
							Log.e("SERVER_RESPONSE", json);
						JsonParser parser = new JsonParser();
						//JsonObject object = null;
						JsonArray arr=null;
						try {
							//object = parser.parse(json).getAsJson();
							arr=parser.parse(json).getAsJsonArray();
							

								if (handle != null)
									handle.result(false, arr);
							

						} catch (Exception e) {
							
							e.printStackTrace();
							if (handle != null)
								handle.result(true, null);
						}
					}

				}));
	}

	public interface CCPClientHandle {
		public void result(boolean error, JsonArray data);
	}

	public static String getImageUrl(int image_id, String string) {
		return getImageUrl(image_id) + "/" + string;
	}

	public static String getImageUrl(int image_id) {
		return BASE_URL + "/img/" + image_id;
	}

}
