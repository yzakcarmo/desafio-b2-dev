package com.yzakcarmo.desafiob2dev.tenant;

public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenant(String tenantCode) {
        CURRENT_TENANT.set(tenantCode);
    }

    public static String getTenant() {
        return CURRENT_TENANT.get();
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }

    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}