
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

    private static final String PREFERENCES_NAME =
            "csu_census_store";

    private static final String KEY_ACTIVE_ID =
            "active_id";

    private static final String KEY_HOUSEHOLDS =
            "households";

    private static final String KEY_RECORDS =
            "records";

    private AppStore() {
    }

    private static SharedPreferences preferences(Context context) {

        return context.getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
    }

    // --------------------------------------------------
    // INITIAL SEEDING
    // --------------------------------------------------

    public static void seed(Context context) {

        SharedPreferences store = preferences(context);

        if (!store.getBoolean("seeded", false)) {

            try {

                JSONObject demoAccount = new JSONObject();

                demoAccount.put("name", "Demo Collector");
                demoAccount.put("email", "demo@csu.org");
                demoAccount.put("password", "Demo@1234");

                store.edit()
                        .putString(
                                "account_CSU001",
                                demoAccount.toString()
                        )
                        .putBoolean("seeded", true)
                        .apply();

            } catch (Exception ignored) {
                // The app can still open if seeding fails.
            }
        }

        seedDemoHouseholdsIfNeeded(context);
    }

    private static void seedDemoHouseholdsIfNeeded(Context context) {

        SharedPreferences store = preferences(context);

        if (store.getBoolean("demo_households_seeded", false)) {
            return;
        }

        try {

            JSONArray current = readArray(
                    context,
                    KEY_HOUSEHOLDS
            );

            if (current.length() == 0) {

                JSONArray sample = new JSONArray();

                sample.put(createSampleHousehold(
                        "HH001",
                        "Anita Sharma",
                        "Sector 12",
                        "01",
                        3,
                        "Submitted"
                ));

                sample.put(createSampleHousehold(
                        "HH002",
                        "Ramesh Kumar",
                        "Sector 15",
                        "02",
                        4,
                        "In Review"
                ));

                sample.put(createSampleHousehold(
                        "HH003",
                        "Priya Verma",
                        "Sector 18",
                        "03",
                        6,
                        "Returned"
                ));

                sample.put(createSampleHousehold(
                        "HH004",
                        "Suresh Singh",
                        "Sector 21",
                        "04",
                        3,
                        "Submitted"
                ));

                store.edit()
                        .putString(
                                KEY_HOUSEHOLDS,
                                sample.toString()
                        )
                        .apply();
            }

        } catch (Exception ignored) {
            // Existing local data is never replaced by sample data.
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
    ) throws Exception {

        JSONObject household = new JSONObject();

        household.put("id", id);
        household.put("name", name);
        household.put("address", address);
        household.put("ward", ward);
        household.put("members", members);
        household.put("status", status);
        household.put("date", formatDate("dd MMM yyyy"));

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
                    .putString(
                            accountKey,
                            account.toString()
                    )
                    .apply();

            return true;

        } catch (Exception exception) {

            return false;
        }
    }

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------

    public static boolean signIn(
            Context context,
            String collectorId,
            String password
    ) {

        try {

            String accountKey = "account_" + collectorId;

            String accountJson = preferences(context)
                    .getString(accountKey, "{}");

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

    // --------------------------------------------------
    // PASSWORD RESET
    // --------------------------------------------------

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

                JSONObject account = new JSONObject(
                        store.getString(key, "{}")
                );

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

    // --------------------------------------------------
    // PROFILE
    // --------------------------------------------------

    public static String profile(
            Context context,
            String field
    ) {

        try {

            String id = activeId(context);

            if (id == null || id.trim().isEmpty()) {
                return "Collector";
            }

            String accountKey = "account_" + id;

            String accountJson = preferences(context)
                    .getString(accountKey, "{}");

            JSONObject account = new JSONObject(accountJson);

            return account.optString(field, "Collector");

        } catch (Exception exception) {

            return "Collector";
        }
    }

    // --------------------------------------------------
    // LOGOUT
    // --------------------------------------------------

    public static void signOut(Context context) {

        preferences(context)
                .edit()
                .remove(KEY_ACTIVE_ID)
                .apply();
    }

    // --------------------------------------------------
    // JSON STORAGE HELPER
    // --------------------------------------------------

    private static JSONArray readArray(
            Context context,
            String key
    ) {

        try {

            String json = preferences(context)
                    .getString(key, "[]");

            return new JSONArray(json);

        } catch (Exception exception) {

            return new JSONArray();
        }
    }

    // --------------------------------------------------
    // GET ALL HOUSEHOLDS
    // --------------------------------------------------

    public static ArrayList<JSONObject> households(
            Context context
    ) {

        JSONArray savedHouseholds = readArray(
                context,
                KEY_HOUSEHOLDS
        );

        ArrayList<JSONObject> result = new ArrayList<>();

        for (int index = 0;
             index < savedHouseholds.length();
             index++) {

            JSONObject household =
                    savedHouseholds.optJSONObject(index);

            if (household != null) {
                result.add(household);
            }
        }

        return result;
    }

    // --------------------------------------------------
    // GET ONE HOUSEHOLD BY ID
    // --------------------------------------------------

    public static JSONObject getHousehold(
            Context context,
            String householdId
    ) {

        if (context == null
                || householdId == null
                || householdId.trim().isEmpty()) {

            return null;
        }

        JSONArray savedHouseholds = readArray(
                context,
                KEY_HOUSEHOLDS
        );

        for (int index = 0;
             index < savedHouseholds.length();
             index++) {

            JSONObject household =
                    savedHouseholds.optJSONObject(index);

            if (household == null) {
                continue;
            }

            String savedId = household.optString("id", "");

            if (householdId.equals(savedId)) {
                return household;
            }
        }

        return null;
    }

    // --------------------------------------------------
    // ADD HOUSEHOLD
    // --------------------------------------------------

    public static void addHousehold(
            Context context,
            String name,
            String address,
            int members
    ) {

        addHousehold(
                context,
                name,
                address,
                "Not assigned",
                members
        );
    }

    public static void addHousehold(
            Context context,
            String name,
            String address,
            String ward,
            int members
    ) {

        JSONArray savedHouseholds = readArray(
                context,
                KEY_HOUSEHOLDS
        );

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
            household.put("ward", ward);
            household.put("members", members);
            household.put("status", "Draft");
            household.put("date", formatDate("dd MMM yyyy"));

            savedHouseholds.put(household);

            preferences(context)
                    .edit()
                    .putString(
                            KEY_HOUSEHOLDS,
                            savedHouseholds.toString()
                    )
                    .apply();

        } catch (Exception ignored) {
            // Do not crash the UI if local JSON cannot be updated.
        }
    }

    // --------------------------------------------------
    // SUBMIT HOUSEHOLD
    // --------------------------------------------------

    public static void submitHousehold(
            Context context,
            String householdId
    ) {

        JSONArray savedHouseholds = readArray(
                context,
                KEY_HOUSEHOLDS
        );

        JSONArray savedRecords = readArray(
                context,
                KEY_RECORDS
        );

        try {

            for (int index = 0;
                 index < savedHouseholds.length();
                 index++) {

                JSONObject household =
                        savedHouseholds.getJSONObject(index);

                String currentId = household.optString("id");

                if (!householdId.equals(currentId)) {
                    continue;
                }

                if ("Submitted".equals(
                        household.optString("status")
                )) {
                    break;
                }

                household.put("status", "Submitted");

                household.put(
                        "submittedAt",
                        formatDate("dd MMM yyyy, hh:mm a")
                );

                JSONObject record = new JSONObject();

                record.put("id", householdId);

                record.put(
                        "name",
                        household.optString("name")
                );

                record.put(
                        "date",
                        formatDate("dd MMM yyyy, hh:mm a")
                );

                record.put("status", "Submitted");

                savedRecords.put(record);

                break;
            }

            preferences(context)
                    .edit()
                    .putString(
                            KEY_HOUSEHOLDS,
                            savedHouseholds.toString()
                    )
                    .putString(
                            KEY_RECORDS,
                            savedRecords.toString()
                    )
                    .apply();

        } catch (Exception ignored) {
        }
    }

    // --------------------------------------------------
    // GET ALL RECORDS
    // --------------------------------------------------

    public static ArrayList<JSONObject> records(
            Context context
    ) {

        JSONArray savedRecords = readArray(
                context,
                KEY_RECORDS
        );

        ArrayList<JSONObject> result = new ArrayList<>();

        for (int index = 0;
             index < savedRecords.length();
             index++) {

            JSONObject record =
                    savedRecords.optJSONObject(index);

            if (record != null) {
                result.add(record);
            }
        }

        return result;
    }

    // --------------------------------------------------
    // COUNT HOUSEHOLDS BY STATUS
    // --------------------------------------------------

    public static int countStatus(
            Context context,
            String status
    ) {

        int count = 0;

        for (JSONObject household : households(context)) {

            if (status.equalsIgnoreCase(
                    household.optString("status", "Draft").trim()
            )) {

                count++;
            }
        }

        return count;
    }

    // --------------------------------------------------
    // DATE FORMATTER
    // --------------------------------------------------

    private static String formatDate(String pattern) {

        SimpleDateFormat formatter =
                new SimpleDateFormat(
                        pattern,
                        Locale.getDefault()
                );

        return formatter.format(new Date());
    }
}
