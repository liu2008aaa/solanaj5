/************************************************************************ 
 * Copyright PointCheckout, Ltd.
 * 
 */
package com.paymennt.solanaj.api.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.paymennt.solanaj.api.rpc.types.RpcRequest;
import com.paymennt.solanaj.api.rpc.types.RpcResponse;
import com.paymennt.solanaj.exception.SolanajException;
import com.paymennt.solanaj.utils.JsonUtils;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// TODO: Auto-generated Javadoc
/**
 * The Class SolanaRpcClient.
 */
public class SolanaRpcClient {
    
    /** The Constant JSON. */
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /** The endpoint. */
    private String endpoint;
    
    /** The http client. */
    private OkHttpClient httpClient = new OkHttpClient.Builder()//
            .connectTimeout(60, TimeUnit.SECONDS)//
            .writeTimeout(60, TimeUnit.SECONDS)//
            .readTimeout(60, TimeUnit.SECONDS)//
            .build();

    /** The rpc api. */
    private SolanaRpcApi rpcApi;

    /**
     * Instantiates a new solana rpc client.
     *
     * @param endpoint the endpoint
     */
    public SolanaRpcClient(Cluster endpoint) {
        this(endpoint.getEndpoint());
    }

    /**
     * Instantiates a new solana rpc client.
     *
     * @param endpoint the endpoint
     */
    public SolanaRpcClient(String endpoint) {
        this.endpoint = endpoint;
        rpcApi = new SolanaRpcApi(this);
    }

    /**
     * Call.
     *
     * @param <T> the generic type
     * @param method the method
     * @param params the params
     * @param clazz the clazz
     * @return the t
     */
    @SuppressWarnings("deprecation")
    public <T> T call(String method, List<Object> params, Class<T> clazz) {
        RpcRequest rpcRequest = new RpcRequest(method, params);
        Request request = new Request.Builder()//
                .url(endpoint)//
                .post(RequestBody.create(JSON, JsonUtils.encode(rpcRequest)))//
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            String responseJson = response.body().string();

            RpcResponse<T> rpcResult = JsonUtils.getObjectMapper()//
                    .readValue(responseJson, new TypeReference<RpcResponse<T>>() {});

            if (rpcResult.getError() != null) {
                throw new SolanajException(rpcResult.getError().getMessage());
            }

            String resultJson = JsonUtils.getObjectMapper().writeValueAsString(rpcResult.getResult());

            return JsonUtils.decode(resultJson, clazz);
        } catch (IOException e) {
            throw new SolanajException(e.getMessage());
        }
    }

    /**
     * Call batch.
     *
     * @param <T> the generic type
     * @param method the method
     * @param paramList the param list
     * @param clazz the clazz
     * @return the list
     */
    @SuppressWarnings("deprecation")
    public <T> List<T> callBatch(String method, List<List<Object>> paramList, Class<T> clazz) {

        List<RpcRequest> rpcRequests =
                paramList.stream().map(params -> new RpcRequest(method, params)).collect(Collectors.toList());

        Request request = new Request.Builder()//
                .url(endpoint)//
                .post(RequestBody.create(JSON, JsonUtils.encode(rpcRequests)))//
                .build();

        try {
            Response response = httpClient.newCall(request).execute();
            String responseJson = response.body().string();

            if (responseJson.isBlank())
                return new ArrayList<>();

            List<RpcResponse<T>> rpcResults = JsonUtils.getObjectMapper()//
                    .readValue(responseJson, new TypeReference<List<RpcResponse<T>>>() {});

            RpcResponse<T> errorResult = rpcResults.stream().filter(r -> r.getError() != null).findAny().orElse(null);

            if (errorResult != null) {
                throw new SolanajException(errorResult.getError().getMessage());
            }

            List<T> result = new ArrayList<>();

            for (RpcResponse<T> rpcResult : rpcResults) {
                String resultJson = JsonUtils.getObjectMapper().writeValueAsString(rpcResult.getResult());
                result.add(JsonUtils.decode(resultJson, clazz));
            }

            return result;
        } catch (IOException e) {
            throw new SolanajException(e.getMessage());
        }
    }

    /**
     * Gets the api.
     *
     * @return the api
     */
    public SolanaRpcApi getApi() {
        return rpcApi;
    }

    /**
     * Gets the endpoint.
     *
     * @return the endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

}
