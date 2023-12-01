package pt.ulisboa.tecnico.auxTests;

public class TestConfig {

    public static final String SOURCE_1_JSON = "{\n  \"account\": {\n    \"accountHolder\": [\"Alice\"],\n    \"balance\": 872.22,\n    \"currency\": \"EUR\",\n    \"movements\": [\n      {\n        \"date\": \"09/11/2023\",\n        \"value\": 1000.00,\n        \"description\": \"Salary\"\n      },\n      {\n        \"date\": \"15/11/2023\",\n        \"value\": -77.78,\n        \"description\": \"Electricity bill\"\n      },\n      {\n        \"date\": \"22/11/2023\",\n        \"value\": -50.00,\n        \"description\": \"ATM Withdrawal\"\n      }\n    ]\n  }\n}";
    public static final String SOURCE_TEST_PATH_1 = "source1.json";

    public static final String NON_EXISTENT_FILE = "nonExistentFile.json";

    public static final String DEST_TEST_PATH_1 = "dest1";

    public static final String SECRET_KEY_TEST_PATH_1 = "secret.key";
    public static final String SECRET_KEY_1 = "C3F0E109F251D6C7BA0E300820A660D2";
}
