    package com.example.worknear.network

    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory

            object RetrofitInstance {
        private const val BASE_URL = "https://backend-server-g4uw.onrender.com" // Update with your backend URL

        // Retrofit instance for ApiService
        val apiService: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
