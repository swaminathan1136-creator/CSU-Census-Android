package com.csu.census.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public final class AppStore {

    private static final String PREFERENCES_NAME = "csu_census_store";
    private static final String KEY_ACTIVE_ID = "active_id";
    private static final String KEY_HOUSEHOLDS = "households";
    private static final String KEY_RECORDS = "records";

    private AppStore() {
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static void seed(Context context) {
        SharedPreferences store = preferences(context);

        try {
            if (!store.contains("account_CSU001")) {
                JSONObject demoAccount = new JSONObject();
                demoAccount.put("name", "Demo Collector");
                demoAccount.put("email", "demo@csu.org");
                demoAccount.put("password", "Demo@1234");
                store.edit()
                        .putString("account_CSU001", demoAccount.toString())
                        .apply();
            }

            String existing = store.getString(KEY_HOUSEHOLDS, "[]");
            if (store.getBoolean("seeded", false)
                    && existing != null
                    && existing.length() > 2) {
                return;
            }

            JSONArray households = new JSONArray();

            JSONObject h1 = new JSONObject();
            h1.put("id", "HH-0001");
            h1.put("name", "Sharma Family");
            h1.put("address", "12 MG Road, Sector 5");
            h1.put("members", 4);
            h1.put("status", "Submitted");
            h1.put("date", "10 Nov 2026");
            households.put(h1);

            JSONObject h2 = new JSONObject();
            h2.put("id", "HH-0002");
            h2.put("name", "Patel Residence");
            h2.put("address", "45 Lake View Apartments");
            h2.put("members", 3);
            h2.put("status", "Draft");
            h2.put("date", "11 Nov 2026");
            households.put(h2);

            JSONObject h3 = new JSONObject();
            h3.put("id", "HH-0003");
            h3.put("name", "Khan Household");
            h3.put("address", "8 Park Street, Block B");
            h3.put("members", 5);
            h3.put("status", "In Review");
            h3.put("date", "11 Nov 2026");
            households.put(h3);

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

        } catch (Exception ignored) {
        }
    }

    public static boolean register(
            Context context,
            String name,
            String email,
            String collectorId,
            String password
    ) {
        SharedPreferences store = preferences(context);
        String accountKey = "account_" + collectorId;

        if (store.contains(accountKey)) {
            return false;
        }

        try {
            JSONObject account = new JSONObject();
            account.put("name", name);
            account.put("email", email);
            account.put("password", password);

            store.edit()
                    .putString(accountKey, account.toString())
                    .apply();

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean signIn(
            Context context,
            String collectorId,
            String password
    ) {
        try {
            String accountKey = "account_" + collectorId;
            String accountJson = preferences(context).getString(accountKey, "{}");
            JSONObject account = new JSONObject(accountJson);

            if (password.equals(account.optString("password"))) {
                preferences(context)
                        .edit()
                        .putString(KEY_ACTIVE_ID, collectorId)
                        .apply();
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean resetPassword(
            Context context,
            String email,
            String newPassword
    ) {
        SharedPreferences store = preferences(context);
        Map<String, ?> allPreferences = store.getAll();

        for (String key : allPreferences.keySet()) {
            if (!key.startsWith("account_")) {
                continue;
            }

            try {
                JSONObject account = new JSONObject(store.getString(key, "{}"));
                String savedEmail = account.optString("email");

                if (email.equalsIgnoreCase(savedEmail)) {
                    account.put("password", newPassword);
                    store.edit()
                            .putString(key, account.toString())
                            .apply();
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static String activeId(Context context) {
        return preferences(context).getString(KEY_ACTIVE_ID, "CSU001");
    }

    public static String profile(Context context, String field) {
        try {
            String accountKey = "account_" + activeId(context);
            String accountJson = preferences(context).getString(accountKey, "{}");
            JSONObject account = new JSONObject(accountJson);
            return account.optString(field, "Collector");
        } catch (Exception exception) {
            return "Collector";
        }
    }

    public static void signOut(Context context) {
        preferences(context)
                .edit()
                .remove(KEY_ACTIVE_ID)
                .apply();
    }

    private static JSONArray readArray(Context context, String key) {
        try {
            String json = preferences(context).getString(key, "[]");
            return new JSONArray(json);
        } catch (Exception exception) {
            return new JSONArray();
        }
    }

    public static ArrayList<JSONObject> households(Context context) {
        JSONArray savedHouseholds = readArray(context, KEY_HOUSEHOLDS);
        ArrayList<JSONObject> result = new ArrayList<>();

        for (int index = 0; index < savedHouseholds.length(); index++) {
            JSONObject household = savedHouseholds.optJSONObject(index);
            if (household != null) {
                result.add(household);
            }
        }
        return result;
    }

    public static void addHousehold(
            Context context,
            String name,
            String address,
            int members
    ) {
        JSONArray savedHouseholds = readArray(context, KEY_HOUSEHOLDS);

        try {
            JSONObject household = new JSONObject();
            String householdId = "HH-" + String.format(
                    Locale.US,
                    "%04d",
                    savedHouseholds.length() + 1
            );

            household.put("id", householdId);
            household.put("name", name);
            household.put("address", address);
            household.put("members", members);
            household.put("status", "Draft");
            household.put("date", formatDate("dd MMM yyyy"));

            savedHouseholds.put(household);

            preferences(context)
                    .edit()
                    .putString(KEY_HOUSEHOLDS, savedHouseholds.toString())
                    .apply();
        } catch (Exception ignored) {
        }
    }

    public static void submitHousehold(Context context, String householdId) {
        JSONArray savedHouseholds = readArray(context, KEY_HOUSEHOLDS);
        JSONArray savedRecords = readArray(context, KEY_RECORDS);

        try {
            for (int index = 0; index < savedHouseholds.length(); index++) {
                JSONObject household = savedHouseholds.getJSONObject(index);
                String currentId = household.optString("id");

                if (!householdId.equals(currentId)) {
                    continue;
                }

                if ("Submitted".equals(household.optString("status"))) {
                    break;
                }

                household.put("status", "Submitted");

                JSONObject record = new JSONObject();
                record.put("id", householdId);
                record.put("name", household.optString("name"));
                record.put("date", formatDate("dd MMM yyyy, hh:mm a"));
                record.put("status", "Submitted");
                savedRecords.put(record);
                break;
            }

            preferences(context)
                    .edit()
                    .putString(KEY_HOUSEHOLDS, savedHouseholds.toString())
                    .putString(KEY_RECORDS, savedRecords.toString())
                    .apply();
        } catch (Exception ignored) {
        }
    }

    public static ArrayList<JSONObject> records(Context context) {
        JSONArray savedRecords = readArray(context, KEY_RECORDS);
        ArrayList<JSONObject> result = new ArrayList<>();

        for (int index = 0; index < savedRecords.length(); index++) {
            JSONObject record = savedRecords.optJSONObject(index);
            if (record != null) {
                result.add(record);
            }
        }
        return result;
    }

    public static int countStatus(Context context, String status) {
        int count = 0;
        for (JSONObject household : households(context)) {
            if (status.equals(household.optString("status"))) {
                count++;
            }
        }
        return count;
    }

    private static String formatDate(String pattern) {
        SimpleDateFormat formatter =
                new SimpleDateFormat(pattern, Locale.getDefault());
        return formatter.format(new Date());
    }
}