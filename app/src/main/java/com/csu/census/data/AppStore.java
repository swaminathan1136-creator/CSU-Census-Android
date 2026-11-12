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