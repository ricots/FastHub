package com.fastaccess.ui.modules.profile.overview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.UserModel;
import com.fastaccess.helper.BundleConstant;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.profile.ProfilePagerMvp;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.SpannableBuilder;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

/**
 * Created by Kosh on 03 Dec 2016, 9:16 AM
 */

public class ProfileOverviewView extends BaseFragment<ProfileOverviewMvp.View, ProfileOverviewPresenter> implements ProfileOverviewMvp.View {

    @BindView(R.id.username) FontTextView username;
    @BindView(R.id.description) FontTextView description;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.organization) FontTextView organization;
    @BindView(R.id.location) FontTextView location;
    @BindView(R.id.email) FontTextView email;
    @BindView(R.id.link) FontTextView link;
    @BindView(R.id.joined) FontTextView joined;
    @BindView(R.id.following) FontTextView following;
    @BindView(R.id.followers) FontTextView followers;
    @BindView(R.id.progress) View progress;

    @State UserModel userModel;

    private ProfilePagerMvp.View profileCallback;

    public static ProfileOverviewView newInstance(String login) {
        ProfileOverviewView view = new ProfileOverviewView();
        view.setArguments(Bundler.start().put(BundleConstant.EXTRA, login).end());
        return view;
    }

    @OnClick({R.id.following, R.id.followers}) void onClick(View view) {
        if (view.getId() == R.id.followers) {
            profileCallback.onNavigateToFollowers();
        } else {
            profileCallback.onNavigateToFollowing();
        }
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof ProfilePagerMvp.View) {
            profileCallback = (ProfilePagerMvp.View) getParentFragment();
        } else {
            profileCallback = (ProfilePagerMvp.View) context;
        }
    }

    @Override public void onDetach() {
        profileCallback = null;
        super.onDetach();
    }

    @Override protected int fragmentLayout() {
        return R.layout.profile_overview_layout;
    }

    @Override protected void onFragmentCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getPresenter().onFragmentCreated(getArguments());
        } else {
            if (userModel != null) onInitViews(userModel);
            else getPresenter().onFragmentCreated(getArguments());
        }
    }

    @NonNull @Override public ProfileOverviewPresenter providePresenter() {
        return new ProfileOverviewPresenter();
    }

    @Override public void onInitViews(@Nullable UserModel userModel) {
        progress.setVisibility(View.GONE);
        if (userModel == null) return;
        this.userModel = userModel;
        username.setText(userModel.getLogin());
        description.setText(userModel.getBio());
        avatarLayout.setUrl(userModel.getAvatarUrl(), null);
        organization.setText(InputHelper.toNA(userModel.getCompany()));
        location.setText(InputHelper.toNA(userModel.getLocation()));
        email.setText(InputHelper.toNA(userModel.getEmail()));
        link.setText(InputHelper.toNA(userModel.getBlog()));
        joined.setText(userModel.getCreatedAt() != null ? ParseDateFormat.getTimeAgo(userModel.getCreatedAt()) : "N/A");
        followers.setText(SpannableBuilder.builder()
                .append(getString(R.string.followers))
                .append("\n")
                .bold(String.valueOf(userModel.getFollowers())));
        following.setText(SpannableBuilder.builder()
                .append(getString(R.string.following))
                .append("\n")
                .bold(String.valueOf(userModel.getFollowing())));
    }

    @Override public void showProgress(@StringRes int resId) {
        progress.setVisibility(View.VISIBLE);
    }

    @Override public void hideProgress() {
        progress.setVisibility(View.GONE);
    }

    @Override public void showErrorMessage(@NonNull String msgRes) {
        hideProgress();
        super.showErrorMessage(msgRes);
    }
}
