package github.alessandrofazio.messaging.debezium;

public enum DebeziumOp {
    CREATE("c"),
    UPDATE("u"),
    DELETE("d");
    private final String value;

    public String getValue() {
        return value;
    }

    DebeziumOp(String value) {
        this.value = value;
    }
}

