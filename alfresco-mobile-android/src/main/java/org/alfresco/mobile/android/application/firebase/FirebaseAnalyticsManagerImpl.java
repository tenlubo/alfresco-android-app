package org.alfresco.mobile.android.application.firebase;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.alfresco.mobile.android.platform.accounts.AlfrescoAccount;
import org.alfresco.mobile.android.platform.accounts.AlfrescoAccountManager;
import org.alfresco.mobile.android.platform.extensions.AnalyticsManager;

import java.util.List;

public class FirebaseAnalyticsManagerImpl extends AnalyticsManager {

    private FirebaseAnalytics mFirebaseAnalytics;

    protected SharedPreferences.Editor editor;

    protected Integer status = null;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public static FirebaseAnalyticsManagerImpl getInstance(Context context)
    {
        synchronized (LOCK)
        {
            if (mInstance == null)
            {
                mInstance = new FirebaseAnalyticsManagerImpl(context);
            }

            return (FirebaseAnalyticsManagerImpl) mInstance;
        }
    }

    protected FirebaseAnalyticsManagerImpl(Context context)
    {
        super(context);
    }



    // ///////////////////////////////////////////////////////////////////////////
    // REPORT
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void initMainActivity(final Activity activity)
    {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, activity.getLocalClassName());
        bundle.putString(FirebaseAnalytics.Param.METHOD, "FireBase Analytics Recreated");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void startReport(final Activity activity)
    {
        mFirebaseAnalytics.setCurrentScreen(activity, getClass().getSimpleName(),
                "FirebaseAnalyticsManagerImpl Screen");
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, activity.getLocalClassName());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void reportScreen(String name)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ORIGIN, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void reportEvent(String category, String action, String label, int value)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        bundle.putString(FirebaseAnalytics.Param.METHOD, action);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, label);
        bundle.putInt(FirebaseAnalytics.Param.VALUE, value);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public void reportEvent(final String category, final String action, final String label,
                            final int value, final int customMetricId,
                            final Long customMetricValue)
    {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        bundle.putString(FirebaseAnalytics.Param.METHOD, action);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, label);
        bundle.putInt(FirebaseAnalytics.Param.VALUE, value);
        StringBuilder metrics = new StringBuilder();
        metrics.append("id:");
        metrics.append(customMetricId);
        metrics.append("=");
        metrics.append(customMetricValue);
        bundle.putString(FirebaseAnalytics.Param.CONTENT, metrics.toString());
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public void reportInfo(String label, SparseArray<String> dimensions, SparseArray<Long> metrics)
    {
        reportEvent(CATEGORY_SESSION, ACTION_INFO, label, 1, dimensions, metrics);
    }

    @Override
    public void reportEvent(final String category, final String action, final String label,
                            final int eventValue, final SparseArray<String> dimensions,
                            final SparseArray<Long> metrics)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        bundle.putString(FirebaseAnalytics.Param.METHOD, action);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, label);
        bundle.putInt(FirebaseAnalytics.Param.VALUE, eventValue);
        bundle.putString(FirebaseAnalytics.Param.CONTENT, FirebaseTools.toString(metrics));
        bundle.putString(FirebaseAnalytics.Param.ITEM_LIST, FirebaseTools.toString(dimensions));
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void reportError(boolean isFatal, String description)
    {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.SCORE, "FATAL");
        bundle.putString(FirebaseAnalytics.Param.CONTENT, description);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // O
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void optOutByConfig(Context context, AlfrescoAccount account)
    {
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(ANALYTICS_PREFIX + account.getId(), STATUS_BLOCKED).apply();
        status = getStatus();
    }

    @Override
    public void optInByConfig(Context context, AlfrescoAccount account)
    {
        editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(ANALYTICS_PREFIX + account.getId(), STATUS_ENABLE).apply();
        status = getStatus();
    }

    @Override
    public void optIn(Activity activity)
    {
        setStatus(STATUS_ENABLE);
        status = getStatus();
    }

    @Override
    public void optOut(Activity activity)
    {
        setStatus(STATUS_DISABLE);
        status = getStatus();
    }

    @Override
    public void cleanOptInfo(Context context, AlfrescoAccount account)
    {
        if (editor == null)
        {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        }
        if (account != null)
        {
            editor.remove(ANALYTICS_PREFIX + account.getId());
            editor.apply();
        }
        status = getStatus();
    }

    private void opt(Context context, int value, AlfrescoAccount account)
    {
        if (editor == null)
        {
            editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        }
        if (account != null)
        {
            editor.putInt(ANALYTICS_PREFIX + account.getId(), value);
            editor.apply();
        }
    }

    @Override
    public boolean isEnable()
    {
        if (status == null)
        {
            getStatus();
        }
        return status == STATUS_ENABLE;
    }

    @Override
    public boolean isEnable(AlfrescoAccount account)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getInt(ANALYTICS_PREFIX + account.getId(),
                STATUS_ENABLE) == STATUS_ENABLE;
    }

    @Override
    public boolean isBlocked(AlfrescoAccount account)
    {
        return PreferenceManager.getDefaultSharedPreferences(appContext).getInt(ANALYTICS_PREFIX + account.getId(),
                STATUS_ENABLE) == STATUS_BLOCKED;
    }

    @Override
    public boolean isBlocked()
    {
        if (status == null)
        {
            getStatus();
        }
        return status == STATUS_BLOCKED;
    }

    private int getStatus()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(appContext);
        List<AlfrescoAccount> accounts = AlfrescoAccountManager.retrieveAccounts(appContext);
        int tempStatus = STATUS_ENABLE;
        Integer resultStatus = null;
        for (AlfrescoAccount account : accounts)
        {
            tempStatus = sharedPref.getInt(ANALYTICS_PREFIX + account.getId(), STATUS_ENABLE);
            switch (tempStatus)
            {
                case STATUS_BLOCKED:
                    status = STATUS_BLOCKED;
                    return STATUS_BLOCKED;
                case STATUS_DISABLE:
                    resultStatus = STATUS_DISABLE;
                    break;
                default:
                    continue;
            }
        }
        status = resultStatus != null ? resultStatus : STATUS_ENABLE;
        return status;
    }

    private void setStatus(int status)
    {
        List<AlfrescoAccount> accounts = AlfrescoAccountManager.retrieveAccounts(appContext);
        for (AlfrescoAccount account : accounts)
        {
            opt(appContext, status, account);
        }
    }


    @Deprecated
    public void enableManualDispatch(boolean enable)
    {
        //dispatchManually = enable;
    }
}
