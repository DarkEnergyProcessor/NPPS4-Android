package com.npdep.npps4;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class ConsoleText implements Parcelable {
    protected ConsoleText(Parcel in) {
        timestamp = in.readLong();
        stderr = in.readInt() == 1;
        text = Objects.requireNonNull(in.readString());
    }

    public ConsoleText() {
        timestamp = System.currentTimeMillis();
    }

    public ConsoleText(@NonNull String text) {
        timestamp = System.currentTimeMillis();
        this.text = Objects.requireNonNull(text);
    }

    public ConsoleText(@NonNull String text, boolean stderr) {
        timestamp = System.currentTimeMillis();
        this.text = Objects.requireNonNull(text);
        this.stderr = stderr;
    }

    public static final Creator<ConsoleText> CREATOR = new Creator<>() {
        @Override
        public ConsoleText createFromParcel(Parcel in) {
            return new ConsoleText(in);
        }

        @Override
        public ConsoleText[] newArray(int size) {
            return new ConsoleText[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeLong(timestamp);
        parcel.writeInt(stderr ? 1 : 0);
        parcel.writeString(text);
    }

    public long timestamp;
    public boolean stderr = false;
    @NonNull
    public String text = "";
}
