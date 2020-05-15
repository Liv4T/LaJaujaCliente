package com.dybcatering.lajauja.PaGo;


import android.app.Activity;
import android.os.CountDownTimer;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
//Se utilizó la librería httpcore 4.4.7
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
//Se utilizó la librería Android-async-http 1.4.4.
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import java.io.UnsupportedEncodingException;

public class PaGoLib {

    final static String url = "https://"; //url provista por CredibanCo
    final static String bearerAuth = ""; //Codigo de Autenticación provisto por CredibanCo
    final static int uniqueCode = 000000; //Codigo único provisto por CredibanCo
    static String responseWs, paymentState; //variables correspondientes al token de sesión y al estado delpago
    static StringEntity entity;
    static WebView wV;
    static int transactionState; //Código del resultado de la transacción

    public static void invokeWS(final Activity activity, String value, String tax, String desc) {
        try {
 /*
 * Se crea el objeto JSON que se enviará al servicio para recibir la dirección del pago:
 * El objeto JSON tiene el siguiente formato:
 {
 "value":000000, //valor de la transacción en formato numérico
 "description":"AAAAAAAA", //descripción de la transacción
 "commerce": {
 "uniqueCode":000000000 //Código único provisto por CredibanCo
 }
 }
 */
            JSONObject json = new JSONObject();
            json.put("value", value);
            json.put("tax", tax);
            json.put("description", desc);
            JSONObject commerce = new JSONObject();
            commerce.put("uniqueCode", uniqueCode);
            json.put("commerce", commerce);
            //Se convierte el objeto y se configura el tipo de contenido a ser entregado al servicio
            entity = new StringEntity(json.toString());
            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            //Se crea el cliente Http asíncrono con el que se realizará el llamado al servicio
            AsyncHttpClient client = new AsyncHttpClient();
            //Se agregan los headers correspondientes:
            //Código del comercio
            client.addHeader("Commerce", String.valueOf(uniqueCode));
            //Código de autenticación
            client.addHeader("Authorization", bearerAuth);
            //Se realiza el llamado con el método POST al servicio de PaGo, disponiendo un Handler para la        respuesta
            client.post(activity.getApplicationContext(), url + "/ws/payment", entity, "application/json", new
                    AsyncHttpResponseHandler() {
                        //Método de respuesta en caso de éxito en el llamado
                        public void onSuccess(String response) {
                            try {
                                //Se crea un objeto que recibirá la respuesta del servicio
                                JSONObject obj = new JSONObject(response);
                                /*
                                 * El objeto JSON resultante tiene el siguiente formato:
                                 * {
                                 * "responseCode": "00", //Código de respuesta
                                 * "responseDescription": "AAAAAAA", //Descripción de la respuesta
                                 * "responseEntity": {
                                 * "paymentId": 000, //Identificador del pago
                                 * "value": 000000, //Valor del pago
                                 * "tax": 000, //Impuestos asociados al pago
                                 * "description": "AAAAAAA", //Descripción del pago
                                 * "urlToken": "", //Token de identificación de la sesión del usuario
                                 * "paymentState": {
                                 * "paymentStateId": 0 //Estado del pago
                                 * }
                                 * }
                                 * }
                                 */
                                //Si el código de respuesta es "01", el servicio se ejecutó correctamente
                                if (obj.getString("responseCode").equals("01")) {
                                    //Captura del token de sesión del usuario
                                    responseWs = obj.getJSONObject("responseEntity").getString("urlToken");
                                    //Creación y parametrización del WebView en el que se mostrará la interfaz del pago
                                    wV = new WebView(activity);
                                    wV.setWebViewClient(new WebViewClient());
                                    wV.setWebChromeClient(new WebChromeClient());
                                    wV.getSettings().setJavaScriptEnabled(true);
                                    //Carga de la interfaz del servicio
                                    wV.loadUrl(url + "/faces/payment.xhtml?payment_id=" + responseWs);
                                    activity.setContentView((wV));
                                    //Llamado al servicio que realizará la comprobación del pago
                                    invokeTrxWS(activity, responseWs);
                                }
                                //Si el código de respuesta es diferente a "01" ha ocurrido un error en el servicio
                                else {
//Sección para controlar errores en el llamado al servicio
                                }
                            }
                            //Captura de excepción en caso de problemas con la librería de JSON
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //Método de respuesta en caso de falla en el llamado
                        //Los parámetros para este método son:
                        //int statusCode: Código de estado para el error, los valores más comunes son 404 (Servicio no encontrado) o 500 (Error de servidor)
                        //Throwable error: Error provisto por el método
                        //String content: Contenido de error provisto por el método
                        @Override
                        public void onFailure(int statusCode, Throwable error, String content) {
                            //En esta sección deben implementarse las acciones en el evento de falla del servicio
                            if (statusCode == 404) {
                            } else if (statusCode == 500) {
                            } else {
                            }
                        }
                    });
        }
        //Captura de excepción en caso de problemas con la librería de JSON
        catch (JSONException e) {
            e.printStackTrace();
        }
        //Captura de excepción en caso de problemas con la codificación del objeto JSON
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Método que realiza la repetición de la consulta al servicio de pago
    //Los parámetros para este método son:
    //Activity activity : Actividad sobre la cual se realiza el llamado al servicio de estado del pago
    private static void checkPayment(final Activity activity) {
        //Se instancia una cuenta regresiva con duración de 2 segundos para realizar la consulta al servicio
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                invokeTrxWS(activity, responseWs);
            }
        }.start();
    }
    //Método que realiza la llamada al servicio de estado del Pago
    //Los parámetros para este método son:
    //Activity activity : Actividad sobre la cual se realiza el llamado al servicio de estado del pago
    //String token : Token de sesión del usuario
    public static void invokeTrxWS(final Activity activity, String token) {
        //Se crea el cliente Http asíncrono con el que se realizará el llamado al servicio
        AsyncHttpClient client = new AsyncHttpClient();
        //Se agregan los headers correspondientes:
        //Código del comercio
        client.addHeader("Commerce", String.valueOf(uniqueCode));
        //Código de autenticación
        client.addHeader("Authorization", bearerAuth);
        //Se realiza el llamado con el método GET al servicio de PaGo, disponiendo un Handler para la  respuesta
        client.get(url + "/ws/payment/" + token, new AsyncHttpResponseHandler() {
            //Método de respuesta en caso de éxito en el llamado
            public void onSuccess(String response) {
                try {
                    //Se crea un objeto que recibirá la respuesta del servicio
                    JSONObject obj = new JSONObject(response);
                    /* El objeto JSON resultante tiene el siguiente formato
                     * {
                     * "responseCode": "00", //Código de respuesta
                     * "responseDescription": "AAAAA", //Descripción de la respuesta
                     * "responseEntity": {
                     * "paymentId": 00, //Identificador de la transacción
                     * "value": 000000, //Valor de la transacción
                     * "tax": 000, //Impuestos asociados a la transacción
                     * "description": "AAAAAAA", //Descripción de la transacción
                     * "urlToken": "", //Token de identificación de la sesión
                     * "paymentState": {
                     * "paymentStateId": 0, //Id de estado de la transacción
                     * "description": "AAAAAAA" //Descripción del estado de la transacción
                     * }
                     * }
                     * }
                     */

                    //Si el código de respuesta es "01", el servicio se ejecutó correctamente
                    if (obj.getString("responseCode").equals("01")) {
                        //Captura del estado del pago
                        paymentState =
                                obj.getJSONObject("responseEntity").getJSONObject("paymentState").getString("description");
                        //Captura del código de estado del pago
                        transactionState =
                                obj.getJSONObject("responseEntity").getJSONObject("paymentState").getInt("paymentStateId");
                        //Si el código de transacción es 3, el pago se realizó exitosamente
                        if (transactionState == 3) {
                            //Sección para controlar el pago exitoso
                            //Si el código de transacción es 1, la transacción no se ha llevado a cabo
                        } else if (transactionState == 1) {
                            checkPayment(activity);
                            //Sección para implementar acciones de acuerdo al código de transacción recibido
                        } else{

                        }
                        /**/
                    }
                    //Si el código de respuesta es diferente a "01" ha ocurrido un error en el servicio
                    else {
                    //Sección para controlar errores en el llamado al servicio
                    }
                }
                //Captura de excepción en caso de problemas con la librería de JSON
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //Método de respuesta en caso de falla en el llamado
            //Los parámetros para este método son:
            // int statusCode: Código de estado para el error, los valores más comunes son 404 (Servicio noencontrado) o 500 (Error de servidor)
            //Throwable error: Error provisto por el método
            //String content: Contenido de error provisto por el método
            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                //En esta sección deben implementarse las acciones en el evento de falla del servicio
                if (statusCode == 404) {
                } else if (statusCode == 500) {
                } else {
                }
            }
        });
    }
}
