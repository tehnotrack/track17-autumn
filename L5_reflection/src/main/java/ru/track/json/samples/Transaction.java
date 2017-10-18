package ru.track.json.samples;

import com.google.gson.annotations.SerializedName;
import ru.track.json.SerializedTo;

/**
 *
 */
public class Transaction {
    @SerializedName("txid")
    @SerializedTo("txid")
    long transactionId;

    @SerializedName("sid")
    @SerializedTo("sid")
    long senderId;

    @SerializedName("rid")
    @SerializedTo("rid")
    long receiverId;

    @SerializedName("am")
    @SerializedTo("am")
    long amount;

    @SerializedName("op")
    @SerializedTo("op")
    String operator;

    public Transaction(long transactionId, long senderId, long receiverId, long amount, String operator) {
        this.transactionId = transactionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
        this.operator = operator;
    }
}
