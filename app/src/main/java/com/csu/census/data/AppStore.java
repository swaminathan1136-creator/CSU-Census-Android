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

    private static final String PREFS = "csu_census_store";
    private static final String KEY_ACTIVE_ID = "active_id";
    private static final String KEY_HOUSEHOLDS = "households";
    private static final String KEY_RECORDS = "records";

    private AppStore() {
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    // --------------------------------------------------
    // INITIAL DATA
    // --------------------------------------------------

    public static void seed(Context context) {
        SharedPreferences store = preferences(context);

        if (!store.getBoolean("seeded", false)) {
            try {
                JSONObject demo = new JSONObject();
                demo.put("name", "Demo Collector");
                demo.put("email", "demo@csu.org");
                demo.put("password", "Demo@1234");

                store.edit()
                        .putString("account_CSU001", demo.toString())
                        .putBoolean("seeded", true)
                        .apply();
            } catch (Exception ignored) {
            }
        }

        seedDemoHouseholdsIfNeeded(context);
    }

    private static void seedDemoHouseholdsIfNeeded(Context context) {
        SharedPreferences store = preferences(context);

        if (store.getBoolean("demo_households_seeded", false)) {
            return;
        }

        JSONArray current = readArray(context, KEY_HOUSEHOLDS);

        if (current.length() == 0) {
            JSONArray sample = new JSONArray();

            sample.put(createSampleHousehold(
                    "HH001", "Anita Sharma", "Sector 12",
                    "01", 3, "Submitted"
            ));

            sample.put(createSampleHousehold(
                    "HH002", "Ramesh Kumar", "Sector 15",
                    "02", 4, "In Review"
            ));

            sample.put(createSampleHousehold(
                    "HH003", "Priya Verma", "Sector 18",
                    "03", 6, "Returned"
            ));

            sample.put(createSampleHousehold(
                    "HH004", "Suresh Singh", "Sector 21",
                    "04", 3, "Submitted"
            ));

            store.edit()
                    .putString(KEY_HOUSEHOLDS, sample.toString())
                    .apply();
        }

        store.edit()
                .putBoolean("demo_households_seeded", true)
                .apply();
    }

    private static JSONObject createSampleHousehold(
            String id,
            String name,
            String address,
            String ward,
            int members,
            String status
    ) {
        JSONObject household = new JSONObject();

        try {
            household.put("id", id);
            household.put("name", name);
            household.put("address", address);
            household.put("ward", ward);
            household.put("members", members);
            household.put("status", status);
            household.put("date", formatDate("dd MMM yyyy"));
        } catch (Exception ignored) {
        }

        return household;
    }

    // --------------------------------------------------
    // REGISTRATION
    // --------------------------------------------------

    public static boolean register(
            Context context,
            String name,
            String email,
            String collectorId,
            String password
    ) {
        if (context == null || name == null || email == null
                || collectorId == null || password == null) {
            return false;
        }

        SharedPreferences store = preferences(context);
        String accountKey = "account_" + collectorId.trim();

        if (store.contains(accountKey)) {
            return false;
        }

        try {
            JSONObject account = new JSONObject();
            account.put("name", name.trim());
            account.put("email", email.trim());
            account.put("password", password);

            store.edit()
                    .putString(accountKey, account.toString())
                    .apply();

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    // Registration overload used by SignupActivity.
    // The phone number is saved in the account profile.
    public static boolean register(
            Context context,
            String name,
            String email,
            String phone,
            String collectorId,
            String password
    ) {
        if (context == null || name == null || email == null
                || collectorId == null || password == null) {
            return false;
        }

        if (emailExists(context, email)) {
            return false;
        }

        SharedPreferences store = preferences(context);
        String accountKey = "account_" + collectorId.trim();

        if (store.contains(accountKey)) {
            return false;
        }

        try {
            JSONObject account = new JSONObject();
            account.put("name", name.trim());
            account.put("email", email.trim());
            account.put("phone", phone == null ? "" : phone.trim());
            account.put("password", password);

            store.edit()
                    .putString(accountKey, account.toString())
                    .apply();

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public static boolean emailExists(Context context, String email) {
        if (context == null || email == null
                || email.trim().isEmpty()) {
            return false;
        }

        SharedPreferences store = preferences(context);

        for (Map.Entry<String, ?> entry : store.getAll().entrySet()) {
            if (!entry.getKey().startsWith("account_")) {
                continue;
            }

            try {
                JSONObject account = new JSONObject(
                        String.valueOf(entry.getValue())
                );

                String savedEmail = account.optString("email", "");

                if (email.trim().equalsIgnoreCase(savedEmail.trim())) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        return false;
    }

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

    public static boolean signIn(
            Context context,
            String collectorId,
            String password
    ) {
        if (context == null || collectorId == null || password == null) {
            return false;
        }

        try {
            String accountKey = "account_" + collectorId.trim();

            String accountJson = preferences(context)
                    .getString(accountKey, "{}");

            JSONObject account = new JSONObject(accountJson);

            if (password.equals(account.optString("password"))) {
                preferences(context)
                        .edit()
                        .putString(KEY_ACTIVE_ID, collectorId.trim())
                        .apply();

                return true;
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    // --------------------------------------------------
    // PASSWORD RESET
    // --------------------------------------------------

    public static boolean resetPassword(
            Context context,
            String email,
            String newPassword
    ) {
        if (context == null || email == null || newPassword == null) {
            return false;
        }

        SharedPreferences store = preferences(context);

        for (String key : store.getAll().keySet()) {
            if (!key.startsWith("account_")) {
                continue;
            }

            try {
                JSONObject account = new JSONObject(
                        store.getString(key, "{}")
                );

                String savedEmail = account.optString("email", "");

                if (email.trim().equalsIgnoreCase(savedEmail.trim())) {
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

    // --------------------------------------------------
    // SESSION MANAGEMENT
    // --------------------------------------------------

    public static boolean isSignedIn(Context context) {
        String id = preferences(context)
                .getString(KEY_ACTIVE_ID, null);

        return id != null
                && !id.trim().isEmpty()
                && preferences(context).contains("account_" + id);
    }

    public static String activeId(Context context) {
        return preferences(context)
                .getString(KEY_ACTIVE_ID, null);
    }

    public static void signOut(Context context) {
        preferences(context)
                .edit()
                .remove(KEY_ACTIVE_ID)
                .apply();
    }

    // --------------------------------------------------
    // PROFILE
    // --------------------------------------------------

    public static String profile(Context context, String field) {
        try {
            String id = activeId(context);

            if (id == null || id.trim().isEmpty()) {
                return "Collector";
            }

            JSONObject account = new JSONObject(
                    preferences(context).getString(
                            "account_" + id,
                            "{}"
                    )
            );

            return account.optString(field, "Collector");
        } catch (Exception exception) {
            return "Collector";
        }
    }

    // --------------------------------------------------
    // JSON STORAGE
    // --------------------------------------------------

    private static JSONArray readArray(Context context, String key) {
        try {
            String json = preferences(context).getString(key, "[]");
            return new JSONArray(json);
        } catch (Exception exception) {
            return new JSONArray();
        }
    }

    private static void saveArray(
            Context context,
            String key,
            JSONArray array
    ) {
        preferences(context)
                .edit()
                .putString(key, array.toString())
                .apply();
    }

    // --------------------------------------------------
    // HOUSEHOLDS
    // --------------------------------------------------

    public static ArrayList<JSONObject> households(Context context) {
        JSONArray saved = readArray(context, KEY_HOUSEHOLDS);
        ArrayList<JSONObject> result = new ArrayList<>();

        for (int i = 0; i < saved.length(); i++) {
            JSONObject household = saved.optJSONObject(i);

            if (household != null) {
                result.add(household);
            }
        }

        return result;
    }

    public static JSONObject getHousehold(
            Context context,
            String householdId
    ) {
        if (context == null || householdId == null) {
            return null;
        }

        JSONArray saved = readArray(context, KEY_HOUSEHOLDS);

        for (int i = 0; i < saved.length(); i++) {
            JSONObject household = saved.optJSONObject(i);

            if (household != null
                    && householdId.equals(household.optString("id"))) {
                return household;
            }
        }

        return null;
    }

    public static void addHousehold(
            Context context,
            String name,
            String address,
            int members
    ) {
        addHousehold(context, name, address, "Not assigned", members);
    }

    // Used by AddHouseholdActivity.
    public static void addHousehold(
            Context context,
            String name,
            String address,
            int members,
            String phone,
            String notes
    ) {
        JSONArray saved = readArray(context, KEY_HOUSEHOLDS);

        try {
            JSONObject household = new JSONObject();

            household.put("id", generateHouseholdId(saved));
            household.put("name", name);
            household.put("address", address);
            household.put("ward", "Not assigned");
            household.put("members", members);
            household.put("phone", phone == null ? "" : phone);
            household.put("notes", notes == null ? "" : notes);
            household.put("status", "Draft");
            household.put("date", formatDate("dd MMM yyyy"));

            saved.put(household);
            saveArray(context, KEY_HOUSEHOLDS, saved);
        } catch (Exception ignored) {
        }
    }

    // Used by the Census Registration form in HouseholdsFragment.
    public static void addHousehold(
            Context context,
            String name,
            String address,
            String ward,
            JSONArray familyMembers
    ) {
        JSONArray saved = readArray(context, KEY_HOUSEHOLDS);

        try {
            JSONObject household = new JSONObject();

            int memberCount = familyMembers == null
                    ? 0 : familyMembers.length();

            household.put("id", generateHouseholdId(saved));
            household.put("name", name);
            household.put("address", address);
            household.put("ward", ward);
            household.put("members", memberCount);
            household.put(
                    "familyMembers",
                    familyMembers == null ? new JSONArray() : familyMembers
            );
            household.put("status", "Draft");
            household.put("date", formatDate("dd MMM yyyy"));

            saved.put(household);
            saveArray(context, KEY_HOUSEHOLDS, saved);
        } catch (Exception ignored) {
        }
    }

    public static void addHousehold(
            Context context,
            String name,
            String address,
            String ward,
            int members
    ) {
        JSONArray saved = readArray(context, KEY_HOUSEHOLDS);

        try {
            JSONObject household = new JSONObject();

            household.put("id", generateHouseholdId(saved));
            household.put("name", name);
            household.put("address", address);
            household.put("ward", ward);
            household.put("members", members);
            household.put("status", "Draft");
            household.put("date", formatDate("dd MMM yyyy"));

            saved.put(household);
            saveArray(context, KEY_HOUSEHOLDS, saved);
        } catch (Exception ignored) {
        }
    }

    private static String generateHouseholdId(JSONArray saved) {
        return "HH-" + String.format(
                Locale.US,
                "%04d",
                saved.length() + 1
        );
    }

    // --------------------------------------------------
    // SUBMIT HOUSEHOLD
    // --------------------------------------------------

    public static void submitHousehold(
            Context context,
            String householdId
    ) {
        JSONArray savedHouseholds = readArray(context, KEY_HOUSEHOLDS);
        JSONArray savedRecords = readArray(context, KEY_RECORDS);

        try {
            for (int i = 0; i < savedHouseholds.length(); i++) {
                JSONObject household =
                        savedHouseholds.optJSONObject(i);

                if (household == null
                        || !householdId.equals(household.optString("id"))) {
                    continue;
                }

                if ("Submitted".equalsIgnoreCase(
                        household.optString("status"))) {
                    break;
                }

                household.put("status", "Submitted");
                household.put(
                        "submittedAt",
                        formatDate("dd MMM yyyy, hh:mm a")
                );

                JSONObject record = new JSONObject();
                record.put("id", householdId);
                record.put("name", household.optString("name"));
                record.put(
                        "date",
                        formatDate("dd MMM yyyy, hh:mm a")
                );
                record.put("status", "Submitted");

                savedRecords.put(record);
                break;
            }

            saveArray(context, KEY_HOUSEHOLDS, savedHouseholds);
            saveArray(context, KEY_RECORDS, savedRecords);
        } catch (Exception ignored) {
        }
    }

    // --------------------------------------------------
    // RECORDS
    // --------------------------------------------------

    public static ArrayList<JSONObject> records(Context context) {
        JSONArray saved = readArray(context, KEY_RECORDS);
        ArrayList<JSONObject> result = new ArrayList<>();

        for (int i = 0; i < saved.length(); i++) {
            JSONObject record = saved.optJSONObject(i);

            if (record != null) {
                result.add(record);
            }
        }

        return result;
    }

    // --------------------------------------------------
    // STATUS COUNTS
    // --------------------------------------------------

    public static int countStatus(Context context, String status) {
        int count = 0;

        for (JSONObject household : households(context)) {
            if (status.equalsIgnoreCase(
                    household.optString("status", "Draft").trim())) {
                count++;
            }
        }

        return count;
    }

    // --------------------------------------------------
    // DATE FORMATTER
    // --------------------------------------------------

    private static String formatDate(String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                pattern,
                Locale.getDefault()
        );

        return formatter.format(new Date());
    }
}