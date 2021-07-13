/*
Copyright 2021 kino jin
zhikai.jin@bozhon.com
This file is part of next-sdk.
next-sdk is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.
next-sdk is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with next-sdk.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.lib.sdk.next.life;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.HashMap;
import java.util.Map;

/**
 * FileName: LifeManager
 * Author: zhikai.jin
 * Date: 2021/5/26 14:40
 * Description: 生命周期管理,使用ui界面，一定要加入生命周期管理
 */
public class LifeManager implements LifecycleListener {
    private static final String TAG = LifeManager.class.getSimpleName();

    private static final String FRAGMENT_TAG = "com.bozhon.lib.next.life";

    /**
     * 用于临时记录 FragmentManager - SupportRequestManagerFragment 的映射关系
     */
    final Map<android.app.FragmentManager, LifeManagerFragment> pendingRequestManagerFragments = new HashMap<>();


    @VisibleForTesting
    final Map<FragmentManager, SupportRequestManagerFragment> pendingSupportRequestManagerFragments = new HashMap<>();

    private static volatile LifeManager sInstance;

    private Activity mActivity;

    private LifecycleListener mLifecycleListener;


    public static LifeManager getInstance() {
        if (sInstance == null) {
            synchronized (LifeManager.class) {
                if (sInstance == null) {
                    sInstance = new LifeManager();
                }
            }
        }
        return sInstance;
    }

    @NonNull
    public void registerSelf(@NonNull Activity activity, LifecycleListener listener) {
        if (activity == null) {
            throw new IllegalArgumentException("You cannot start a load on a null activity");
        } else {
            this.mActivity = activity;
            this.mLifecycleListener = listener;
            get(activity);
        }
    }

    public void registerSelf(@NonNull Fragment fragment, LifecycleListener listener) {
        if (fragment == null) {
            throw new IllegalArgumentException("You cannot start a load on a null fragment");
        } else {
            this.mActivity = fragment.getActivity();
            this.mLifecycleListener = listener;
            get(fragment);
        }
    }


    @NonNull
    private void get(@NonNull Activity activity) {
        assertNotDestroyed(activity);
        android.app.FragmentManager fm = activity.getFragmentManager();
        LifeManagerFragment current = getRequestManagerFragment(fm, null);
        current.getAcFgLifecycle().addListener(this);
    }


    public void get(Fragment fragment) {
        FragmentManager fm = fragment.getChildFragmentManager();
        supportFragmentGet(fragment.getContext(), fm, fragment, fragment.isVisible());
    }


    private void supportFragmentGet(Context context, FragmentManager fm, Fragment parentHint, boolean isParentVisible) {

        SupportRequestManagerFragment current = getSupportRequestManagerFragment(fm, parentHint);

        LifeManager requestManager = current.getRequestManager();
        if (requestManager == null) {
            current.setRequestManager(this);
        }

        current.getActivityLifecycle().addListener(this);

    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void assertNotDestroyed(@NonNull Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }


    @SuppressWarnings("deprecation")
    @NonNull
    private LifeManagerFragment getRequestManagerFragment(@NonNull final android.app.FragmentManager fm, @Nullable android.app.Fragment parentHint) {
        LifeManagerFragment current = (LifeManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = pendingRequestManagerFragments.get(fm);
            if (current == null) {
                current = new LifeManagerFragment();
                current.setParentFragmentHint(parentHint);
                pendingRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            }
        }

        return current;
    }


    private static boolean isActivityVisible(Context context) {
        // This is a poor heuristic, but it's about all we have. We'd rather err on the side of visible
        // and start requests than on the side of invisible and ignore valid requests.
        Activity activity = findActivity(context);
        return activity == null || !activity.isFinishing();
    }

    @Nullable
    private static Activity findActivity(@NonNull Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        } else {
            return null;
        }
    }


    @Override
    public void onStart() {
        mLifecycleListener.onStart();
    }

    @Override
    public void onStop() {
        mLifecycleListener.onStop();
    }

    @Override
    public void onDestroy() {
        android.app.FragmentManager fm = mActivity.getFragmentManager();
        Object key = fm;
        Object removed = pendingRequestManagerFragments.remove(fm);
        if (removed == null && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, "Failed to remove expected request manager fragment, manager: " + key);
        }
        mLifecycleListener.onDestroy();

    }

    public LifeManagerFragment getRequestManagerFragment(Activity activity) {
        return getRequestManagerFragment(activity.getFragmentManager(), /*parentHint=*/ null);
    }

    @NonNull
    SupportRequestManagerFragment getSupportRequestManagerFragment(FragmentManager fragmentManager) {
        return getSupportRequestManagerFragment(fragmentManager, /*parentHint=*/ null);
    }

    @NonNull
    private SupportRequestManagerFragment getSupportRequestManagerFragment(
            @NonNull final FragmentManager fm, @Nullable Fragment parentHint) {
        SupportRequestManagerFragment current = (SupportRequestManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = pendingSupportRequestManagerFragments.get(fm);
            if (current == null) {
                current = new SupportRequestManagerFragment();
                current.setParentFragmentHint(parentHint);
                pendingSupportRequestManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            }
        }
        return current;
    }
}
