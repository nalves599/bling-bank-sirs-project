package pt.ulisboa.tecnico.auxTests;

public class TestConfig {

    public static final String SOURCE_1_JSON = """
        {
          "account" : {
            "accountHolder" : [ "Alice" ],
            "balance" : 872.22,
            "currency" : "EUR",
            "movements" : [ {
              "date" : "09/11/2023",
              "description" : "Salary",
              "value" : 1000
            }, {
              "date" : "15/11/2023",
              "description" : "Electricity bill",
              "value" : -77.78
            }, {
              "date" : "22/11/2023",
              "description" : "ATM Withdrawal",
              "value" : -50
            } ]
          }
        }""";

    public static final String SOURCE_TEST_PATH_1 = "source1.json";

    public static final String NON_EXISTENT_FILE = "nonExistentFile.json";

    public static final String DEST_TEST_PATH_1 = "dest1";

    public static final String SECRET_KEY_TEST_PATH_1 = "secret.key";
    public static final String SECRET_KEY_1 = "C3F0E109F251D6C7BA0E300820A660D2";
}
