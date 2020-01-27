package org.alfresco.mobile.android.appcenter.analytics;

import android.app.Application;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

public class AppCenterAnalyticsManagerImpl  {

    // ///////////////////////////////////////////////////////////////////////////
    // Static Initialization
    // ///////////////////////////////////////////////////////////////////////////
    public static void  createAppCenterAnalyticsManagerImpl(final Application application,
                                                            final String appCenterIdKey)
    {
        AppCenter.start(application, appCenterIdKey, Analytics.class, Crashes.class);
    }
}
