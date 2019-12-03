package com.boldman.cooperuser.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * CardInfo Model
 * Created by BoldMan
 * 2019.05.26
 */


public class CardInfo implements Parcelable {

    private String lastFour;
    private int cardId;
    private String cardType;
    private int selected;

    public CardInfo() {
    }

    protected CardInfo(Parcel in) {
        lastFour = in.readString();
        cardId = in.readInt();
        cardType = in.readString();
        selected = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lastFour);
        dest.writeInt(cardId);
        dest.writeString(cardType);
        dest.writeInt(selected);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CardInfo> CREATOR = new Creator<CardInfo>() {
        @Override
        public CardInfo createFromParcel(Parcel in) {
            return new CardInfo(in);
        }

        @Override
        public CardInfo[] newArray(int size) {
            return new CardInfo[size];
        }
    };

    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CardInfo cardInfo = (CardInfo) o;

        return lastFour != null ? lastFour.equals(cardInfo.lastFour) : cardInfo.lastFour == null;

    }

    @Override
    public int hashCode() {
        return lastFour != null ? lastFour.hashCode() : 0;
    }
}
