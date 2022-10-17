package dev.mfaheemezani.mvvm.helpers.network

import android.os.Build
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http.RealResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.GzipSource
import okio.IOException
import okio.buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.security.KeyStore
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object NewYorkTimesBackendAccess {

    private val BASE_DOMAIN_NAME = "api.nytimes.com"
    private val BASE_URL = "https://${BASE_DOMAIN_NAME}"

    private val USER_AGENT = "Android"

    private val logging = HttpLoggingInterceptor(HttpLoggingPrettifier()).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    class HttpLoggingPrettifier : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            val logName = this::class.java.simpleName
            if (message.startsWith("{") || message.startsWith("[")) {
                try {
                    val prettyPrintJson = GsonBuilder().setPrettyPrinting()
                        .create().toJson(JsonParser.parseString(message).asJsonObject)
                    Log.d(logName, prettyPrintJson)
                } catch (m: JsonSyntaxException) {
                    Log.d(logName, message)
                }
            } else {
                Log.d(logName, message)
                return
            }
        }
    }

    private val clientBuilder = createOkHttpBuilderWithTlsConfig()
        .dns(DnsPreference())
        .protocols(listOf(Protocol.HTTP_1_1))
        .addInterceptor(HeaderInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(UnzippingInterceptor())
        .addInterceptor(logging)

    private val client = clientBuilder.build()

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(NewYorkTimesAPI::class.java) }

    class DnsPreference : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            // Return only results for IPv4 addresses
            return Dns.SYSTEM.lookup(hostname).filter { Inet4Address::class.java.isInstance(it) }
        }
    }

    class UnzippingInterceptor : Interceptor {
        @Throws(Exception::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            return unzip(response)
        }
    }

    @Throws(IOException::class)
    private fun unzip(response: Response): Response {
        if (response.body == null) return response

        // Check if we have gzip response
        val contentEncoding = response.headers["content-encoding"]

        // This is used to decompress gzipped responses
        if (contentEncoding != null && contentEncoding.equals("gzip", true)) {
            val contentLength = response.body!!.contentLength()
            val responseBody = GzipSource(response.body!!.source().buffer)
            val strippedHeaders = response.headers.newBuilder().build()
            return response.newBuilder().headers(strippedHeaders)
                .body(
                    RealResponseBody(response.body!!.contentType().toString(), contentLength,
                    responseBody.buffer()
                )
                )
                .build()
        } else {
            return response
        }
    }

    class HeaderInterceptor : Interceptor {

        @Throws(Exception::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            try {
                val builder = chain.request().newBuilder()
                builder.addHeader("User-Agent", "$USER_AGENT/" +
                        Build.VERSION.SDK_INT + "/" + Build.VERSION.CODENAME)
                builder.addHeader("Accept", "*/*")
                builder.addHeader("Cache-Control", "no-cache")
                builder.addHeader("Host", BASE_DOMAIN_NAME)
                builder.addHeader("Accept-Encoding", "gzip, deflate, br")
                builder.addHeader("Connection", "keep-alive")
                return chain.proceed(builder.build())
            } catch (e: Exception) {
                e.printStackTrace()
                return Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(999)
                    .message(e.toString())
                    .body("{${e}}".toResponseBody(null)).build()
            }
        }

    }

    private fun createOkHttpBuilderWithTlsConfig(): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            val trustManager by lazy {
                val trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(null as KeyStore?)
                trustManagerFactory.trustManagers.first { it is X509TrustManager } as X509TrustManager
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
                try {
                    val sc = SSLContext.getInstance(TlsVersion.TLS_1_2.javaName)
                    sc.init(null, null, null)
                    sslSocketFactory(TlsSocketFactory(sc.socketFactory), trustManager)

                    val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(*TlsSocketFactory.ALLOWED_TLS_VERSIONS)
                        .build()

                    val specs = ArrayList<ConnectionSpec>()
                    specs.add(cs)
                    specs.add(ConnectionSpec.COMPATIBLE_TLS)
                    connectionSpecs(specs)
                } catch (e: Exception) {
                    Log.e(this::class.java.simpleName, e.toString())
                }
            }
        }
    }


}