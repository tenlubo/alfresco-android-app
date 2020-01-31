package org.alfresco.mobile.android.application.firebase;

import android.util.SparseArray;

public class FirebaseTools {

    public static final String toString(final SparseArray<?> pSparseArray) {
        final StringBuilder stringBuilder = new StringBuilder();

        final int size = pSparseArray.size();
        stringBuilder.append("{");
        for (int i = 0; i < size; i++) {
            stringBuilder.append(pSparseArray.keyAt(i)).append("=")
                    .append(pSparseArray.valueAt(i));
            if (i < (size - 1)) {
                stringBuilder.append(", ");
            }//from w ww  .j  av  a2 s  .co m
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }
}
