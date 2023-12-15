package pt.ulisboa.tecnico.auxTests;

import java.util.Base64;

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
    public static final String SECRET_KEY_1 = "h9OsqMdAyzdEaPDh4nMGTQ==";

    public static final String PRIVATE_KEY_TEST_PATH_1 = "private.key";

    public static final String PRIVATE_KEY_1 = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCh+foKo1ZQ5OJJh2CGDOrSrwugUnwHsrg8/8yQtGrXmLNppHOAjuAqW4udUl7cfDus2rM3bTFq7tIOgm+fC1UdfimAjm+FESjd/96npn93FhiiIQLQoHPKVgtDeK1kzqtHZR9eZyqgonbttHEI75l/VgcgGCcOZzJ20SL9jWtwq9VIEstw6fFw1S5rn/yo6Y+lPs1Hakeu+StBaVOpdZu5N9Tg9mZDwEpFeVTACTSTc57UfO/vh4FACKBVkJdHMu360XItiLK5UoJCZt7ziFUwECtpUsGgiMCUnueUwmVBryfFRfU/HJCwOpCdp172ww6CojqXk4mSLV+GAjUNLBpjAgMBAAECggEAP4HS6xvainILVRf6nORwp3VRYiUlEyGcAfKrYqsSXwC0i32aebPLKWBmT6MNpXYiMaY38UnjP9FuU5kJqbqYPy2JmSl/iRVOyCf7xRDPVkI4G01FHMZkT9dHqEcm+OjdF+uKjol1mbDJX27JiN7keJwixA8kNQ36J8Lc7Lvnfa+JpCKiIF4bLQVrxsTCk21aw+6d+m/VjjHfO1LFzymeoZBxecKq/48cNI5cqTPjZCIzJWe323pFa6TIDXW07XyumlLv1zDuywQ5ZxiqRWw1eUa34ik8SM7KLCJt0yF99e/N7fZ0wyID1+F1DnQAJZ0QSjrf/T75n2fLQA40a7s6vQKBgQC9mc0BOu0aRc+iUD8qX6ONqX2qR0Xih+4bSQ53OvhaIR273crL357/+uXQi1pP5qFBU+kSZ6HOi5902HwW9xre5IooYMn1C5LJAUDVx+C89UqMLl+R2lyfWh3eO88/8Rg0A0dtSn+eN58WU5oI6l/8oRosChEKTUn2l2DeUjGr/QKBgQDas5XGHbxdlx9X1H9MnjUS8TmSxea8N6l1DRUihVHgCAD2CSvnhWjpDrBgaJ+5UlQS1bJIxNLdRZPHahz9nQ5Hjmo3YdicDf1elum3mA4z8Z93vSITxewttnSkpcPbM7WxnzCzoHB2nTzYqcEngYDD2WFWzpjo69EBydaCu1w93wKBgHuMCfMgQ7pX/UnGQrLI6Vl5NWLM2LQZYUW0YFG/Qm/wnmB50RjyWbhx9GjlNW9VWISqOfj9QInHn5tTKeEJGiBeCczv7QnZhcenImlrun1G1VHtMednw+0umsHidVU0NFJIRLXp0qj1k5vH1Fq05uFG1TgXQy1UWbruv3d/VdC9AoGBAIiQGRcT3FpoklbKxw61v5hrbrL6xqCC9Vq055yduUl2VftMMNEqGh9lX7djvVEAWqVEMUaFONKYrrWXyjCHULJX+HVKsPKJPcxB2gEdfsjquNQREuVb8qV8yAtDwy/ucydNqyCPL4qxlpwILCeaEyi4ZbLJ4OuYzPMb8MnDas7rAoGADaLE6Gs119bTVtioHaT1p3xB9gAqTB6w8Pj2FwyZ7r4uMEHPBEz0xOGHi9F2yHLpWqKWrfZ1dc0kb6OIndfLlXYzw1pdGLfWPzu6XmoETsGMkIcJHc8fWTEDTTCEcFPqWbhcUFRb03i6dsHndH2KRpaooqj+OANBodhri+sjq4s=";
    public static final String PUBLIC_KEY_TEST_PATH_1 = "public.key";

    public static final String PUBLIC_KEY_1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAofn6CqNWUOTiSYdghgzq0q8LoFJ8B7K4PP/MkLRq15izaaRzgI7gKluLnVJe3Hw7rNqzN20xau7SDoJvnwtVHX4pgI5vhREo3f/ep6Z/dxYYoiEC0KBzylYLQ3itZM6rR2UfXmcqoKJ27bRxCO+Zf1YHIBgnDmcydtEi/Y1rcKvVSBLLcOnxcNUua5/8qOmPpT7NR2pHrvkrQWlTqXWbuTfU4PZmQ8BKRXlUwAk0k3Oe1Hzv74eBQAigVZCXRzLt+tFyLYiyuVKCQmbe84hVMBAraVLBoIjAlJ7nlMJlQa8nxUX1PxyQsDqQnade9sMOgqI6l5OJki1fhgI1DSwaYwIDAQAB";

    public static final String SECRET_SESSION_KEY_TEST_PATH_1 = "secretSession.key";

    public static final String SECRET_SESSION_KEY_1 = "6UQ9k8mbz+flQtOeyD1/mw==";

    public static byte[] base64Decode(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }
}
