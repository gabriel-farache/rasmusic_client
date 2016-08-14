package gabi.appli.rasmusic.utils;

import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.File;
import java.io.IOException;

import gabi.appli.rasmusic.main.RasMusicMain;
import gabi.appli.rasmusic.main.RasMusicMainFragment;

import static gabi.appli.rasmusic.utils.HTTPUtil.HTTPActions.*;

/**
 * Created by gabi on 26/03/16.
 */
public class HTTPUtil extends AsyncTask<String, String, String> {

    private HttpClient httpClient;
    private String serverAddr;

    public enum HTTPActions {
        ADDANDPLAY,
        ADDTOPLAYLIST,
        PLAYSEPAUSE,
        STOP,
        NEXT,
        SOUND_UP,
        SOUND_DOWN,
        SET_SOUND_LEVEL,
        GET_PLAYLIST,
        PLAY_SONG_IN_PLAYLIST
    }

    ;

    @Override
    protected String doInBackground(String... params) {
        httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;
        String jsonRequest;
        serverAddr = params[0];
        HTTPActions actionToPerform = valueOf(params[1]);
        HttpGet httpGet;
        HttpPost httpPost;
        HttpPatch httpPatch;
        HttpPut httpPut;
        StatusLine statusLine;
        String encodedSong;
        File song;
        String responseString = null;
        try {


            switch (actionToPerform) {
                case ADDANDPLAY:
                    song = new File(params[2]);
                    encodedSong = Base64.encodeToString(FileUtils.readFileToByteArray(song),
                            Base64.NO_WRAP | Base64.URL_SAFE);

                    jsonRequest = "{" +
                            "\"title\": \"" + song.getName() + "\", " +
                            "\"data\": \"" + encodedSong + "\"" +
                            "}";
                    encodedSong = "";
                    httpPost = new HttpPost(serverAddr + Common.POST_ADD_AND_PLAY);
                    httpPost.addHeader("Accept", "application/json");
                    httpPost.addHeader("content-type", "application/json");
                    httpPost.setEntity(new StringEntity(jsonRequest, HTTP.UTF_8));
                    jsonRequest = "";
                    httpResponse = httpClient.execute(httpPost);

                    statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                    break;

                case ADDTOPLAYLIST:
                    song = new File(params[2]);
                    encodedSong = Base64.encodeToString(FileUtils.readFileToByteArray(song),
                            Base64.NO_WRAP | Base64.URL_SAFE);

                    jsonRequest = "{" +
                            "\"title\": \"" + params[3] + "\", " +
                            "\"data\": \"" + encodedSong + "\"" +
                            "}";
                    encodedSong = "";
                    httpPut = new HttpPut(serverAddr + Common.PUT_ADD_TO_PLAYLIST);
                    httpPut.addHeader("Accept", "application/json");
                    httpPut.addHeader("content-type", "application/json");
                    httpPut.setEntity(new StringEntity(jsonRequest, HTTP.UTF_8));
                    jsonRequest = "";
                    httpResponse = httpClient.execute(httpPut);

                    statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                    break;

                case PLAYSEPAUSE:
                    httpPatch = new HttpPatch(serverAddr + Common.PATCH_PLAY_PAUSE);
                    httpPatch.addHeader("Accept", "application/json");
                    httpPatch.addHeader("content-type", "application/json");
                    httpResponse = httpClient.execute(httpPatch);

                    statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                    break;

                case STOP:
                    HttpDelete httpDelete = new HttpDelete(serverAddr + Common.DELETE_STOP);
                    httpDelete.addHeader("Accept", "application/json");
                    httpDelete.addHeader("content-type", "application/json");
                    httpResponse = httpClient.execute(httpDelete);

                    statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                    break;

                case NEXT:
                    httpPatch = new HttpPatch(serverAddr + Common.PATCH_NEXT);
                    httpPatch.addHeader("Accept", "application/json");
                    httpPatch.addHeader("content-type", "application/json");
                    httpResponse = httpClient.execute(httpPatch);

                    statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                    break;
                case SET_SOUND_LEVEL:
                    Integer soundLevel = Integer.parseInt(params[2]);

                    jsonRequest = "{" +
                            "\"soundLevel\": \"" + soundLevel + "\" " +
                            "}";
                    httpPost = new HttpPost(serverAddr + Common.PATCH_SET_SOUND_LEVEL);
                    httpPost.addHeader("Accept", "application/json");
                    httpPost.addHeader("content-type", "application/json");
                    httpPost.setEntity(new StringEntity(jsonRequest, HTTP.UTF_8));
                    httpResponse = httpClient.execute(httpPost);

                    statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                    break;
                case GET_PLAYLIST:
                    responseString = this.getPlaylist();
                    break;
                case PLAY_SONG_IN_PLAYLIST:
                    String positionToPlay = params[2];

                    jsonRequest = "{\"positionInPlaylist\": \"" + positionToPlay + "\"}";
                    httpPatch = new HttpPatch(serverAddr + Common.PLAY_SONG_IN_PLAYLIST);
                    httpPatch.addHeader("Accept", "application/json");
                    httpPatch.addHeader("content-type", "application/json");
                    httpPatch.setEntity(new StringEntity(jsonRequest, HTTP.UTF_8));
                    httpResponse = httpClient.execute(httpPatch);

                    statusLine = httpResponse.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        httpResponse.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        httpResponse.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                    break;
                default:

                    break;
            }

        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            responseString = e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            responseString = e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            responseString = e.getMessage();
        } finally {
            return responseString;
        }

    }

    private String getPlaylist() throws IOException, JSONException {
        HttpResponse httpResponse;
        HttpGet httpGet;
        StatusLine statusLine;
        String responseString = "";

        httpGet = new HttpGet(serverAddr + Common.GET_PLAYLIST);
        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("content-type", "application/json");
        try {
            httpResponse = httpClient.execute(httpGet);

            statusLine = httpResponse.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
                System.out.println(responseString);
                JSONObject mainObject = new JSONObject(responseString);

                String[] playlist = mainObject.getString("message").split(",");
                RasMusicMainFragment.playlist.clear();
                for (int i = 0; i < playlist.length; i++) {
                    RasMusicMainFragment.playlist.add(playlist[i]);
                }
            } else {
                //Closes the connection.
                httpResponse.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (HttpHostConnectException e) {
            e.printStackTrace();
            responseString = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            responseString = e.getMessage();
        } finally {
            return responseString;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        CharSequence text = result;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(RasMusicMainFragment.me.getActivity(), text, duration);
        toast.show();
        RasMusicMainFragment.listViewAdapter.notifyDataSetChanged();
        System.out.println(result);
    }
}

