package com.festival.dailypostermaker.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.festival.dailypostermaker.Api.ApiEndpoints;
import com.festival.dailypostermaker.Api.RetrofitClient;
import com.festival.dailypostermaker.BuildConfig;
import com.festival.dailypostermaker.Model.UpdatePersonalDetails.UpdatePersonalDetails;
import com.festival.dailypostermaker.MyUtils.MyUtil;
import com.festival.dailypostermaker.Preference.MyPreference;
import com.festival.dailypostermaker.R;
import com.festival.dailypostermaker.databinding.FragmentProfilePersonalBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentProfilePersonal extends Fragment {

    private String profilePath;
    private Dialog pleaseWaitDialog;
    private boolean isProfileChange = false;
    private int selectedMedia_P = 1;

    private FragmentProfilePersonalBinding binding;

    private ActivityResultLauncher<PickVisualMediaRequest> pickPhoto =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    startCropActivity(uri);
                }
            });

    public FragmentProfilePersonal() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfilePersonalBinding.inflate(inflater, container, false);

        pickPhoto = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                startCropActivity(uri);
            }
        });

        setupClickListeners();

        if (MyPreference.get_IsProfileAdded()) {

            binding.pEtName.setText(MyPreference.get_P_Name());
            binding.pEtMobileNo.setText(MyPreference.get_P_MobileNo());
            binding.pEtAbout.setText(MyPreference.get_P_About());

            if (MyPreference.get_P_SelectedSocialMedia() == 1) {
                binding.pTxtSocialMedia.setText(getResources().getString(R.string.instagram));
                binding.pEtSocialMedia.setHint(getResources().getString(R.string.instagram));
                binding.pEtSocialMedia.setText(MyPreference.get_P_Instagram());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_instagram);
                binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_P_SelectedSocialMedia() == 2) {
                binding.pTxtSocialMedia.setText(getResources().getString(R.string.youTube));
                binding.pEtSocialMedia.setHint(getResources().getString(R.string.youTube));
                binding.pEtSocialMedia.setText(MyPreference.get_P_YouTube());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_youtube);
                binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_P_SelectedSocialMedia() == 3) {
                binding.pTxtSocialMedia.setText(getResources().getString(R.string.facebook));
                binding.pEtSocialMedia.setHint(getResources().getString(R.string.facebook));
                binding.pEtSocialMedia.setText(MyPreference.get_P_Facebook());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_facebook);
                binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_P_SelectedSocialMedia() == 4) {
                binding.pTxtSocialMedia.setText(getResources().getString(R.string.twitter));
                binding.pEtSocialMedia.setHint(getResources().getString(R.string.twitter));
                binding.pEtSocialMedia.setText(MyPreference.get_P_Twitter());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_twitter);
                binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_P_SelectedSocialMedia() == 5) {
                binding.pTxtSocialMedia.setText(getResources().getString(R.string.email));
                binding.pEtSocialMedia.setHint(getResources().getString(R.string.email));
                binding.pEtSocialMedia.setText(MyPreference.get_P_Email());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_email);
                binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);

            } else if (MyPreference.get_P_SelectedSocialMedia() == 6) {
                binding.pTxtSocialMedia.setText(getResources().getString(R.string.website));
                binding.pEtSocialMedia.setHint(getResources().getString(R.string.website));
                binding.pEtSocialMedia.setText(MyPreference.get_P_Website());

                Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_website);
                binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
            }

            profilePath = MyPreference.get_P_ProfilePath();

            if (profilePath.isEmpty()) {
                binding.pProfileImg.setVisibility(View.GONE);
            } else {
                binding.pProfileImg.setVisibility(View.VISIBLE);
                Glide.with(requireContext())
                        .load(profilePath)
                        .placeholder(R.drawable.img_place_holder)
                        .error(R.drawable.img_place_holder)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(binding.pProfileImg);
            }
        }

        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.pLayChangeSocialMedia.setOnClickListener(view -> {
            dialogChangeSocialMedia();
        });

        binding.updateTxt.setOnClickListener(view -> {
            updateClick();
        });

        binding.pProfileLay.setOnClickListener(view -> {
            pickPhoto.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void dialogChangeSocialMedia() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(requireActivity(), R.style.CustomBottomSheetDialog);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_social_media, null);
        sheetDialog.setContentView(bottomSheetView);
        sheetDialog.show();

        LinearLayoutCompat d_SM_Instagram_Lay = sheetDialog.findViewById(R.id.d_SM_Instagram_Lay);
        LinearLayoutCompat d_SM_Youtube_Lay = sheetDialog.findViewById(R.id.d_SM_Youtube_Lay);
        LinearLayoutCompat d_SM_Facebook_Lay = sheetDialog.findViewById(R.id.d_SM_Facebook_Lay);
        LinearLayoutCompat d_SM_Twitter_Lay = sheetDialog.findViewById(R.id.d_SM_Twitter_Lay);
        LinearLayoutCompat d_SM_Email_Lay = sheetDialog.findViewById(R.id.d_SM_Email_Lay);
        LinearLayoutCompat d_SM_Website_Lay = sheetDialog.findViewById(R.id.d_SM_Website_Lay);

        d_SM_Instagram_Lay.setOnClickListener(view -> {
            selectedMedia_P = 1;
            sheetDialog.dismiss();
            binding.pTxtSocialMedia.setText(getResources().getString(R.string.instagram));
            binding.pEtSocialMedia.setHint(getResources().getString(R.string.instagram));
            binding.pEtSocialMedia.setText(MyPreference.get_P_Instagram());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_instagram);
            binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Youtube_Lay.setOnClickListener(view -> {
            selectedMedia_P = 2;
            sheetDialog.dismiss();
            binding.pTxtSocialMedia.setText(getResources().getString(R.string.youTube));
            binding.pEtSocialMedia.setHint(getResources().getString(R.string.youTube));
            binding.pEtSocialMedia.setText(MyPreference.get_P_YouTube());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_youtube);
            binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Facebook_Lay.setOnClickListener(view -> {
            selectedMedia_P = 3;
            sheetDialog.dismiss();
            binding.pTxtSocialMedia.setText(getResources().getString(R.string.facebook));
            binding.pEtSocialMedia.setHint(getResources().getString(R.string.facebook));
            binding.pEtSocialMedia.setText(MyPreference.get_P_Facebook());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_facebook);
            binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Twitter_Lay.setOnClickListener(view -> {
            selectedMedia_P = 4;
            sheetDialog.dismiss();
            binding.pTxtSocialMedia.setText(getResources().getString(R.string.twitter));
            binding.pEtSocialMedia.setHint(getResources().getString(R.string.twitter));
            binding.pEtSocialMedia.setText(MyPreference.get_P_Twitter());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_twitter);
            binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Email_Lay.setOnClickListener(view -> {
            selectedMedia_P = 5;
            sheetDialog.dismiss();
            binding.pTxtSocialMedia.setText(getResources().getString(R.string.email));
            binding.pEtSocialMedia.setHint(getResources().getString(R.string.email));
            binding.pEtSocialMedia.setText(MyPreference.get_P_Email());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_email);
            binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });

        d_SM_Website_Lay.setOnClickListener(view -> {
            selectedMedia_P = 6;
            sheetDialog.dismiss();
            binding.pTxtSocialMedia.setText(getResources().getString(R.string.website));
            binding.pEtSocialMedia.setHint(getResources().getString(R.string.website));
            binding.pEtSocialMedia.setText(MyPreference.get_P_Website());

            Drawable drawableStart = ContextCompat.getDrawable(requireContext(), R.drawable.icon_edit_profile_website);
            binding.pEtSocialMedia.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, null, null, null);
        });
    }

    private void updateClick() {
        if (cheakValidation()) {
            showPleaseWaitDialog();

            MyPreference.set_P_SelectedSocialMedia(selectedMedia_P);

            ApiEndpoints apiEndpoints = RetrofitClient.getInstance().create(ApiEndpoints.class);

            if (MyPreference.get_P_SelectedSocialMedia() == 1) {
                MyPreference.set_P_Instagram(binding.pEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_P_SelectedSocialMedia() == 2) {
                MyPreference.set_P_YouTube(binding.pEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_P_SelectedSocialMedia() == 3) {
                MyPreference.set_P_Facebook(binding.pEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_P_SelectedSocialMedia() == 4) {
                MyPreference.set_P_Twitter(binding.pEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_P_SelectedSocialMedia() == 5) {
                MyPreference.set_P_Email(binding.pEtSocialMedia.getText().toString().trim());

            } else if (MyPreference.get_P_SelectedSocialMedia() == 6) {
                MyPreference.set_P_Website(binding.pEtSocialMedia.getText().toString().trim());
            }

            RequestBody versionCode = RequestBody.create(String.valueOf(BuildConfig.VERSION_CODE), MediaType.parse("text/plain"));
            RequestBody deviceId = RequestBody.create(MyUtil.device_androidId, MediaType.parse("text/plain"));
            RequestBody name = RequestBody.create(binding.pEtName.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody mobile = RequestBody.create(binding.pEtMobileNo.getText().toString().trim(), MediaType.parse("text/plain"));
            RequestBody aboutUs = RequestBody.create(binding.pEtAbout.getText().toString().trim(), MediaType.parse("text/plain"));

            RequestBody instagram = RequestBody.create(MyPreference.get_P_Instagram(), MediaType.parse("text/plain"));
            RequestBody email = RequestBody.create(MyPreference.get_P_Email(), MediaType.parse("text/plain"));
            RequestBody youtube = RequestBody.create(MyPreference.get_P_YouTube(), MediaType.parse("text/plain"));
            RequestBody facebook = RequestBody.create(MyPreference.get_P_Facebook(), MediaType.parse("text/plain"));
            RequestBody twitter = RequestBody.create(MyPreference.get_P_Twitter(), MediaType.parse("text/plain"));
            RequestBody website = RequestBody.create(MyPreference.get_P_Website(), MediaType.parse("text/plain"));

            RequestBody isLogoChangs;
            MultipartBody.Part profileImage = null;

            if (isProfileChange) {
                File file = new File(profilePath);
                RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
                profileImage = MultipartBody.Part.createFormData("profile", file.getName(), requestFile);
                isLogoChangs = RequestBody.create("1", MediaType.parse("text/plain"));
            } else {
                isLogoChangs = RequestBody.create("0", MediaType.parse("text/plain"));
            }

            Call<UpdatePersonalDetails> call = apiEndpoints.updateUserProfile(
                    versionCode, deviceId, profileImage, name, mobile, email, aboutUs, instagram, youtube, facebook, twitter, website, isLogoChangs);

            call.enqueue(new Callback<UpdatePersonalDetails>() {
                @Override
                public void onResponse(@NonNull Call<UpdatePersonalDetails> call, @NonNull Response<UpdatePersonalDetails> response) {
                    dismissPleaseWaitDialog();
                    if (response.isSuccessful()) {
                        if (response.body().getCode() == 2) {
                            savePersonalDetails();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<UpdatePersonalDetails> call, @NonNull Throwable t) {
                    dismissPleaseWaitDialog();
                    Log.d("TAG", "onFailure: " + t.getMessage());
                }
            });
        }
    }

    private void savePersonalDetails() {
        MyPreference.set_P_Name(binding.pEtName.getText().toString().trim());
        MyPreference.set_P_MobileNo(binding.pEtMobileNo.getText().toString().trim());
        MyPreference.set_P_About(binding.pEtAbout.getText().toString().trim());

        MyPreference.set_P_ProfilePath(profilePath);
        MyPreference.set_IsProfileAdded(true);

        if (MyPreference.get_IsProfileBusiness()) {
            MyUtil.isAlternative = true;
            MyUtil.isUpdate = false;
            MyPreference.set_IsProfileBusiness(false);
        } else {
            MyUtil.isAlternative = false;
            MyUtil.isUpdate = true;
        }

        Toast.makeText(requireContext(), "Update Successfully", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }

    private boolean cheakValidation() {
        if (TextUtils.isEmpty(binding.pEtName.getText().toString().trim())) {
            binding.pEtName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.pEtMobileNo.getText().toString().trim())) {
            binding.pEtMobileNo.requestFocus();
            return false;
        }
        if (binding.pEtMobileNo.getText().toString().trim().length() < 10) {
            binding.pEtMobileNo.setError("Enter Valid Mobile No");
            binding.pEtMobileNo.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(binding.pEtAbout.getText().toString().trim())) {
            binding.pEtAbout.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK && data != null) {
            Uri croppedImageUri = UCrop.getOutput(data);
            handleCrop(MyUtil.getPathFromURI(requireContext(), croppedImageUri));
        }
    }

    private void startCropActivity(Uri imageUri) {
        UCrop.Options options = new UCrop.Options();
        UCrop.of(imageUri, Uri.fromFile(new File(requireContext().getCacheDir(), "SampleCropImage" + System.currentTimeMillis() + ".png")))
                .withAspectRatio(1, 1)
                .withOptions(options)
                .start(requireContext(), this);
    }

    private void handleCrop(String imageFilePath) {
        try {
            File newfile = new File(MyUtil.getAppFolder(requireContext()) + "profile");
            if (!newfile.exists()) {
                newfile.mkdir();
                newfile.mkdirs();
            }
            MyUtil.cropFileMove(requireContext(), new File(imageFilePath), newfile);

            profilePath = newfile.getAbsolutePath() + "/" + new File(imageFilePath).getName();
            Glide.with(requireContext())
                    .load(profilePath)
                    .placeholder(R.drawable.img_place_holder)
                    .error(R.drawable.img_place_holder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(binding.pProfileImg);

            binding.pProfileImg.setVisibility(View.VISIBLE);
            isProfileChange = true;
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showPleaseWaitDialog() {
        try {
            pleaseWaitDialog = new Dialog(requireActivity());
            pleaseWaitDialog.setContentView(R.layout.dialog_please_wait);
            if (pleaseWaitDialog.getWindow() != null) {
                pleaseWaitDialog.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                pleaseWaitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            pleaseWaitDialog.setCanceledOnTouchOutside(false);
            pleaseWaitDialog.setCancelable(false);

            if (!requireActivity().isFinishing() && !requireActivity().isDestroyed() && pleaseWaitDialog != null) {
                pleaseWaitDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissPleaseWaitDialog() {
        if (!requireActivity().isFinishing() && !requireActivity().isDestroyed() && pleaseWaitDialog != null && pleaseWaitDialog.isShowing()) {
            pleaseWaitDialog.dismiss();
        }
    }
}