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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.HashSet;
import java.util.Set;

/**
 * FileName: SupportRequestManagerFragment
 * Author: zhikai.jin
 * Date: 2021/7/2 14:52
 * Description:
 */
public class SupportRequestManagerFragment extends Fragment{

    // 生命周期回调
    private final ActivityFragmentLifecycle lifecycle;


    private final Set<SupportRequestManagerFragment> childRequestManagerFragments = new HashSet<>();

    @Nullable
    private SupportRequestManagerFragment rootRequestManagerFragment;
    @Nullable
    private LifeManager requestManager;
    @Nullable
    private Fragment parentFragmentHint;


    public SupportRequestManagerFragment() {
        this(new ActivityFragmentLifecycle());
    }

    @VisibleForTesting
    @SuppressLint("ValidFragment")
    public SupportRequestManagerFragment(@NonNull ActivityFragmentLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void setRequestManager(@Nullable LifeManager requestManager) {
        this.requestManager = requestManager;
    }

    @Nullable
    public LifeManager getRequestManager() {
        return requestManager;
    }

    private void addChildRequestManagerFragment(SupportRequestManagerFragment child) {
        childRequestManagerFragments.add(child);
    }


    private void removeChildRequestManagerFragment(SupportRequestManagerFragment child) {
        childRequestManagerFragments.remove(child);
    }




    @Override
    public void onStart() {
        super.onStart();
        lifecycle.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        lifecycle.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lifecycle.onDestroy();
        unregisterFragmentWithRoot();
    }

    @Override
    public void onAttach(@NonNull  Context context) {
        super.onAttach(context);
        FragmentManager rootFragmentManager = getRootFragmentManager(this);
        try {
            registerFragmentWithRoot(getContext(), rootFragmentManager);
        } catch (IllegalStateException e) {
            // OnAttach can be called after the activity is destroyed, see #497.
        }
    }


    @Nullable
    private static FragmentManager getRootFragmentManager(@NonNull Fragment fragment) {
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment.getFragmentManager();
    }

    @Nullable
    private Fragment getParentFragmentUsingHint() {
        Fragment fragment = getParentFragment();
        return fragment != null ? fragment : parentFragmentHint;
    }

    @NonNull
    ActivityFragmentLifecycle getActivityLifecycle() {
        return lifecycle;
    }

    private void registerFragmentWithRoot(
            @NonNull Context context, @NonNull FragmentManager fragmentManager) {
        unregisterFragmentWithRoot();
        rootRequestManagerFragment = LifeManager.getInstance().getSupportRequestManagerFragment(fragmentManager);
        if (!equals(rootRequestManagerFragment)) {
            rootRequestManagerFragment.addChildRequestManagerFragment(this);
        }
    }

    private void unregisterFragmentWithRoot() {
        if (rootRequestManagerFragment != null) {
            rootRequestManagerFragment.removeChildRequestManagerFragment(this);
            rootRequestManagerFragment = null;
        }
    }
    /**
     * Sets a hint for which fragment is our parent which allows the fragment to return correct
     * information about its parents before pending fragment transactions have been executed.
     */
    void setParentFragmentHint(@Nullable Fragment parentFragmentHint) {
        this.parentFragmentHint = parentFragmentHint;
        if (parentFragmentHint == null || parentFragmentHint.getContext() == null) {
            return;
        }
        FragmentManager rootFragmentManager = getRootFragmentManager(parentFragmentHint);
        if (rootFragmentManager == null) {
            return;
        }
        registerFragmentWithRoot(parentFragmentHint.getContext(), rootFragmentManager);
    }

}
