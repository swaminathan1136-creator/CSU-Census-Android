package com.csu.census.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public final class AppStore {

    private static final String PREFS = "csu_census_store";
    private static final String KEY_ACTIVE_ID = "active_id";
    private static final String KEY_HOUSEHOLDS = "households";
    private static final String KEY_RECORDS = "records";

    private AppStore() {}

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void seed(Context context) {
        SharedPreferences store = prefs(context);
        try {
            JSONObject demo = new JSONObject();
            demo.put("name", "Demo Collector");
            demo.put("email", "demo@csu.org");
            demo.put("phone", "+91 90000 00001");
            demo.put("password", "1");
            store.edit().putString("account_1", demo.toString()).apply();

            String existing = store.getString(KEY_HOUSEHOLDS, "[]");
            if (store.getBoolean("seeded", false) && existing != null && existing.length() > 2) {
                return;
            }

            JSONArray households = new JSONArray();
            households.put(hh("HH-0001", "Sharma Family", "12 MG Road, Sector 5, New Delhi", 4,
                    "Submitted", "10 Nov 2026", "+91 98765 43210", "Primary contact: Rajesh Sharma."));
            households.put(hh("HH-0002", "Patel Residence", "45 Lake View Apartments, Sector 12", 3,
                    "Draft", "11 Nov 2026", "+91 91234 56789", "Follow-up required."));
            households.put(hh("HH-0003", "Khan Household", "8 Park Street, Block B, South City", 5,
                    "In Review", "11 Nov 2026", "+91 99887 76655", "Verification in progress."));

            JSONArray records = new JSONArray();
            JSONObject r1 = new JSONObject();
            r1.put("id", "HH-0001");
            r1.put("name", "Sharma Family");
            r1.put("date", "10 Nov 2026, 04:30 PM");
            r1.put("status", "Submitted");
            records.put(r1);

            store.edit()
                    .putString(KEY_HOUSEHOLDS, households.toString())
                    .putString(KEY_RECORDS, records.toString())
                    .putBoolean("seeded", true)
                    .apply();
        } catch (Exception ignored) {}
    }

    private static JSONObject hh(String id, String name, String address, int members,
                                 String status, String date, String phone, String notes) throws Exception {
        JSONObject h = new JSONObject();
        h.put("id", id);
        h.put("name", name);
        h.put("address", address);
        h.put("members", members);
        h.put("status", status);
        h.put("date", date);
        h.put("phone", phone);
        h.put("notes", notes);
        return h;
    }

    public static boolean register(Context c, String name, String email, String phone,
                                   String collectorId, String password) {
        SharedPreferences store = prefs(c);
        String key = "account_" + collectorId;
        if (store.contains(key)) return false;
        try {
            JSONObject a = new JSONObject();
            a.put("name", name);
            a.put("email", email);
            a.put("phone", phone != null ? phone : "");
            a.put("password", password);
            store.edit().putString(key, a.toString()).apply();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean signIn(Context c, String collectorId, String password) {
        try {
            String json = prefs(c).getString("account_" + collectorId, "{}");
            JSONObject a = new JSONObject(json);
            if (password.equals(a.optString("password"))) {
                prefs(c).edit().putString(KEY_ACTIVE_ID, collectorId).apply();
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean resetPassword(Context c, String email, String newPassword) {
        SharedPreferences store = prefs(c);
        for (String key : store.getAll().keySet()) {
            if (!key.startsWith("account_")) continue;
            try {
                JSONObject a = new JSONObject(store.getString(key, "{}"));
                if (email.equalsIgnoreCase(a.optString("email"))) {
                    a.put("password", newPassword);
                    store.edit().putString(key, a.toString()).apply();
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    public static boolean emailExists(Context c, String email) {
        SharedPreferences store = prefs(c);
        for (String key : store.getAll().keySet()) {
            if (!key.startsWith("account_")) continue;
            try {
                JSONObject a = new JSONObject(store.getString(key, "{}"));
                if (email.equalsIgnoreCase(a.optString("email"))) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public static String activeId(Context c) {
        return prefs(c).getString(KEY_ACTIVE_ID, "1");
    }

    public static String profile(Context c, String field) {
        try {
            String json = prefs(c).getString("account_" + activeId(c), "{}");
            return new JSONObject(json).optString(field, "Collector");
        } catch (Exception e) {
            return "Collector";
        }
    }

    public static void signOut(Context c) {
        prefs(c).edit().remove(KEY_ACTIVE_ID).apply();
    }

    private static JSONArray readArray(Context c, String key) {
        try {
            return new JSONArray(prefs(c).getString(key, "[]"));
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    public static ArrayList<JSONObject> households(Context c) {
        JSONArray arr = readArray(c, KEY_HOUSEHOLDS);
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o != null) list.add(o);
        }
        return list;
    }

    public static void addHousehold(Context c, String name, String address, int members,
                                    String phone, String notes) {
        JSONArray arr = readArray(c, KEY_HOUSEHOLDS);
        try {
            JSONObject h = new JSONObject();
            h.put("id", "HH-" + String.format(Locale.US, "%04d", arr.length() + 1));
            h.put("name", name);
            h.put("address", address);
            h.put("members", members);
            h.put("status", "Draft");
            h.put("date", formatDate("dd MMM yyyy"));
            h.put("phone", phone != null ? phone : "");
            h.put("notes", notes != null ? notes : "");
            arr.put(h);
            prefs(c).edit().putString(KEY_HOUSEHOLDS, arr.toString()).apply();
        } catch (Exception ignored) {}
    }

    public static void addHousehold(Context c, String name, String address, int members) {
        addHousehold(c, name, address, members, "", "");
    }

    public static JSONObject getHousehold(Context c, String householdId) {
        for (JSONObject h : households(c)) {
            if (householdId.equals(h.optString("id"))) return h;
        }
        return null;
    }

    public static void submitHousehold(Context c, String householdId) {
        JSONArray households = readArray(c, KEY_HOUSEHOLDS);
        JSONArray records = readArray(c, KEY_RECORDS);
        try {
            for (int i = 0; i < households.length(); i++) {
                JSONObject h = households.getJSONObject(i);
                if (!householdId.equals(h.optString("id"))) continue;
                if ("Submitted".equals(h.optString("status"))) break;
                h.put("status", "Submitted");
                JSONObject r = new JSONObject();
                r.put("id", householdId);
                r.put("name", h.optString("name"));
                r.put("date", formatDate("dd MMM yyyy, hh:mm a"));
                r.put("status", "Submitted");
                records.put(r);
                break;
            }
            prefs(c).edit()
                    .putString(KEY_HOUSEHOLDS, households.toString())
                    .putString(KEY_RECORDS, records.toString())
                    .apply();
        } catch (Exception ignored) {}
    }

    public static ArrayList<JSONObject> records(Context c) {
        JSONArray arr = readArray(c, KEY_RECORDS);
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o != null) list.add(o);
        }
        return list;
    }

    public static int countStatus(Context c, String status) {
        int n = 0;
        for (JSONObject h : households(c)) {
            if (status.equals(h.optString("status"))) n++;
        }
        return n;
    }

    private static String formatDate(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }
}