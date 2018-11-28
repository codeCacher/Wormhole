package com.codecacher.wormhole;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author cuishun
 * @since 2018/11/28.
 */
public class BinderWrapper implements Parcelable {
    private IBinder binder;

    public BinderWrapper(IBinder binder) {
        this.binder = binder;
    }

    private BinderWrapper(Parcel in) {
        binder = in.readStrongBinder();
    }

    public IBinder getBinder() {
        return binder;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(binder);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BinderWrapper> CREATOR = new Creator<BinderWrapper>() {
        @Override
        public BinderWrapper createFromParcel(Parcel in) {
            return new BinderWrapper(in);
        }

        @Override
        public BinderWrapper[] newArray(int size) {
            return new BinderWrapper[size];
        }
    };
}
