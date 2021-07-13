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
import android.app.Fragment;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.bozh.logger.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * FileName: LifeManagerFragment
 * Author: zhikai.jin
 * Date: 2021/5/26 11:31
 * Description:
 */
public class LifeManagerFragment extends Fragment {


    private final ActivityFragmentLifecycle lifecycle;

    @Nullable
    private LifeManagerFragment rootLifeManagerFragment;

    @Nullable
    private Fragment parentFragmentHint;


    @SuppressWarnings("deprecation")
    private final Set<LifeManagerFragment> childRequestManagerFragments = new HashSet<>();


    public LifeManagerFragment() {
        this(new ActivityFragmentLifecycle());
    }


    public LifeManagerFragment(ActivityFragmentLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    private void registerFragmentWithRoot(@NonNull Activity activity) {
        unregisterFragmentWithRoot();
        rootLifeManagerFragment = LifeManager.getInstance().getRequestManagerFragment(activity);
        if (!equals(rootLifeManagerFragment)) {
            rootLifeManagerFragment.addChildRequestManagerFragment(this);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            registerFragmentWithRoot(activity);
        } catch (IllegalStateException e) {
            // OnAttach can be called after the activity is destroyed, see #497.
                Logger.w ("Unable to register fragment with root", e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        unregisterFragmentWithRoot();
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
    private void unregisterFragmentWithRoot() {
        if (rootLifeManagerFragment != null) {
            rootLifeManagerFragment.removeChildRequestManagerFragment(this);
            rootLifeManagerFragment = null;
        }
    }

    @SuppressWarnings("deprecation")
    private void removeChildRequestManagerFragment(LifeManagerFragment child) {
        childRequestManagerFragments.remove(child);
    }


    @NonNull
    ActivityFragmentLifecycle getAcFgLifecycle() {
        return lifecycle;
    }

    @SuppressWarnings("deprecation")
    private void addChildRequestManagerFragment(LifeManagerFragment child) {
        childRequestManagerFragments.add(child);
    }
    /**
     * Sets a hint for which fragment is our parent which allows the fragment to return correct
     * information about its parents before pending fragment transactions have been executed.
     */
    void setParentFragmentHint(@Nullable Fragment parentFragmentHint) {
        this.parentFragmentHint = parentFragmentHint;
        if (parentFragmentHint != null && parentFragmentHint.getActivity() != null) {
            registerFragmentWithRoot(parentFragmentHint.getActivity());
        }
    }

    @Nullable
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Fragment getParentFragmentUsingHint() {
        final Fragment fragment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            fragment = getParentFragment();
        } else {
            fragment = null;
        }
        return fragment != null ? fragment : parentFragmentHint;
    }

    @Override
    public String toString() {
        return super.toString() + "{parent=" + getParentFragmentUsingHint() + "}";
    }

}
