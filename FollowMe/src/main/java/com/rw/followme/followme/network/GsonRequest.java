package com.rw.followme.followme.network;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class GsonRequest<T> extends Request<T>{

	private final Gson gson;
	private final Type type;
    private final Map<String, String> headers;
    private final Listener<T> listener;
	private final ErrorListener errorListener;

	public GsonRequest(int method, String url, TypeToken token, Map<String, String> headers, Listener<T> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.type = token.getType();
        this.headers = headers;
		this.listener = listener;
        this.errorListener = errorListener;
		gson = new Gson();
	}

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            return (Response<T>)Response.success(
                    gson.fromJson(json, type), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		listener.onResponse(response);
	}
	
	@Override
    public void deliverError(VolleyError error) {
		errorListener.onErrorResponse(error);
    }
}
