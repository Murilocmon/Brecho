package com.example.brecho;

public final class SupabaseConfig {
    // Preencha com os valores do seu projeto Supabase
    // Ex.: https://xyzcompany.supabase.co
    public static final String SUPABASE_URL = "https://hamqyanzgfzcxnxnqzev.supabase.co";
    // Chave ANON pública (NUNCA use service_role no app)
    public static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhhbXF5YW56Z2Z6Y3hueG5xemV2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc4Mjg2NDAsImV4cCI6MjA2MzQwNDY0MH0.6l3dW3OXC8M_CX2TrejJR8EY5xgZvsIcKzTIXQ14rTs";

    // Endpoints convenientes
    public static final String POSTGREST_BASE = SUPABASE_URL + "/rest/v1";
    public static final String AUTH_BASE = SUPABASE_URL + "/auth/v1";

    private SupabaseConfig() {}
}

